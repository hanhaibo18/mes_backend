package com.richfit.mes.produce.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.MaterialInspectionServiceClient;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.WordUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
    //已经完工
    private final static int YES_DOING = 2;
    //工序完成
    private final static int ITEM_END = 1;

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
    public CommonResult saveOrder(PhysChemOrderInner physChemOrderInner) throws Exception {
        //校验修改
        if(!com.mysql.cj.util.StringUtils.isNullOrEmpty(physChemOrderInner.getId())){
            String status = physChemOrderInner.getStatus();
            if(YES_STATUS.equals(status)){
                return CommonResult.failed("材料实验室已经确认委托，无法被修改");
            }
        }else{
            //质检发起委托操作
            //设置委托单状态为待确认、报告生成、实验数据未同步
            physChemOrderInner.setStatus(GOING_STATUS);
            physChemOrderInner.setSyncStatus(NO_SYNC_STATUS);
            physChemOrderInner.setReportStatus(NO_REPORT_STATUS);
            //保存委托单号
            Code.update("order_no",physChemOrderInner.getOrderNo(),SecurityUtils.getCurrentUser().getTenantId(), physChemOrderInner.getBranchCode(),codeRuleService);
            //保存报告号
            Code.update("report_no",physChemOrderInner.getOrderNo(),SecurityUtils.getCurrentUser().getTenantId(), physChemOrderInner.getBranchCode(),codeRuleService);

        }
        //委托人
        physChemOrderInner.setConsignor(SecurityUtils.getCurrentUser().getUserId());
        physChemOrderInner.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        //本地保存一份委托单用于委托单打印
        PhysChemOrder physChemOrder = new PhysChemOrder();
        BeanUtil.copyProperties(physChemOrderInner,physChemOrder);
        physChemOrderService.saveOrUpdate(physChemOrder);
        //为了委托单本地和中间表id保持一致
        physChemOrderInner.setId(physChemOrder.getId());
        return CommonResult.success(materialInspectionServiceClient.saveOrder(physChemOrderInner));
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
        //委托单数据
        QueryWrapper<PhysChemOrder> physChemOrderQueryWrapper = new QueryWrapper<>();
        physChemOrderQueryWrapper.eq("report_no",reportNo);
        PhysChemOrder physChemOrder = physChemOrderService.list(physChemOrderQueryWrapper).get(0);
        //试验结果数据
        QueryWrapper<PhysChemResult> physChemResultQueryWrapper = new QueryWrapper<>();
        physChemResultQueryWrapper.eq("report_no",reportNo);
        List<PhysChemResult> results = physChemResultService.list(physChemResultQueryWrapper);

        //构造填充数据
        Map<String, Object> dataMap = new HashMap<>();
        //委托单数据
        dataMap.putAll(JSON.parseObject(JSON.toJSONString(physChemOrder), Map.class));
        dataMap.put("rel","Rel");
        dataMap.put("forceTensileElongation","A50mm");
        dataMap.put("forceBendDirection","横向");
        dataMap.put("forceImpactDirection","横向");
        dataMap.put("forceTensileDirection","纵向");
        dataMap.put("forceBendType","截面1/2"); //硬度
        dataMap.put("forceFlaser","1232"); //压扁
        dataMap.put("w","10mm");
        dataMap.put("kp","300j");
        dataMap.put("kv2","kv2");
        List<String> headerNames = new ArrayList<>();
        headerNames.add("V");
        headerNames.add("Nb");
        headerNames.add("Ti");
        dataMap.put("headerNames",headerNames);
        createDataMap(dataMap);
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
        PhysChemOrder physChemOrder = new PhysChemOrder();
        //查询委托单
        List<PhysChemOrder> list = physChemOrderService.list(new QueryWrapper<PhysChemOrder>().eq("order_no",orderNo));
        if(list.size()>0){
            physChemOrder = list.get(0);
        }
        int sheetNum = 0;
        try {
            ExcelWriter writer = ExcelUtil.getReader(ResourceUtil.getStream("excel/" + "PhyChemOrderTemplate.xlsx")).getWriter();
            HSSFWorkbook wk = (HSSFWorkbook) writer.getWorkbook();

            if (sheetNum > 0) {
                writer.setSheet(wk.cloneSheet(0));
            }
            //回车
            CellStyle cellStyle = writer.getCellStyle();
            cellStyle.setWrapText(true);
            //向左对齐
            writer.renameSheet(physChemOrder.getOrderNo());
            writer.writeCellValue("A3", physChemOrder.getOrderNo());
            writer.writeCellValue("C4", physChemOrder.getSampleTime());
            writer.writeCellValue("F4", physChemOrder.getManufacturer());
            writer.writeCellValue("I4", physChemOrder.getManufacturer());

            ServletOutputStream outputStream = rsp.getOutputStream();
            rsp.setContentType("application/vnd.ms-excel;charset=utf-8");
            rsp.setHeader("Content-disposition", "attachment; filename=" + new String("理化检测委托单".getBytes("utf-8"),
                    "ISO-8859-1") + ".xlsx");
            writer.flush(outputStream, true);
            IoUtil.close(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


}
