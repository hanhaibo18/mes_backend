package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.IErrorCode;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackHeadRelation;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.dao.TrackHeadRelationMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
@Transactional
public class TrackHeadServiceImpl extends ServiceImpl<TrackHeadMapper, TrackHead> implements TrackHeadService {

    @Autowired
    private TrackHeadMapper trackHeadMapper;

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private LineStoreService lineStoreService;

    @Autowired
    private TrackHeadRelationMapper trackHeadRelationMapper;

    @Autowired
    private PlanService planService;

    @Override
    public boolean saveTrackHead(TrackHead trackHead, List<LineStore> lineStores, List<TrackItem> trackItems) {
        int result = trackHeadMapper.insert(trackHead);
        if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanNo())) {
            planService.setPlanStatusStart(trackHead.getWorkPlanNo(), trackHead.getTenantId());
        }
        if (result > 0) {

            String[] products = trackHead.getProductNo().split(",");
            int num = trackHead.getNumber();
            for (int i = 0; i < products.length; i++) {
                if (num == 0) {
                    break;
                }
                int userNum = 0; //本次使用数量
                //修改库存状态
                LineStore lineStore = lineStoreService.useItem(num, trackHead.getDrawingNo(), products[i]);

                TrackHeadRelation relation = new TrackHeadRelation();
                relation.setThId(trackHead.getId());
                relation.setLsId(lineStore.getId());
                relation.setType("0");
                relation.setNumber(userNum);
                trackHeadRelationMapper.insert(relation);
            }
        }
        //新增一条半成品/成品信息
            /*for (LineStore lineStore: lineStores) {
                lineStoreMapper.insert(lineStore);
                TrackHeadRelation relation = new TrackHeadRelation();
                relation.setThId(trackHead.getId());
                relation.setLsId(lineStore.getId());
                relation.setType("1");
                relation.setNumber(1);
                trackHeadRelationMapper.insert(relation);
            }*/
        if (trackItems != null && trackItems.size() > 0) {
            int count = 0;
            for (TrackItem item : trackItems) {
                if (item.getId() != null && !item.getId().equals("")) {
                    count += trackItemMapper.updateById(item);
                } else {
                    item.setTrackHeadId(trackHead.getId());
                    count += trackItemMapper.insert(item);
                }
            }
            return true;
        } else {
            return true;
        }

    }

    @Override
    public boolean deleteTrackHead(List<TrackHead> trackHeads) {
        List<String> ids = trackHeads.stream().filter(trackHead -> {
            if (trackHead.getStatus().equals("0")) {
                return true;
            } else {
                return false;
            }
        }).map(trackHead -> trackHead.getId()).collect(Collectors.toList());
        int result = trackHeadMapper.deleteBatchIds(ids);
        if (result > 0) {
            for (String id : ids) {
                Map<String, Object> map = new HashMap<>();
                map.put("track_head_id", id);
                trackItemMapper.deleteByMap(map);
                List<TrackHeadRelation> relations = trackHeadRelationMapper.selectList(new QueryWrapper<TrackHeadRelation>().eq("th_id", id));
                for (TrackHeadRelation relation : relations) {
                    if (relation.getType().equals("0")) { //输入物料

                        lineStoreService.rollBackItem(relation.getNumber(), relation.getLsId());
                    } else if (relation.getType().equals("1")) { //输出物料
                        lineStoreService.removeById(relation.getLsId());
                    }
                }
            }

            for (TrackHead head : trackHeads) {
                Map<String, Object> map = new HashMap<>();
                map.put("work_plan_no", head.getWorkPlanNo());
                map.put("tenant_id", head.getTenantId());
                List<TrackHead> list = trackHeadMapper.selectByMap(map);
                if (list.size() == 0) {
                    planService.setPlanStatusNew(head.getWorkPlanNo(), head.getTenantId());
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public IPage<TrackHead> selectTrackHeadRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query) {
        return trackHeadMapper.selectTrackHeadRouter(page, query);
    }

    @Override
    public IPage<TrackHead> selectTrackHeadCurrentRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query) {
        return trackHeadMapper.selectTrackHeadCurrentRouter(page, query);
    }

    /**
     * 功能描述: 对当前跟单增加计划
     *
     * @param documentaryId 跟单Id
     * @param workPlanId    计划Id
     * @Author: xinYu.hou
     * @Date: 2022/4/19 18:07
     * @return: boolean
     **/
    @Override
    public boolean updateTrackHeadPlan(String documentaryId, String workPlanId) {
        TrackHead trackHead = this.getById(documentaryId);
        trackHead.setWorkPlanId(workPlanId);
        return this.updateById(trackHead);
    }

    @Override
    public Integer queryTrackHeadList(String workPlanId) {
        return trackHeadMapper.selectTrackHeadNumber(workPlanId);
    }

    @Override
    public IPage<IncomingMaterialVO> queryMaterialList(Integer page, Integer size, String certificateNo, String drawingNo, String branchCode, String tenantId) {
        QueryWrapper<IncomingMaterialVO> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.eq("head.material_certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.eq("head.drawing_no", drawingNo);
        }
        queryWrapper.eq("head.branch_code", branchCode);
        queryWrapper.eq("head.tenant_id", tenantId);
        return trackHeadMapper.queryIncomingMaterialPage(new Page<>(page, size), queryWrapper);
    }

    /**
     * 功能描述: 分页查询跟单台账
     *
     * @param standingBookDto
     * @Author: xinYu.hou
     * @Date: 2022/4/27 23:06
     * @return: IPage<TrackHead>
     **/
    @Override
    public IPage<TrackHead> queryTrackHeadPage(QueryDto<StandingBookDto> standingBookDto) {
        StandingBookDto standing = standingBookDto.getParam();
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", standing.getStartTime());
        //处理结束时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(standing.getEndTime());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        queryWrapper.le("create_time", calendar.getTime());
        if (!StringUtils.isNullOrEmpty(standing.getDrawingNo())) {
            queryWrapper.eq("drawing_no", standing.getDrawingNo());
        }
        if (!StringUtils.isNullOrEmpty(standing.getDocumentaryId())) {
            queryWrapper.eq("track_no", standing.getDocumentaryId());
        }
        if (!StringUtils.isNullOrEmpty(standing.getProductNo())) {
            queryWrapper.eq("product_no", standing.getProductNo());
        }
        if (!StringUtils.isNullOrEmpty(standing.getWorkNo())) {
            queryWrapper.eq("work_no", standing.getWorkNo());
        }
        queryWrapper.eq("branch_code", standingBookDto.getBranchCode());
        queryWrapper.eq("tenant_id", standingBookDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return this.page(new Page<>(standingBookDto.getPage(), standingBookDto.getSize()), queryWrapper);
    }

    /**
     * 功能描述: 工作清单
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:41
     * @return: IPage<WorkDetailedListVo>
     **/
    @Override
    public IPage<WorkDetailedListVo> queryWorkDetailedList(QueryDto<QueryWork> queryDto) {
        QueryWork queryWork = queryDto.getParam();
        QueryWrapper<WorkDetailedListVo> queryWrapper = new QueryWrapper<>();
        if (null != queryWork.getStartDate() && null != queryWork.getEndDate()) {
            queryWrapper.ge("create_time", queryWork.getStartDate());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(queryWork.getEndDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (null != queryWork.getWorkId()) {
            queryWrapper.eq("head.work_no", queryWork.getWorkId());
        }
        if (null != queryWork.getDrawingNo()) {
            queryWrapper.eq("head.drawing_no", queryWork.getWorkId());
        }
        if (null != queryWork.getTrackNo()) {
            queryWrapper.eq("head.track_no", queryWork.getTrackNo());
        }
        queryWrapper.eq("branch_code", queryDto.getBranchCode());
        queryWrapper.eq("tenant_id", queryDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return trackHeadMapper.queryWorkDetailedList(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

    /**
     * 功能描述: 修改优先级
     *
     * @param trackNo
     * @param priority
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:49
     * @return: Boolean
     **/
    @Override
    public Boolean updateWorkDetailed(String trackNo, String priority) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.eq("track_no", trackNo);
        }
        TrackHead trackHead = this.getOne(queryWrapper);
        trackHead.setPriority(priority);
        return this.updateById(trackHead);
    }

    @Override
    public IPage<TailAfterVo> queryTailAfterList(QueryDto<QueryTailAfterDto> afterDto) {
        QueryTailAfterDto tailAfter = afterDto.getParam();
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (null != tailAfter.getEndDate() && null != tailAfter.getStartDate()) {
            queryWrapper.ge("create_time", tailAfter.getStartDate());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(tailAfter.getEndDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (!StringUtils.isNullOrEmpty(tailAfter.getDrawingNo())) {
            queryWrapper.eq("drawing_no", tailAfter.getDrawingNo());
        }
        if (!StringUtils.isNullOrEmpty(tailAfter.getTrackNo())) {
            queryWrapper.ge("track_no", tailAfter.getTrackNo());
        }
        queryWrapper.eq("branch_code", afterDto.getBranchCode());
        queryWrapper.eq("tenant_id", afterDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return trackHeadMapper.queryTailAfterList(new Page<>(afterDto.getPage(), afterDto.getSize()), queryWrapper);
    }

    @Override
    public IPage<TrackHead> querySplitPage(QueryDto<QuerySplitDto> queryDto) {
        QuerySplitDto querySplitDto = queryDto.getParam();
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (null != querySplitDto.getEndTime() && null != querySplitDto.getStartTime()) {
            queryWrapper.ge("create_time", querySplitDto.getStartTime());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(querySplitDto.getEndTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (null != querySplitDto.getStatus()) {
            queryWrapper.eq("status", querySplitDto.getStatus());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getTrackNo())) {
            queryWrapper.like("track_no", querySplitDto.getTrackNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getDrawingNo())) {
            queryWrapper.like("drawing_no", querySplitDto.getDrawingNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getProductNo())) {
            queryWrapper.like("product_no", querySplitDto.getProductNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getTemplateCode())) {
            queryWrapper.eq("template_code", querySplitDto.getTemplateCode());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getWorkPlanId())) {
            queryWrapper.eq("work_plan_id", querySplitDto.getWorkPlanId());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getBatchNo())) {
            queryWrapper.eq("batch_no", querySplitDto.getBatchNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getProductionOrder())) {
            queryWrapper.eq("production_order", querySplitDto.getProductionOrder());
        }
        return this.page(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

    @Override
    public CommonResult<Boolean> saveTrackHeader(SaveTrackHeadDto saveTrackHeadDto) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_no", saveTrackHeadDto.getTrackHead());
        TrackHead trackHead = this.getOne(queryWrapper);
        if (trackHead.getNumber() < saveTrackHeadDto.getNumber()) {
            return CommonResult.failed(new IErrorCode() {
                @Override
                public long getCode() {
                    return 500;
                }

                @Override
                public String getMessage() {
                    return "请输入正确拆分数量";
                }
            });
        }
        trackHead.setNumber(trackHead.getNumber() - saveTrackHeadDto.getNumber());
        this.updateById(trackHead);
        trackHead.setTrackNo(saveTrackHeadDto.getNewTrackHead());
        trackHead.setNumber(saveTrackHeadDto.getNumber());
        return CommonResult.success(this.save(trackHead));
    }
}
