package com.richfit.mes.produce.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.util.StringUtil;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.MaterialInspectionServiceClient;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.WordUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 理化检验接口
 */
@Service
@Slf4j
public class PhyChemTestService{


    @Autowired
    private PhysChemOrderService physChemOrderService;
    @Autowired
    private PhysChemResultService physChemResultService;
    @Autowired
    private WordUtil wordUtil;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private MaterialInspectionServiceClient materialInspectionServiceClient;
    //同步状态
    private final static String SYNC_STATUS = "1";
    //未同步状态
    private final static String NO_SYNC_STATUS = "0";
    //委托单拒绝
    private final static String BACK_STATUS = "3";
    //委托单确认
    private final static String YES_STATUS = "2";
    //委托单待确认
    private final static String GOING_STATUS = "1";
    //材料检测部门生成报告
    private final static String YES_REPORT_STATUS = "1";
    //材料检测部门未生成报告
    private final static String NO_REPORT_STATUS = "0";


    /**
     * 查询委托单列表
     * @param phyChemTaskVo
     * @return
     */
    public IPage<PhysChemOrderInner> page(PhyChemTaskVo phyChemTaskVo) {
        return materialInspectionServiceClient.page(phyChemTaskVo);
    }

    /**
     * 保存委托单
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult saveOrder(List<PhysChemOrderInner> physChemOrderInners) throws Exception {
        //获取第一个用于校验数据使用
        PhysChemOrderInner physChemOrderInner = physChemOrderInners.get(0);
        //根据委托组id查询中间表的数据
        List<PhysChemOrderInner> inners = materialInspectionServiceClient.queryByGroupId(StringUtils.isNullOrEmpty(physChemOrderInner.getGroupId())?"":physChemOrderInner.getGroupId());

        //校验是否能修改
        if(inners.size()>0){
            String status = inners.get(0).getStatus();
            if(YES_STATUS.equals(status)){
                return CommonResult.failed("材料实验室已经确认委托，无法被修改");
            }
            //校验通过删除之前的数据
            materialInspectionServiceClient.deleteByGroupId(physChemOrderInner.getGroupId());
        }
        //inners有数据说明是修改  没数据说明是新增 新增并委托需要保存委托单号和报告号
        if(physChemOrderInner.getStatus().equals("1")){
            if((inners.size()>0 && StringUtils.isNullOrEmpty(inners.get(0).getOrderNo())) || inners.size()==0){
                //保存委托单号
                Code.update("order_no",physChemOrderInner.getOrderNo(),SecurityUtils.getCurrentUser().getTenantId(), physChemOrderInner.getBranchCode(),codeRuleService);
                //保存报告号
                Code.update("m_report_no",physChemOrderInner.getReportNo(),SecurityUtils.getCurrentUser().getTenantId(), physChemOrderInner.getBranchCode(),codeRuleService);
            }
        }
        //插入新的数据
        String newGroupId = UUID.randomUUID().toString().replaceAll("-", "");
        for (PhysChemOrderInner chemOrderInner : physChemOrderInners) {
            //质检发起委托操作
            //设置委托单、报告未生成、实验数据未同步
            chemOrderInner.setSyncStatus(NO_SYNC_STATUS);
            chemOrderInner.setReportStatus(NO_REPORT_STATUS);
            //委托人
            chemOrderInner.setConsignor(SecurityUtils.getCurrentUser().getUserId());
            chemOrderInner.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            //委托组id
            chemOrderInner.setGroupId(newGroupId);
        }
        //保存委托单到中间表
        materialInspectionServiceClient.saveOrder(physChemOrderInners);
        return CommonResult.success(true);
    }

    /**
     * 委托单保存校验
     * @param physChemOrderInner
     */
    public void checkOrderInfo(PhysChemOrderInner physChemOrderInner){
        StringBuilder waringStr = new StringBuilder();
        if(StringUtils.isNullOrEmpty(physChemOrderInner.getDrawNo()) && StringUtils.isNullOrEmpty(physChemOrderInner.getAccepStandard())){
            waringStr.append("″样品/零件图号″和″验收标准″二者必需填写一项");
        }
        //拉伸校验
        if(1 == physChemOrderInner.getForceTensile()){
            if(StringUtil.isEmpty(physChemOrderInner.getForceTensileStrength1())
                    ||StringUtil.isEmpty(physChemOrderInner.getForceTensileElongation())
                    ||StringUtil.isEmpty(physChemOrderInner.getForceTensileDirection())){
                if(!StringUtil.isEmpty(String.valueOf(waringStr))){
                    waringStr.append(";");
                }
                waringStr.append("选中拉伸试验时, ″屈服强度1″、″伸长率″、″试样方向″为必填");
            }
        }
        //冲击校验
        if(1 == physChemOrderInner.getForceImpact()){
            List<PhysChemOrderImpactDto> impacts = physChemOrderInner.getImpacts();
            if(ObjectUtil.isEmpty(impacts) || impacts.size()==0){
                if(!StringUtil.isEmpty(String.valueOf(waringStr))){
                    waringStr.append(";");
                }
                waringStr.append("选中冲击试验时, ″实验温度″、″缺口类型″、″试样方向″为必填");
            }
            for (PhysChemOrderImpactDto impact : impacts) {
                if(StringUtil.isEmpty(impact.getForceImpactTemp())
                        ||StringUtil.isEmpty(impact.getForceImpactGap())
                        ||StringUtil.isEmpty(impact.getForceImpactDirection())){
                    if(!StringUtil.isEmpty(String.valueOf(waringStr))){
                        waringStr.append(";");
                    }
                    waringStr.append("选中冲击试验时, ″实验温度″、″缺口类型″、″试样方向″为必填");
                    break;
                }
            }
        }
        if(!StringUtil.isEmpty(String.valueOf(waringStr))){
            throw new GlobalException(String.valueOf(waringStr),ResultCode.FAILED);
        }
    }

    /**
     * 修改委托单状态
     * @param status
     * @param id
     */
    public boolean changeOrderStaus(String status,String id){
        PhysChemOrder physChemOrder = new PhysChemOrder();
        physChemOrder.setId(id);
        physChemOrder.setStatus(status);
        return physChemOrderService.updateById(physChemOrder);
    }

    /**
     * 修改委托单状态
     * @param jsonObject
     */
    public boolean changeOrderStatus(JSONObject jsonObject) throws Exception {
        //要委托的委托单单号集合
        List<String> groupIds = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("groupIds")), String.class);
        //组织机构
        String branchCode = jsonObject.getString("branchCode");
        //定义要修改的委托单集合
        List<PhysChemOrderInner> updateInfos = new ArrayList<>();
        List<PhysChemOrderInner> innerListByOrders = materialInspectionServiceClient.getInnerListByGroupIds(groupIds);
        //根据委托组id分组
        Map<String, List<PhysChemOrderInner>> groups = innerListByOrders.stream().collect(Collectors.groupingBy(PhysChemOrderInner::getGroupId));
        //修改委托单号、报告号、状态
        for (List<PhysChemOrderInner> value : groups.values()) {
            if(StringUtils.isNullOrEmpty(value.get(0).getOrderNo())){
                String orderNo = Code.valueOnUpdate("order_no", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
                String reportNo = Code.valueOnUpdate("m_report_no", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
                for (PhysChemOrderInner physChemOrderInner : value) {
                    physChemOrderInner.setOrderNo(orderNo);
                    physChemOrderInner.setReportNo(reportNo);
                    physChemOrderInner.setStatus("1");
                    updateInfos.add(physChemOrderInner);
                }
            }else{
                for (PhysChemOrderInner physChemOrderInner : value) {
                    physChemOrderInner.setStatus("1");
                    updateInfos.add(physChemOrderInner);
                }
            }

        }
        return materialInspectionServiceClient.changeOrderStatus(updateInfos);
    }

    /**
     * 同步试验结果
     * @param reportNos
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> syncResult(List<String> reportNos) {
        //中间表数据
        List<PhysChemOrderInner> physChemOrderInners = materialInspectionServiceClient.synResultInfos(reportNos);

        //校验是否能执行同步操作
        List<PhysChemOrderInner> checkList = physChemOrderInners.stream().filter(order ->
                //非确认委托状态
                !YES_STATUS.equals(order.getStatus())
                        //未生成报告的
                        || NO_REPORT_STATUS.equals(order.getReportStatus())).collect(Collectors.toList());
        List<PhysChemOrderInner> checkList2 = physChemOrderInners.stream().filter(order ->
                //同步成功的数据
                SYNC_STATUS.equals(order.getSyncStatus())).collect(Collectors.toList());
        if (checkList.size()>0) {
            return CommonResult.failed("请确认所选数据'确认委托并且已生成报告数据'");
        }
        if(checkList2.size()>0){
            return CommonResult.failed("所选数据有已同步的数据");
        }

        if(reportNos.size()>0){
            //key->报告号   value—>实验结果
            Map<String, List<PhysChemOrderInner>> results = physChemOrderInners.stream().collect(Collectors.groupingBy(PhysChemOrderInner::getReportNo));

            //保存的试验结果数据
            List<PhysChemResult> physChemResults = new ArrayList<>();

            results.forEach((key,value)->{
                //实验数据
                for (PhysChemOrderInner physChemOrderInner : value) {
                    PhysChemResult physChemResult = new PhysChemResult();
                    BeanUtil.copyProperties(physChemOrderInner,physChemResult);
                    physChemResults.add(physChemResult);
                }
            });
            //保存同步的数据
            physChemResultService.saveBatch(physChemResults);

            //修改委托单同步接口状态"已同步"
            for (String reportNo : reportNos) {
                materialInspectionServiceClient.changeOrderSyncSatus(reportNo,SYNC_STATUS);
            }

        }

        return CommonResult.success(true);
    }

    /**
     * 理化检测报告导出
     * @param response
     * @throws IOException
     * @throws TemplateException
     */
    public void exoprtReport(HttpServletResponse response,String reportNo) throws IOException, TemplateException {
        //中间表数据 用于生成报告
        List<PhysChemOrderInner> physChemOrderInners = materialInspectionServiceClient.queryByReportNo(reportNo);
        //根据炉号排序
        physChemOrderInners.sort((t1,t2)->t1.getBatchNo().compareTo(t2.getBatchNo()));
        //相同报告的委托单信息大体一致（除了冲击试验数据） 所以委托单信息取第一条就好
        PhysChemOrderInner physChemOrderInner = physChemOrderInners.get(0);
        //冲击参数
        List<PhysChemOrderInner> cjs = new ArrayList<>();

        //中间表数据 用于生成报告
        List<PhysChemOrderInner> allPhysChemOrderInners = materialInspectionServiceClient.queryByOrderNo(physChemOrderInner.getOrderNo());
        if(allPhysChemOrderInners.size()>0){
            Map<String, List<PhysChemOrderInner>> batchNoGroup = allPhysChemOrderInners.stream().collect(Collectors.groupingBy(PhysChemOrderInner::getBatchNo));
            physChemOrderInner = new ArrayList<>(batchNoGroup.values()).get(0).get(0);
            //冲击参数赋值
            cjs = new ArrayList<>(batchNoGroup.values()).get(0);
            //炉号赋值
            physChemOrderInner.setBatchNo(org.apache.commons.lang.StringUtils.join(batchNoGroup.keySet(),","));
        }
        //构造填充数据
        Map<String, Object> dataMap = new HashMap<>();
        //1、报告头信息
        dataMap.put("sampleDept",physChemOrderInner.getSampleDept());
        dataMap.put("productName",physChemOrderInner.getProductName());
        dataMap.put("drawNo",physChemOrderInner.getDrawNo());
        dataMap.put("materialMark",physChemOrderInner.getMaterialMark());
        dataMap.put("reportYear",physChemOrderInner.getReportYear());
        dataMap.put("reportMonth",physChemOrderInner.getReportMonth());
        dataMap.put("reportDay",physChemOrderInner.getReportDay());
        dataMap.put("reportNo",physChemOrderInner.getReportNo());
        //报告列表表头信息
        dataMap.put("tensileAdditionalNo",physChemOrderInner.getTensileAdditionalNo());    //拉伸试验序号             9
        dataMap.put("forceTensileDirection",physChemOrderInner.getForceTensileDirection()); //拉伸试验方向            15
        dataMap.put("impactAdditionalNo",physChemOrderInner.getImpactAdditionalNo()); //冲击附加序号                  10
        dataMap.put("forceImpactDirection",physChemOrderInner.getForceImpactDirection()); //力学性能冲击试验方向       16
        dataMap.put("othertestName",physChemOrderInner.getOthertestName()); //弯压硬低应剪名称                         26
        dataMap.put("othertestAdditionalNo",physChemOrderInner.getOthertestAdditionalNo()); //弯压硬低应剪附加序号             11
        dataMap.put("othertestDirection",physChemOrderInner.getOthertestDirection()); //弯压硬低应剪方向                     17
        dataMap.put("othertestParameter1",physChemOrderInner.getOthertestParameter1()); //弯压硬低应剪参数1            28
        dataMap.put("othertestParameter2",physChemOrderInner.getOthertestParameter2()); //弯压硬低应剪参数2            27
        dataMap.put("chemicalAdditionalNo",physChemOrderInner.getChemicalAdditionalNo()); //化学附加序号           12
        dataMap.put("forceTensileStrength1",physChemOrderInner.getForceTensileStrength1()); //力学性能->拉伸->屈服强度1        23
        dataMap.put("ForceTensileStrength2",physChemOrderInner.getForceTensileStrength2()); //力学性能->拉伸->屈服强度2    23
        dataMap.put("forceTensileElongation",physChemOrderInner.getForceTensileElongation()); //力学性能->拉伸->伸长率          24
        dataMap.put("impactParameter",physChemOrderInner.getImpactParameter()); //冲击试验参数         25
        dataMap.put("cSpectralline",physChemOrderInner.getCSpectralline()); //碳光谱线          29
        dataMap.put("sSpectralline",physChemOrderInner.getSSpectralline()); //硫光谱线         30
        dataMap.put("reportRemarkName",physChemOrderInner.getReportRemarkName()); //备注名称         31
        dataMap.put("reportRemarkUnit",physChemOrderInner.getReportRemarkUnit()); //备注单位         32
        //2、报告列表参数
        dataMap.put("resultList",physChemOrderInners);
        //空行
        List<String> nullCells = new ArrayList<>();
        for(int i=0;i<8-physChemOrderInners.size();i++){
            nullCells.add("1");
        }
        dataMap.put("nullCells",nullCells);
        //3、报告列表大框参数
        //金相结果集合
        List<PhysChemOrderInner> list = physChemOrderInners.stream().filter(item -> !StringUtils.isNullOrEmpty(item.getMetallName())).collect(Collectors.toList());
        List<Map<String, String>> mapsList = new ArrayList<>();
        for (PhysChemOrderInner chemOrderInner : list) {
            Map<String, String> addMap = new HashMap<>();
            addMap.put("metallName",chemOrderInner.getMetallName());
            addMap.put("metallAdditionalNo",chemOrderInner.getMetallAdditionalNo());
            addMap.put("resultsMetal",chemOrderInner.getResultsMetal());
            mapsList.add(addMap);
        }
        dataMap.put("mapsList",mapsList);
        //附加数据集合
        List<String> jxList = new ArrayList<>();  //金相附加
        List<String> hxList = new ArrayList<>();  //化学附加
        List<String> wyList = new ArrayList<>();  //弯曲附加
        List<String> cjList = new ArrayList<>();  //冲击附加
        List<String> lsList = new ArrayList<>();  //拉伸附加
        for (PhysChemOrderInner chemOrderInner : physChemOrderInners) {
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getMetallAdditional())){
                jxList.add(chemOrderInner.getMetallAdditional());
            }
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getChemicalAdditional())){
                hxList.add(chemOrderInner.getChemicalAdditional());
            }
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getOthertestAdditional())){
                wyList.add(chemOrderInner.getOthertestAdditional());
            }
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getImpactAdditional())){
                cjList.add(chemOrderInner.getImpactAdditional());
            }
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getTensileAdditional())){
                lsList.add(chemOrderInner.getTensileAdditional());
            }
        }
        dataMap.put("jxList",jxList);
        dataMap.put("hxList",hxList);
        dataMap.put("wyList",wyList);
        dataMap.put("cjList",cjList);
        dataMap.put("lsList",lsList);
        //报告底层人的信息
        Set<String> tensileBys = physChemOrderInners.stream().filter(item->!StringUtils.isNullOrEmpty(item.getTensileTester())).collect(Collectors.toList())
                .stream().map(PhysChemOrderInner::getTensileTester).collect(Collectors.toSet());
        Set<String> impactTesters = physChemOrderInners.stream().filter(item->!StringUtils.isNullOrEmpty(item.getImpactTester())).collect(Collectors.toList())
                .stream().map(PhysChemOrderInner::getImpactTester).collect(Collectors.toSet());
        Set<String> othertestTesters = physChemOrderInners.stream().filter(item->!StringUtils.isNullOrEmpty(item.getOthertestTester())).collect(Collectors.toList())
                .stream().map(PhysChemOrderInner::getOthertestTester).collect(Collectors.toSet());
        Set<String> chemicalTesters = physChemOrderInners.stream().filter(item->!StringUtils.isNullOrEmpty(item.getChemicalTester())).collect(Collectors.toList())
                .stream().map(PhysChemOrderInner::getChemicalTester).collect(Collectors.toSet());
        Set<String> metallTester = physChemOrderInners.stream().filter(item->!StringUtils.isNullOrEmpty(item.getMetallTester())).collect(Collectors.toList())
                .stream().map(PhysChemOrderInner::getMetallTester).collect(Collectors.toSet());
        dataMap.put("supervidor",physChemOrderInner.getSupervidor());
        dataMap.put("reviewedBy",physChemOrderInner.getReviewedBy());
        String join = String.join(" ", tensileBys)+" "+String.join(" ", impactTesters)+" "+String.join(" ", othertestTesters)+" "+String.join(" ", chemicalTesters)+" "+String.join(" ", metallTester);
        dataMap.put("checkBy",join);


        //导出
        wordUtil.exoprtReport(response,dataMap,"lhjcTemp.ftl","理化检测报告");
    }

    /**
     * 构造模板参数
     * @param dataMap
     */
    private void createDataMap(Map<String, Object> dataMap){


    }

    //导出理化委托单
    public void exportExcel(HttpServletResponse rsp,String orderNo) {
        PhysChemOrderInner physChemOrderInner = new PhysChemOrderInner();
        List<PhysChemOrderInner> physChemOrderInners = new ArrayList<>();
        //中间表数据 用于生成报告
        List<PhysChemOrderInner> allPhysChemOrderInners = materialInspectionServiceClient.queryByOrderNo(orderNo);
        if(allPhysChemOrderInners.size()>0){
            Map<String, List<PhysChemOrderInner>> batchNoGroup = allPhysChemOrderInners.stream().collect(Collectors.groupingBy(PhysChemOrderInner::getBatchNo));
            physChemOrderInner = new ArrayList<>(batchNoGroup.values()).get(0).get(0);
            //冲击参数赋值
            physChemOrderInners = new ArrayList<>(batchNoGroup.values()).get(0);
            //炉号赋值
            physChemOrderInner.setBatchNo(org.apache.commons.lang.StringUtils.join(batchNoGroup.keySet(),","));
        }
        //多选值拼接显示
        StringBuilder forceImpactTemp = new StringBuilder();
        StringBuilder forceImpactGap = new StringBuilder();
        StringBuilder forceImpactDirection = new StringBuilder();
        for (PhysChemOrderInner chemOrderInner : physChemOrderInners) {
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getForceImpactTemp())){
                if(!StringUtils.isNullOrEmpty(String.valueOf(forceImpactTemp))){
                    forceImpactTemp.append("、");
                }
                forceImpactTemp.append(chemOrderInner.getForceImpactTemp());
            }
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getForceImpactGap())){
                if(!StringUtils.isNullOrEmpty(String.valueOf(forceImpactGap))){
                    forceImpactGap.append("、");
                }
                forceImpactGap.append(chemOrderInner.getForceImpactGap());
            }
            if(!StringUtils.isNullOrEmpty(chemOrderInner.getForceImpactDirection())){
                if(!StringUtils.isNullOrEmpty(String.valueOf(forceImpactDirection))){
                    forceImpactDirection.append("、");
                }
                forceImpactDirection.append(chemOrderInner.getForceImpactDirection());
            }
        }
        int sheetNum = 0;
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/" + "lhjcOrderTemp.xls");
            ExcelWriter writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();

            HSSFWorkbook wk = (HSSFWorkbook) writer.getWorkbook();

            if (sheetNum > 0) {
                writer.setSheet(wk.cloneSheet(0));
            }
            //回车
            CellStyle cellStyle = writer.getCellStyle();
            cellStyle.setWrapText(true);
            HSSFFont font = wk.createFont();
            font.setColor(IndexedColors.RED.getIndex());
            cellStyle.setFont(font);
            //向左对齐
            Row row = writer.getSheet().getRow(2);
            Cell cell = row.getCell(0);
            CellStyle cellStyle1 = cell.getCellStyle();
            cellStyle1.setAlignment(HorizontalAlignment.LEFT);
            writer.renameSheet(physChemOrderInner.getOrderNo());
            writer.writeCellValue("A3", physChemOrderInner.getOrderNo());
            writer.writeCellValue("C4", !StringUtils.isNullOrEmpty(physChemOrderInner.getSampleTime())?physChemOrderInner.getSampleTime().substring(0,10):physChemOrderInner.getSampleTime());
            writer.writeCellValue("G4", physChemOrderInner.getSampleReceive());
            writer.writeCellValue("J4", physChemOrderInner.getSampleDept());
            writer.writeCellValue("N4", physChemOrderInner.getManufacturer());
            writer.writeCellValue("C5", physChemOrderInner.getProductName());
            writer.writeCellValue("G5", physChemOrderInner.getMaterialMark());
            writer.writeCellValue("J5", physChemOrderInner.getHeatState());
            writer.writeCellValue("N5", physChemOrderInner.getDrawNo());
            writer.writeCellValue("C6", physChemOrderInner.getSampleNum());
            writer.writeCellValue("G6", physChemOrderInner.getTestBarSpec());
            writer.writeCellValue("J6", physChemOrderInner.getSamplePlace());
            writer.writeCellValue("N6", physChemOrderInner.getAccepStandard());
            writer.writeCellValue("C7", physChemOrderInner.getBatchNo());
            //化学分析
            writer.writeCellValue("C10", physChemOrderInner.getChemicalAnalysis()==0?"":"是");
            writer.writeCellValue("E10", physChemOrderInner.getChemicalCarbonSulfur()==0?"":"是");
            writer.writeCellValue("F10", physChemOrderInner.getChemicalClean()==0?"":"是");
            writer.writeCellValue("G10", physChemOrderInner.getChemicalOtherVal());
            //金相分析
            writer.writeCellValue("J10", physChemOrderInner.getMetallLowPower()==0?"":"是");
            writer.writeCellValue("K10", physChemOrderInner.getMetallTexture()==0?"":"是");
            writer.writeCellValue("L10", physChemOrderInner.getMetallGrainSize()==0?"":"是");
            writer.writeCellValue("M10", physChemOrderInner.getMetallCarbide()==0?"":"是");
            writer.writeCellValue("N10", physChemOrderInner.getMetallInclusions()==0?"":"是");
            writer.writeCellValue("O10", physChemOrderInner.getMetallGraphite()==0?"":"是");
            writer.writeCellValue("P10", physChemOrderInner.getMetallOtherVal());
            //力学性能
            writer.writeCellValue("C13", physChemOrderInner.getForceTensileStrength1());
            writer.writeCellValue("D13", physChemOrderInner.getForceTensileStrength2());
            writer.writeCellValue("E13", physChemOrderInner.getForceTensileElongation());
            writer.writeCellValue("F13", physChemOrderInner.getForceTensileDirection());

            writer.writeCellValue("G13",forceImpactTemp);
            writer.writeCellValue("H13",forceImpactGap);
            writer.writeCellValue("I13",forceImpactDirection);

            writer.writeCellValue("J13", physChemOrderInner.getMetallOtherVal());
            writer.writeCellValue("L13", physChemOrderInner.getForceBendType());
            writer.writeCellValue("M13", physChemOrderInner.getForceBendPart());
            //数量
            writer.writeCellValue("K14", physChemOrderInner.getForceFlaser());
            writer.writeCellValue("N14", physChemOrderInner.getForceShear());
            writer.writeCellValue("O14", physChemOrderInner.getForceOtherVal());
            writer.writeCellValue("P14", physChemOrderInner.getResidual());
            //最底下
            writer.writeCellValue("C16", physChemOrderInner.getReceiveTime());
            writer.writeCellValue("H16", physChemOrderInner.getReportNo());
            writer.writeCellValue("O16", physChemOrderInner.getSampleReceive());
            ServletOutputStream outputStream = rsp.getOutputStream();
            rsp.setContentType("application/vnd.ms-excel;charset=utf-8");
            rsp.setHeader("Content-disposition", "attachment; filename=" + new String("理化检测委托单".getBytes("utf-8"),
                    "ISO-8859-1") + ".xls");
            writer.flush(outputStream, true);
            IoUtil.close(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 委托单复制
     * @param groupId
     * @return
     * @throws GlobalException
     */
    public boolean copyOrder(String groupId) throws GlobalException {
        String newGroupId = UUID.randomUUID().toString().replaceAll("-", "");
        //要复制的数据
        List<PhysChemOrderInner> physChemOrderInners = materialInspectionServiceClient.queryByGroupId(groupId);

        for (PhysChemOrderInner chemOrderInner : physChemOrderInners) {
            chemOrderInner.setId(null);
            chemOrderInner.setStatus("0");
            chemOrderInner.setReportNo(null);
            chemOrderInner.setOrderNo(null);
            //质检发起委托操作
            //设置委托单、报告未生成、实验数据未同步
            chemOrderInner.setSyncStatus(NO_SYNC_STATUS);
            chemOrderInner.setReportStatus(NO_REPORT_STATUS);
            //委托人
            chemOrderInner.setConsignor(SecurityUtils.getCurrentUser().getUserId());
            chemOrderInner.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            //委托组id
            chemOrderInner.setGroupId(newGroupId);
        }
        //保存委托单到中间表
        return materialInspectionServiceClient.saveOrder(physChemOrderInners);
    }


}
