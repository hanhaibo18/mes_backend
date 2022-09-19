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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
    public IPage<TrackItemInspection> page(int page, int limit, String startTime, String endTime, String trackNo, String productName,String drawingNo, String branchCode, String tenantId, Boolean isCheckOut) {
        QueryWrapper<TrackItemInspection> queryWrapper = new QueryWrapper<TrackItemInspection>();
        if (!StringUtils.isEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        //已审核
        if (Boolean.TRUE.equals(isCheckOut)) {
            queryWrapper.isNotNull("audit_by");
        } else if (Boolean.FALSE.equals(isCheckOut)) {
            //未审核
            queryWrapper.isNull("audit_by");
        }

        if (!StringUtils.isEmpty(trackNo)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where track_no LIKE '" + trackNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(productName)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where product_name LIKE '" + productName + '%' + "')");
        }
        if (!StringUtils.isEmpty(drawingNo)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where product_name LIKE '" + drawingNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");

        }
        if (!StringUtils.isEmpty(endTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + endTime + "')");

        }
        queryWrapper.orderByDesc("modify_time");
        IPage<TrackItemInspection> trackItemInspections = trackItemInspectionMapper.selectPage(new Page<TrackItemInspection>(page, limit), queryWrapper);
        //处理map
        Map<String, TrackItemInspection> trackItemInspectionMap =
                trackItemInspections.getRecords().stream().collect(Collectors.toMap(TrackItemInspection::getId, Function.identity()));
        //查询委托单
        List<PhysChemOrder> physChemOrders = physChemOrderService.list(new QueryWrapper<PhysChemOrder>().in("item_id", trackItemInspectionMap.keySet()));
        Map<String, PhysChemOrder> physChemOrderMap = physChemOrders.stream().collect(Collectors.toMap(PhysChemOrder::getItemId, Function.identity()));
        for (TrackItemInspection trackItemInspection : trackItemInspections.getRecords()) {
            TrackHead trackHead = trackHeadService.getById(trackItemInspection.getTrackHeadId());
            trackItemInspection.setTrackNo(trackHead.getTrackNo());
            trackItemInspection.setDrawingNo(trackHead.getDrawingNo());
            trackItemInspection.setQty(trackHead.getNumber());
            trackItemInspection.setProductName(trackHead.getProductName());
            trackItemInspection.setWorkNo(trackHead.getWorkNo());
            trackItemInspection.setTrackType(trackHead.getTrackType());
            trackItemInspection.setTexture(trackHead.getTexture());
            trackItemInspection.setPartsName(trackHead.getMaterialName());
            //委托单赋值
            if(!ObjectUtil.isEmpty(physChemOrderMap.get(trackItemInspection.getId()))){
                trackItemInspection.setPhysChemOrder(physChemOrderMap.get(trackItemInspection.getId()));
            }
        }
        return trackItemInspections;
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

}
