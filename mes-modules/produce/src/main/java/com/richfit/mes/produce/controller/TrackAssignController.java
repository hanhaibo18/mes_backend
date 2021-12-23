package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.produce.service.TrackAssignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.produce.service.TrackCompleteService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.TrackHeadService;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.bouncycastle.util.Arrays;

/**
 * @author 马峰
 * @Description 跟单派工Controller
 */
@Slf4j
@Api("跟单派工")
@RestController
@RequestMapping("/api/produce/trackassign")
public class TrackAssignController extends BaseController {

    @Autowired
    private TrackAssignService trackAssignService;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    
    

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "派工分页查询", notes = "派工分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<Assign>> page(int page, int limit, String tiId, String state, String trackId, String trackNo, String routerNo, String startTime, String endTime) {
        try {
            QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
            if (!StringUtils.isNullOrEmpty(tiId)) {
                queryWrapper.eq("ti_id", tiId);
            }
            if (!StringUtils.isNullOrEmpty(state)) {
                queryWrapper.in("state", state.split(","));
            }
            if (!StringUtils.isNullOrEmpty(trackId)) {
                queryWrapper.eq("track_id",trackId);
            }
            if (!StringUtils.isNullOrEmpty(trackNo)) {
                queryWrapper.eq("track_no", trackNo);
            }
            if (!StringUtils.isNullOrEmpty(routerNo)) {
                queryWrapper.apply("track_id in (select id from produce_track_head where drawing_no='"+routerNo+"')");
            }
            if (!StringUtils.isNullOrEmpty(startTime)) {
                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");

            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + endTime + "')");

            }
            if (null != SecurityUtils.getCurrentUser()) {
                TenantUserDetails user = SecurityUtils.getCurrentUser();
                 queryWrapper.eq("user_id", user.getUserId());

            }
            queryWrapper.orderByDesc(new String[] {"priority","modify_time"});
            
            IPage<Assign> assigns = trackAssignService.page(new Page<Assign>(page, limit), queryWrapper);
            return CommonResult.success(assigns);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "派工自定义分页查询", notes = "派工自定义分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/querypage")
    public CommonResult<IPage<Assign>> querypage(int page, int limit,String siteId, String trackNo, String routerNo, String startTime, String endTime, String state, String userId) {
        try {

            IPage<Assign> assigns = trackAssignService.queryPage(new Page<Assign>(page, limit), siteId,trackNo,routerNo, startTime, endTime, state,userId);
            return CommonResult.success(assigns);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增派工", notes = "新增派工")
    @ApiImplicitParam(name = "assign", value = "派工", required = true, dataType = "Assign", paramType = "path")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign> addAssign(@RequestBody Assign assign) {

        if (StringUtils.isNullOrEmpty(assign.getTiId())) {
            return CommonResult.failed("关联工序ID不能为空！");
        } else {
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            if (null != trackItem) {
                if (trackItem.getAssignableQty() < assign.getQty()) {
                    return CommonResult.failed(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty());
                }

                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                if (null == trackHead.getStatus() || trackHead.getStatus().equals("0") || trackHead.getStatus().equals("")) {
                    //将跟单状态改为在制
                    trackHead.setStatus("1");
                    trackHeadService.updateById(trackHead);
                }
                //可派工数减去已派工数，当前工序为1，在制状态为0
                trackItem.setAssignableQty(trackItem.getAssignableQty() - assign.getQty());
                trackItem.setIsCurrent(1);
                trackItem.setIsDoing(0);
                trackItemService.updateById(trackItem);
            }
            
             if (null != SecurityUtils.getCurrentUser()) {
                String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());

            }
            assign.setModifyTime(new Date());
            assign.setCreateTime(new Date());
            assign.setAvailQty(assign.getQty());
            boolean bool = trackAssignService.save(assign);


            if (bool) {
                return CommonResult.success(assign, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "批量新增派工", notes = "批量新增派工")
    @ApiImplicitParam(name = "assigns", value = "派工", required = true, dataType = "Assign[]", paramType = "path")
    @PostMapping("/batchAdd")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign[]> batchAssign(@RequestBody Assign[] assigns) {
        boolean bool = true;
        for (Assign assign : assigns) {
            if (StringUtils.isNullOrEmpty(assign.getTiId())) {
                return CommonResult.failed("关联工序ID编码不能为空！");
            } else {
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                if (null != trackItem) {
                    if (trackItem.getAssignableQty() < assign.getQty()) {
                        return CommonResult.failed(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty());
                    }

                    TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                    if (null == trackHead.getStatus() || trackHead.getStatus().equals("0") || trackHead.getStatus().equals("")) {
                        //将跟单状态改为在制
                        trackHead.setStatus("1");
                        trackHeadService.updateById(trackHead);
                    }
                    //可派工数减去已派工数，当前工序为1，在制状态为0
                    trackItem.setAssignableQty(trackItem.getAssignableQty() - assign.getQty());
                    trackItem.setIsCurrent(1);
                    trackItem.setIsDoing(0);
                    trackItemService.updateById(trackItem);
                }
                if (null != SecurityUtils.getCurrentUser()) {
                String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());

            }
                assign.setModifyTime(new Date());
                assign.setCreateTime(new Date());
                 assign.setAvailQty(assign.getQty());
                if (!trackAssignService.save(assign)) {
                    bool = false;
                }


            }
        }
        if (bool) {
            return CommonResult.success(assigns, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }

    }

    @ApiOperation(value = "修改派工", notes = "修改派工")
    @ApiImplicitParam(name = "device", value = "派工", required = true, dataType = "Assign", paramType = "path")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign> updateAssign(@RequestBody Assign assign) {



        if (StringUtils.isNullOrEmpty(assign.getTiId())) {
            return CommonResult.failed("关联工序ID编码不能为空！");
        } else {
            TrackItem trackItem = trackItemService.getById(assign.getTiId());

            if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！");
            }
            if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！");
            }
            // 判断后置工序是否已派工，否则无法修改
            List<Assign> cs = this.find(null, null, null, trackItem.getTrackHeadId(), null).getData();
            for (int j = 0; j < cs.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法回滚，需要先取消后序工序【" + cstrackItem.getOptName() + "】的派工");

                }

            }
            // 判断修改的派工数量是否在合理范围
            Assign oldassign = trackAssignService.getById(assign.getId());
            if (null != trackItem) {
                if (trackItem.getAssignableQty() < (assign.getQty() - oldassign.getQty())) {
                    return CommonResult.failed(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty());
                }
            }
            // 设置派工时间，人员，工序可派工数
            if (null != SecurityUtils.getCurrentUser()) {
                String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                assign.setModifyBy(SecurityUtils.getCurrentUser().getUsername());

            }
            assign.setModifyTime(new Date());
             assign.setAvailQty(assign.getQty());
            boolean bool = trackAssignService.updateById(assign);
            trackItem.setAssignableQty(trackItem.getAssignableQty() - (assign.getQty() - oldassign.getQty()));
            if (assign.getState() == 1) {
                trackItem.setIsDoing(1);
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(SecurityUtils.getCurrentUser().getUsername());
            } else {
                trackItem.setIsDoing(0);
            }
            trackItemService.updateById(trackItem);
            if (bool) {
                return CommonResult.success(assign, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "派工查询", notes = "派工查询")
    @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<Assign>> find(String id, String tiId, String state, String trackId, String trackNo) {

        QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(tiId)) {
            queryWrapper.eq("ti_id", tiId);
        }
        if (!StringUtils.isNullOrEmpty(state)) {
            queryWrapper.eq("state", Integer.parseInt(state));
        }
        if (!StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_id", trackId);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.eq("track_no", trackNo);
        }
        queryWrapper.orderByAsc("modify_time");
        List<Assign> result = trackAssignService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "派工查询", notes = "派工查询")
    @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/getPageAssignsByStatus")
    public CommonResult<IPage<TrackItem>> getPageAssignsByStatus(int page, int limit, String trackNo,String routerNo, String startTime, String endTime, String optType) {

        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();

        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");
            queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) <= UNIX_TIMESTAMP('" + endTime + "')");
        }
        if ("4".equals(optType)) {
            queryWrapper.apply("opt_type = 4 and is_final_complete <> '1'");           
        }
        else {
            queryWrapper.apply("opt_type <> 4");           
        }
        queryWrapper.orderByDesc(new String[] {"modify_time"});
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            return CommonResult.success(trackAssignService.getPageAssignsByStatusAndTrack(new Page<TrackItem>(page, limit), trackNo, queryWrapper), "操作成功！");
        }
        else if(!StringUtils.isNullOrEmpty(routerNo)) {
             return CommonResult.success(trackAssignService.getPageAssignsByStatusAndRouter(new Page<TrackItem>(page, limit), routerNo, queryWrapper), "操作成功！");
       } else {
            return CommonResult.success(trackAssignService.getPageAssignsByStatus(new Page<TrackItem>(page, limit), queryWrapper), "操作成功！");
        }
    }

    @ApiOperation(value = "删除派工", notes = "根据id删除派工")
    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "path")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign> delete(@RequestBody String[] ids) {

        for (int i = 0; i < ids.length; i++) {
            Assign assign = trackAssignService.getById(ids[i]);

            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            if(null==trackItem)
            {
                trackAssignService.removeById(ids[i]);
            }
            else {
            if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！");
            }
            if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！");
            }
            List<Assign> ca = this.find(null, null, null, trackItem.getTrackHeadId(), null).getData();
            for (int j = 0; j < ca.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(ca.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法回滚，需要先取消后序工序【" + cstrackItem.getOptName() + "】的派工");

                }

            }
            
            
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            queryWrapper.eq("ti_id", assign.getTiId());
             List<TrackComplete> cs = trackCompleteService.list(queryWrapper);
            if (cs.size() > 0) {
                return CommonResult.failed("无法回滚，已有报工提交，需要先取消工序【" + trackItem.getOptName() + "】的报工！");
            }
            //将前置工序状态改为待派工
            List<TrackItem> items =  trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id",  trackItem.getTrackHeadId()).orderByAsc("opt_sequence"));
            for (int j = 0; j < items.size(); j++) {
                TrackItem cstrackItem = items.get(j);
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    cstrackItem.setIsCurrent(0);
                    cstrackItem.setIsDoing(0);
                    trackItemService.updateById(cstrackItem);
                }

            }
            trackItem.setIsCurrent(1);
            trackItem.setIsDoing(0);
            trackItem.setAssignableQty(trackItem.getAssignableQty() + assign.getQty());
            trackItemService.updateById(trackItem);
            trackAssignService.removeById(ids[i]);

            }
        }
        return CommonResult.success(null, "删除成功！");

    }
}
