package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zhiqian.lu
 * @Description 跟单工序Controller
 */
@Slf4j
@Api(value = "热工跟单工序管理", tags = {"热工跟单工序管理"})
@RestController
@RequestMapping("/api/produce/heat/track_item")
public class HeatTrackItemController extends BaseController {

    @Autowired
    public TrackItemService trackItemService;
    @Autowired
    public BaseServiceClient baseServiceClient;
    @Autowired
    public TrackHeadService trackHeadService;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private TrackAssignService trackAssignService;
    @Autowired
    private ProduceRoleOperationService roleOperationService;

    public static String TRACK_HEAD_ID_NULL_MESSAGE = "跟单ID不能为空！";
    public static String TRACK_ITEM_ID_NULL_MESSAGE = "跟单工序ID不能为空！";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败，请重试！";


    @ApiOperation(value = "预装炉工序查询", notes = "预装炉工序查询")
    @GetMapping("/item/precharge")
    public CommonResult<List<TrackItem>> selectTrackHead(String id, String trackId, String optVer, String productNo) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper();
        if (!StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_head_id", trackId);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", productNo);
        }
        if (!StringUtils.isNullOrEmpty(optVer)) {
            queryWrapper.like("opt_ver", optVer);
        }
        queryWrapper.orderByAsc("sequence_order_by");
        return CommonResult.success(trackItemService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    /**
     * @param assigns
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "热工批量新增派工", notes = "热工批量新增派工")
    @ApiImplicitParam(name = "assigns", value = "派工", required = true, dataType = "Assign[]", paramType = "body")
    @PostMapping("/batchAdd")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign[]> batchAdd(@RequestBody Assign[] assigns) throws Exception {
        try {
            for (Assign assign : assigns) {
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
                    trackItemService.updateById(trackItem);
                    if (StringUtils.isNullOrEmpty(assign.getTrackNo())) {
                        assign.setTrackNo(trackHead.getTrackNo());
                    }
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
                    //保存派工信息
                    trackAssignService.save(assign);
                }
                systemServiceClient.savenote(assign.getAssignBy(),
                        "您有新的派工跟单需要报工！",
                        assign.getTrackNo(),
                        assign.getUserId().substring(0, assign.getUserId().length() - 1),
                        assign.getBranchCode(),
                        assign.getTenantId());
            }
            return CommonResult.success(assigns, "操作成功！");
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }




}
