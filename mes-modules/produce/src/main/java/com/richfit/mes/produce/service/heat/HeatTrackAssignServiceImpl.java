package com.richfit.mes.produce.service.heat;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.utils.OrderUtil;
import com.richfit.mes.produce.utils.ProcessFiltrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhiqiang.lu
 * @Description 跟单派工服务
 */
@Service
public class  HeatTrackAssignServiceImpl extends ServiceImpl<TrackAssignMapper, Assign> implements HeatTrackAssignService {
    @Autowired
    public TrackAssignMapper trackAssignMapper;
    @Resource
    public TrackItemService trackItemService;
    @Resource
    public TrackHeadService trackHeadService;
    @Resource
    public PlanService planService;
    @Resource
    private ProduceRoleOperationService roleOperationService;
    @Resource
    private SystemServiceClient systemServiceClient;
    @Autowired
    public BaseServiceClient baseServiceClient;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;
    @Autowired
    private TrackAssignService trackAssignService;

    @Override
    public IPage<Assign> queryWhetherProduce(ForDispatchingDto dispatchingDto, boolean IsProduce) throws ParseException {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTempWork())) {
            int tempWorkZ = Integer.parseInt(dispatchingDto.getTempWork()) + Integer.parseInt(dispatchingDto.getTempWork1());
            int tempWorkQ = Integer.parseInt(dispatchingDto.getTempWork()) - Integer.parseInt(dispatchingDto.getTempWork1());
            //小于等于
            queryWrapper.le("u.temp_work", tempWorkZ);
            //大于等于
            queryWrapper.ge("u.temp_work", tempWorkQ);
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTrackNo())) {
            dispatchingDto.setTrackNo(dispatchingDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(u.track_no2, char(13), ''), char(10), ''),' ', '') like '%" + dispatchingDto.getTrackNo() + "%'");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getRouterNo())) {
            queryWrapper.like("u.drawing_no", dispatchingDto.getRouterNo());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getProductNo())) {
            queryWrapper.like("u.product_no", dispatchingDto.getProductNo());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getStartTime())) {
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) >= UNIX_TIMESTAMP('" + dispatchingDto.getStartTime() + " ')");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getEndTime())) {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(dispatchingDto.getEndTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
        }
        if ("0,1".equals(dispatchingDto.getState())) {
            queryWrapper.in("u.state", 0, 1);
        }
        if ("2".equals(dispatchingDto.getState())) {
            queryWrapper.in("u.state", 2);
        }
        //增加工序过滤
        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
        queryWrapper.eq(StrUtil.isNotBlank(dispatchingDto.getClasses()), "u.classes", dispatchingDto.getClasses());
        if (IsProduce) {
            queryWrapper.isNotNull("u.precharge_furnace_id");
        } else {
            queryWrapper.isNull("u.precharge_furnace_id");
        }
        queryWrapper.eq("u.site_id", SecurityUtils.getCurrentUser().getBelongOrgId());
        queryWrapper.eq("u.branch_code", dispatchingDto.getBranchCode());
        queryWrapper.eq("u.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        OrderUtil.query(queryWrapper, dispatchingDto.getOrderCol(), dispatchingDto.getOrder());
        IPage<Assign> queryPage = trackAssignMapper.queryPageAssignTrackStore(new Page(dispatchingDto.getPage(), dispatchingDto.getLimit()), queryWrapper);
        if (null != queryPage.getRecords()) {
            for (Assign assign : queryPage.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(assign.getTrackId());
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                if (!StringUtils.isNullOrEmpty(trackHead.getRouterId())) {
                    assign.setRouterId(trackHead.getRouterId());
                }
                assign.setOptId(trackItem.getOptId());
                assign.setWeight(trackHead.getWeight());
                assign.setWorkNo(trackHead.getWorkNo());
                assign.setProductName(trackHead.getProductName());
                assign.setTotalQuantity(trackItem.getNumber());
                assign.setDispatchingNumber(trackItem.getAssignableQty());
                assign.setWorkPlanNo(trackHead.getWorkPlanNo());
            }
        }
        return queryPage;
    }

    /**
     * 功能描述：热工跟单派工
     * @param assign
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Assign assignItem(Assign assign) throws Exception {
        try {
            if (StringUtils.isNullOrEmpty(assign.getTiId())) {
                throw new GlobalException("未关联工序", ResultCode.FAILED);
            }
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            //默认全部派工
            assign.setQty(trackItem.getAssignableQty());
            if (null != trackItem) {
                trackItem.setIsCurrent(1);
                trackItem.setIsDoing(0);
                //默认全部派工
                trackItem.setAssignableQty(0);
                //已派工
                trackItem.setIsSchedule(1);
                trackItem.setDeviceId(assign.getDeviceId());
                //处理工艺信息
                trackItem.setRouterInfo(getRouterInfo(trackItem));
                trackItemService.updateById(trackItem);
                assign.setTrackNo(trackHead.getTrackNo());
                if (!StringUtils.isNullOrEmpty(trackHead.getStatus()) || "0".equals(trackHead.getStatus())) {
                    //将跟单状态改为在制
                    trackHead.setStatus("1");
                    trackHeadService.updateById(trackHead);
                    UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
                    update.set("status", "1");
                    update.eq("id", trackItem.getFlowId());
                    trackHeadFlowService.update(update);
                }
                assign.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                if (null != SecurityUtils.getCurrentUser()) {
                    assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
                }
                CommonResult<TenantUserVo> user = systemServiceClient.queryByUserId(assign.getAssignBy());
                assign.setAssignName(user.getData().getEmplName());
                assign.setAssignTime(new Date());
                assign.setModifyTime(new Date());
                assign.setCreateTime(new Date());
                assign.setAvailQty(assign.getQty());
                assign.setFlowId(trackItem.getFlowId());
                assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                assign.setTrackNo(trackHead.getTrackNo());
                assign.setDeviceId(trackItem.getTypeCode());
                assign.setDeviceName(trackItem.getTypeName());
                assign.setClasses(trackHead.getClasses());
                //保存派工信息
                trackAssignService.save(assign);
            }
            return assign;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 工艺信息字段拼接
     * @param trackItem
     * @return
     */
    private String getRouterInfo(TrackItem trackItem) {
        StringBuilder routerInfo = new StringBuilder();
        QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
        trackItemQueryWrapper.eq("flow_id",trackItem.getFlowId())
                .orderByAsc("sequence_order_by");
        List<TrackItem> list = trackItemService.list(trackItemQueryWrapper);
        for (TrackItem item : list) {
            if(!StringUtils.isNullOrEmpty(String.valueOf(routerInfo))){
                routerInfo.append(";");
            }
            routerInfo.append(item.getOptName()+" "+item.getTempWork()+" "+item.getHoldTime()+" "+item.getCoolType());
        }
        return String.valueOf(routerInfo);
    }
}
