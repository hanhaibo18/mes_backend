package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.common.model.produce.PhysChemResult;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItemInspection;
import com.richfit.mes.produce.dao.TrackItemInspectionMapper;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 理化检验接口
 */
@Service
@Slf4j
public class PhyChemTestService{

    @Autowired
    private TrackItemInspectionMapper trackItemInspectionMapper;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private PhysChemOrderService physChemOrderService;
    @Autowired
    private PhysChemResultService physChemResultService;
    @Autowired
    private WordUtil wordUtil;

    /**
     * 查询跟单工序发起委托列表
     * @param page
     * @param limit
     * @param startTime
     * @param endTime
     * @param trackNo
     * @param productName
     * @param branchCode
     * @param tenantId
     * @param isCheckOut
     * @return
     */
    public IPage<TrackHead> page(int page, int limit, String startTime, String endTime, String trackNo, String productName,String drawingNo, String branchCode, String tenantId, Boolean isCheckOut) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        if (!StringUtils.isEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        if (!StringUtils.isEmpty(trackNo)) {
            queryWrapper.eq("track_no",trackNo);
        }
        if (!StringUtils.isEmpty(productName)) {
            queryWrapper.eq("product_name",productName);
        }
        if (!StringUtils.isEmpty(drawingNo)) {
            queryWrapper.eq("drawing_no", drawingNo);
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.gt("modify_time",startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            queryWrapper.lt("modify_time",endTime);

        }
        queryWrapper.orderByDesc("modify_time");
        IPage<TrackHead> trackHeads = trackHeadService.page(new Page<TrackHead>(page, limit), queryWrapper);
        //处理map
        Map<String, TrackHead> trackHeadMap =
                trackHeads.getRecords().stream().collect(Collectors.toMap(TrackHead::getId, Function.identity()));
        //查询委托单
        List<PhysChemOrder> physChemOrders = physChemOrderService.list(new QueryWrapper<PhysChemOrder>().in("id", trackHeadMap.keySet()));
        Map<String, PhysChemOrder> physChemOrderMap = physChemOrders.stream().collect(Collectors.toMap(PhysChemOrder::getId, Function.identity()));
        for (TrackHead trackHead : trackHeads.getRecords()) {
            //委托单赋值
            if(!ObjectUtil.isEmpty(physChemOrderMap.get(trackHead.getPhysChemId()))){
                trackHead.setPhysChemOrder(physChemOrderMap.get(trackHead.getPhysChemId()));
            }
        }
        return trackHeads;
    }

    /**
     * 同步试验结果
     * @param itemIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncResult(List<String> itemIds){
        //构造存储本地理化试验结果数据
        List<PhysChemResult> physChemResults = new ArrayList<>();
        for (String itemId : itemIds) {
            //调用理化试验接口获取试验结果数据
            try {

            }catch (Exception e){
                log.error("调用化验同步数据接口错误！", e);
            }
        }

        //删除旧的实验结果数据(调试时需要确定重复同步情况是否更新)
        List<PhysChemResult> oldResults = physChemResultService.list(new QueryWrapper<PhysChemResult>().in("item_id", itemIds));
        if(oldResults.size()>0){
            List<String> oldResultsIds = oldResults.stream().map(PhysChemResult::getId).collect(Collectors.toList());
            physChemResultService.removeByIds(oldResultsIds);
        }
        //保存新的测试结果数据
        physChemResultService.saveBatch(physChemResults);
    }

    /**
     * 理化检测报告导出
     * @param response
     * @throws IOException
     * @throws TemplateException
     */
    public void exoprtReport(HttpServletResponse response,String itemId) throws IOException, TemplateException {
        //根据id查询试验数据
        QueryWrapper<PhysChemResult> physChemResultQueryWrapper = new QueryWrapper<>();
        physChemResultQueryWrapper.eq("item_id",itemId);
        List<PhysChemResult> results = physChemResultService.list(physChemResultQueryWrapper);
        //根据跟单工序id查询委托单
        QueryWrapper<PhysChemOrder> physChemOrderQueryWrapper = new QueryWrapper<>();
        physChemOrderQueryWrapper.eq("item_id",itemId);
        List<PhysChemOrder> orders = physChemOrderService.list(physChemOrderQueryWrapper);
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
