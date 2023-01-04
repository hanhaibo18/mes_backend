package com.richfit.mes.produce.service.heat;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.PlanService;
import com.richfit.mes.produce.service.ProduceRoleOperationService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.utils.OrderUtil;
import com.richfit.mes.produce.utils.ProcessFiltrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author zhiqiang.lu
 * @Description 跟单派工服务
 */
@Service
public class
HeatTrackAssignServiceImpl extends ServiceImpl<TrackAssignMapper, Assign> implements HeatTrackAssignService {
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

    @Override
    public IPage<Assign> queryNotAtWork(ForDispatchingDto dispatchingDto) throws ParseException {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
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
}
