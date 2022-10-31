package com.richfit.mes.produce.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.PhysChemOrderMapper;
import com.richfit.mes.produce.entity.phyChemTestVo.PhyChemTaskVo;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.WordUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private PhysChemOrderMapper physChemOrderMapper;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private PhysChemResultInterService physChemResultInterService;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private CodeRuleService codeRuleService;
    //历史状态
    private final static String IS_HISTORY = "1";
    //历史状态
    private final static String NO_HISTORY = "0";
    //同步状态
    private final static String SYNC_STATUS = "1";
    //未同步状态
    private final static String NO_SYNC_STATUS = "0";
    //委托单拒绝
    private final static String BACK_STATUS = "3";
    //委托单确认
    private final static String YES_STATUS = "2";
    //委托单已发起
    private final static String GOING_STATUS = "1";
    //委托单待发起
    private final static String GO_UP_STATUS = "0";
    //材料检测部门生成报告
    private final static String YES_REPORT_STATUS = "1";
    //材料检测部门未生成报告
    private final static String NO_REPORT_STATUS = "0";
    /**
     * 查询跟单工序发起委托列表
     * @param phyChemTaskVo
     * @return
     */
    public IPage<PhysChemOrder> page(PhyChemTaskVo phyChemTaskVo) {
        //需要理化检测的
        List<TrackItem> trackItems = trackItemService.list(new QueryWrapper<TrackItem>().eq("is_entrust", GOING_STATUS));
        //获取跟单ids
        List<String> headIds = trackItems.stream().map(item -> item.getTrackHeadId()).collect(Collectors.toList());

        phyChemTaskVo.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        phyChemTaskVo.setConsignor(SecurityUtils.getCurrentUser().getUserId());
        //对应mapper文件中的别名
        String orderTableName = null;
        //跟单排序字段
        List<String> list = new ArrayList<>();
        list.add("product_name");
        list.add("drawing_no");
        list.add("track_no");
        if(!StringUtils.isEmpty(phyChemTaskVo.getOrderCol()) && !StringUtils.isEmpty(phyChemTaskVo.getOrder())){
            if(list.contains(StrUtil.toUnderlineCase(phyChemTaskVo.getOrderCol()))){
                orderTableName = "head";
            }else{
                orderTableName = "py_order";
            }
            phyChemTaskVo.setOrderCol(StrUtil.toUnderlineCase(phyChemTaskVo.getOrderCol()));
        }

        IPage<PhysChemOrder> physChemOrderIPage = physChemOrderMapper.queryTestPageList(new Page(phyChemTaskVo.getPage(), phyChemTaskVo.getLimit()), phyChemTaskVo,orderTableName,headIds);



        return physChemOrderIPage;
    }

    /**
     * 修改委托单
     * @param physChemOrder
     * @return
     */
    public CommonResult save(PhysChemOrder physChemOrder) throws Exception {
        //校验修改
        if(!com.mysql.cj.util.StringUtils.isNullOrEmpty(physChemOrder.getId())){
            PhysChemOrder order = physChemOrderService.getById(physChemOrder.getId());
            String status = order.getStatus();
            if(YES_STATUS.equals(status)){
                return CommonResult.failed("材料实验室已经确认委托，无法被修改");
            }
        }else{
            //质检发起委托操作
            //判断委托单状态
            if(!StringUtils.isEmpty(physChemOrder.getBatchNo())){
                List<PhysChemOrder> orders = physChemOrderService.list(new QueryWrapper<PhysChemOrder>().eq("batch_no", physChemOrder.getBatchNo()).orderByDesc("modify_time"));
                if(orders.size()>0){
                    //未生成报告
                    if(NO_REPORT_STATUS.equals(orders.get(0).getStatus()) && YES_STATUS.equals(orders.get(0).getStatus())){
                        return CommonResult.failed("炉批号:"+physChemOrder.getBatchNo()+",委托单已经在检验流程中,无法发起新的委托");
                    }
                }
            }
            //设置委托单状态为待发起、报告生成、实验数据未同步
            physChemOrder.setStatus(GO_UP_STATUS);
            physChemOrder.setSyncStatus(NO_SYNC_STATUS);
            physChemOrder.setReportStatus(NO_REPORT_STATUS);
            //设置工序为发起委托单工序
            TrackItem trackItem = new TrackItem();
            trackItem.setIsEntrust(GOING_STATUS);
            trackItemService.updateById(trackItem);
            //保存委托单号
            Code.update("order_no",physChemOrder.getOrderNo(),SecurityUtils.getCurrentUser().getTenantId(), physChemOrder.getBranchCode(),codeRuleService);
        }
        //委托人
        physChemOrder.setConsignor(SecurityUtils.getCurrentUser().getUserId());
        physChemOrder.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(physChemOrderService.saveOrUpdate(physChemOrder));
    }

    /**
     * 发送委托单到材料质检部
     * @param orderId
     * @return
     */
    public boolean sendOrderToZj(String orderId){
        //设置委托单发起状态
        return changeOrderStaus(GOING_STATUS, orderId);
    }


    /**
     * 材料质检部确认或者拒绝委托单
     * @param orderId
     * @return
     */
    public boolean zJConfirm(String orderId,String status){
        //设置委托单确认状态
        return changeOrderStaus(status, orderId);
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
     * @param orderNos
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> syncResult(List<String> orderNos){
        //委托单数据
        QueryWrapper<PhysChemOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_no",orderNos);
        List<PhysChemOrder> orders = physChemOrderService.list(queryWrapper);
        //校验是否能执行同步操作
        List<PhysChemOrder> checkList = orders.stream().filter(order ->
                //非确认委托状态
                !YES_STATUS.equals(order.getStatus())
                        //未生成报告的
                        || NO_REPORT_STATUS.equals(order.getReportStatus())).collect(Collectors.toList());
        List<PhysChemOrder> checkList2 = orders.stream().filter(order ->
                //同步成功的数据
                SYNC_STATUS.equals(order.getSyncStatus())).collect(Collectors.toList());
        if (checkList.size()>0) {
            return CommonResult.failed("请确认所选数据'确认委托并且已生成报告数据'");
        }
        if(checkList2.size()>0){
            return CommonResult.failed("所选数据有已同步的数据");
        }

        //同步数据的的炉批号集合
        List<String> batchNos = orders.stream().map(item -> item.getBatchNo()).collect(Collectors.toList());

        if(batchNos.size()>0){

            //从中间表同步数据
            QueryWrapper<PhysChemResultInter> intercationWrapper = new QueryWrapper<>();
            intercationWrapper.eq("batch_no",batchNos);
            List<PhysChemResultInter> intercationInfos = physChemResultInterService.list(intercationWrapper);
            //新增数据
            List<PhysChemResult> physChemResults = new ArrayList<>();
            for (PhysChemResultInter intercationInfo : intercationInfos) {
                PhysChemResult physChemResult = new PhysChemResult();
                BeanUtil.copyProperties(intercationInfo,physChemResult,new String[]{"id"});
                physChemResults.add(physChemResult);
            }
            //本地已经同步的数据
            QueryWrapper<PhysChemResult> localWrapper = new QueryWrapper<>();
            localWrapper.in("batch_no",batchNos);
            List<PhysChemResult> localInfos = physChemResultService.list(localWrapper);
            //删除本地已同步的数据
            physChemResultService.removeByIds(localInfos.stream().map(PhysChemResult::getId).collect(Collectors.toList()));

            //保存新数据
            physChemResultService.saveBatch(physChemResults);

            //修改委托单同步接口状态"已同步"
            UpdateWrapper<PhysChemOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("order_no",orderNos)
                    .set("sync_status",SYNC_STATUS)
                    .set("sync_time", DateUtil.date());
            physChemOrderService.update(updateWrapper);
        }
        return CommonResult.success(true);
    }

    /**
     * 理化检测报告导出
     * @param response
     * @throws IOException
     * @throws TemplateException
     */
    public void exoprtReport(HttpServletResponse response,String hid) throws IOException, TemplateException {
        //跟单数据
        TrackHead trackHead = trackHeadService.getById(hid);
        //委托单数据 根据batch_no查询
        QueryWrapper<PhysChemOrder> physChemOrderQueryWrapper = new QueryWrapper<>();
        physChemOrderQueryWrapper.eq("batch_no",trackHead.getBatchNo());
        List<PhysChemOrder> orders = physChemOrderService.list(physChemOrderQueryWrapper);
        //试验结果数据
        QueryWrapper<PhysChemResult> physChemResultQueryWrapper = new QueryWrapper<>();
        physChemResultQueryWrapper.eq("batch_no",trackHead.getBatchNo());
        List<PhysChemResult> results = physChemResultService.list(physChemResultQueryWrapper);

        //构造填充数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("rel","Rel");
        dataMap.put("forceTensileElongation","A50mm");
        dataMap.put("forceBendDirection","横向");
        dataMap.put("forceImpactDirection","横向");
        dataMap.put("forceTensileDirection","纵向");
        dataMap.put("forceBendType","截面1/2");
        dataMap.put("forceFlaserNumber","1232");
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
}
