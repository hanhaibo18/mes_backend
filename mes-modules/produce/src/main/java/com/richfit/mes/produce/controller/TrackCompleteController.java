package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.common.model.base.SequenceSite;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.BaseServiceClient;
import io.swagger.annotations.ApiImplicitParams;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 马峰
 * @Description 跟单派工Controller
 */
@Slf4j
@Api("跟单派工")
@RestController
@RequestMapping("/api/produce/trackcomplete")
public class TrackCompleteController extends BaseController {

    @Autowired
    public TrackCompleteService trackCompleteService;
    @Autowired
    public TrackAssignService trackAssignService;
    @Autowired
    public TrackItemService trackItemService;
    @Autowired
    public TrackHeadService trackHeadService;
    @Autowired
    private LineStoreService lineStoreService;
    @Autowired
    private PlanService planService;
    @Autowired
    private BaseServiceClient baseServiceClient;

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
    public CommonResult<IPage<TrackComplete>> page(int page, int limit, String siteId, String tiId, String trackNo, String startTime, String endTime, String optType) {
        try {
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            if (!StringUtils.isNullOrEmpty(tiId)) {
                queryWrapper.eq("ti_id", tiId);
            }
            if (!StringUtils.isNullOrEmpty(trackNo)) {
                queryWrapper.eq("track_no", trackNo);
            }
            if (!StringUtils.isNullOrEmpty(siteId)) {
                queryWrapper.apply("assign_id in (select id from produce_assign where site_id='" + siteId + "')");
            }
            if (!StringUtils.isNullOrEmpty(startTime)) {
                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");

            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + endTime + "')");

            }
            //外协报工判断过滤，外协报工类型是4

            if ("4".equals(optType)) {
                queryWrapper.apply("ti_id in (select id from produce_track_item where opt_type = 4)");
            } else {
                queryWrapper.apply("ti_id in (select id from produce_track_item where opt_type <> 4)");
            }
            // todo 如果是管理员或租户管理员，那么不过滤完工用户ID
            if (null != SecurityUtils.getCurrentUser()) {
                TenantUserDetails user = SecurityUtils.getCurrentUser();

                queryWrapper.apply("(complete_by ='" + user.getUserId() + "' || complete_by is null || complete_by ='' || user_id ='" + user.getUserId() + "' || user_id is null || user_id ='' )");
            }

            queryWrapper.orderByDesc("modify_time");
            IPage<TrackComplete> completes = trackCompleteService.page(new Page<TrackComplete>(page, limit), queryWrapper);
            return CommonResult.success(completes);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增派工", notes = "新增派工")
    @ApiImplicitParam(name = "complete", value = "派工", required = true, dataType = "Complete", paramType = "path")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackComplete> addComplete(@RequestBody TrackComplete complete) {

        Assign assign = trackAssignService.getById(complete.getAssignId());
        List<TrackComplete> cs = this.findByAssignId(assign.getId()).getData();
        TrackItem trackItem = trackItemService.getById(assign.getTiId());
        //合计当前派工下的已报工数
        Double sum = complete.getCompletedQty();
        for (int i = 0; i < cs.size(); i++) {
            sum += cs.get(i).getCompletedQty();
        }
        //超出处理
        if (sum > assign.getQty()) {
            return CommonResult.failed("超出报工数，最大报工数为" + String.valueOf(assign.getQty() - sum + complete.getCompletedQty()));
        }
        if (StringUtils.isNullOrEmpty(complete.getTiId())) {
            return CommonResult.failed("编码不能为空！");
        } else {

            complete.setModifyTime(new Date());
            complete.setCreateTime(new Date());
            boolean bool = trackCompleteService.save(complete);
            //如报工完成，切不需要质检和审核，则跳转下个工序
            if (sum == assign.getQty() && trackItem.getAssignableQty() == 0 && trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
                try {
                    this.activeTrackItem(trackItem);
                } catch (Exception e) {
                }

            }
            //报工数已满，派工状态设置为2
            if (sum == assign.getQty()) {
                assign.setState(2);
                trackAssignService.updateById(assign);
            }
            if (bool) {
                return CommonResult.success(complete, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "新增派工", notes = "新增派工")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "qcpersonId", value = "质检人员", required = true, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "complete", value = "派工", required = true, dataType = "Object", paramType = "query")
    })
    @PostMapping("/batchAdd")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackComplete[]> batchAddComplete(@RequestBody TrackComplete[] completes, String qcpersonId) {
        boolean bool = true;
        boolean isNext = true;
        int curOptSequence = -1;
        String trackId = "";
        for (TrackComplete complete : completes) {
            Assign assign = trackAssignService.getById(complete.getAssignId());
            List<TrackComplete> cs = this.findByAssignId(assign.getId()).getData();
            TrackItem trackItem = trackItemService.getById(assign.getTiId());

            trackId = trackItem.getTrackHeadId();
            if (StringUtils.isNullOrEmpty(trackItem.getStartDoingUser())) {
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(complete.getUserId());
            }
            curOptSequence = trackItem.getOptSequence();
            //合计当前派工下的已报工数
            Double sum = complete.getCompletedQty();
            for (int i = 0; i < cs.size(); i++) {
                sum += cs.get(i).getCompletedQty();
            }
            //报工数超出派工数处理
            if (sum > assign.getQty()) {
                return CommonResult.failed("超出报工数，最大报工数为" + String.valueOf(assign.getQty() - sum + complete.getCompletedQty()));
            }
            if (StringUtils.isNullOrEmpty(complete.getTiId())) {
                return CommonResult.failed("编码不能为空！");
            } else {

                complete.setModifyTime(new Date());
                complete.setCreateTime(new Date());
                if (!trackCompleteService.save(complete)) {
                    bool = false;
                }
                //如报工完成，切不需要质检和审核，则可以跳转下个工序
                if (sum == assign.getQty() && trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
                } else {
                    isNext = false;
                }
                //更新工序完成数量
                if (sum <= assign.getQty() && trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
                    trackItem.setCompleteQty(sum.intValue());
                    assign.setAvailQty(assign.getQty() - sum.intValue());
                    trackAssignService.updateById(assign);
                    trackItemService.updateById(trackItem);
                }
                //判断整个工序是否完成，如果完成，则将完成数量和完成状态写入
                if (sum == assign.getQty()) {
                    if (trackItem.getAssignableQty() == 0) {
                        QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
                        queryWrapper.eq("ti_id", trackItem.getId());
                        List<Assign> assigns = trackAssignService.list(queryWrapper);
                        int isComplete = 1;
                        double completeQty = sum;
                        for (int j = 0; j < assigns.size(); j++) {
                            if (!assigns.get(j).getId().equals(assign.getId()) && assigns.get(j).getState() != 2) {
                                isComplete = 0;
                            }
                            if (!assigns.get(j).getId().equals(assign.getId()) && assigns.get(j).getState() == 2) {
                                sum += assigns.get(j).getQty();
                            }
                        }

                        trackItem.setOperationCompleteTime(new Date());
                        trackItem.setCompleteQty(sum.intValue());
                        trackItem.setIsOperationComplete(isComplete);
                        if (trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
                            trackItem.setIsFinalComplete(String.valueOf(isComplete));
                        }
                        trackItemService.updateById(trackItem);
                    }
                    //派工状态设置为完成
                    assign.setState(2);
                    trackAssignService.updateById(assign);
                }

            }
            if (isNext && trackItem.getAssignableQty() == 0) {
                this.activeTrackItem(trackItem);
            }
        }

        if (bool) {
            return CommonResult.success(completes, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }

    @ApiOperation(value = "新增外协派工", notes = "新增外协派工")
    @ApiImplicitParam(name = "complete", value = "派工", required = true, dataType = "Complete", paramType = "path")
    @PostMapping("/batchAddOutComplete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackComplete[]> batchAddOutComplete(@RequestBody TrackComplete[] completes) {
        boolean bool = true;

        for (TrackComplete complete : completes) {
            TrackItem trackItem = trackItemService.getById(complete.getTiId());

            if (StringUtils.isNullOrEmpty(trackItem.getStartDoingUser())) {
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(complete.getUserId());
            }

            //合计当前派工下的已报工数
            Double sum = complete.getCompletedQty();
            trackItem.setCompleteQty(sum.intValue());
            complete.setAssignId(complete.getTiId());
            complete.setModifyTime(new Date());
            complete.setCreateTime(new Date());
            trackItem.setOperationCompleteTime(new Date());
            trackItem.setCompleteQty(sum.intValue());
            trackItem.setIsOperationComplete(1);
            if (trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
                trackItem.setIsFinalComplete(String.valueOf(1));
            }
            trackCompleteService.save(complete);
            trackItemService.updateById(trackItem);
            this.activeTrackItem(trackItem);

        }

        if (bool) {
            return CommonResult.success(completes, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }

    @ApiOperation(value = "修改外协派工", notes = "修改外协派工")
    @ApiImplicitParam(name = "device", value = "派工", required = true, dataType = "Complete", paramType = "path")
    @PostMapping("/updateOutComplete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackComplete> updateOutComplete(@RequestBody TrackComplete complete) {

        trackCompleteService.updateById(complete);
        return CommonResult.success(complete, "操作成功！");

    }

    @ApiOperation(value = "删除外协派工", notes = "根据id删除外协派工")
    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "path")
    @PostMapping("/deleteOut")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackComplete> deleteOut(@RequestBody String[] ids) {


        String msg = "";
        for (int i = 0; i < ids.length; i++) {
            TrackComplete trackComplete = trackCompleteService.getById(ids[i]);



            TrackItem trackItem = trackItemService.getById(trackComplete.getTiId());
            if (null == trackItem) {
                trackCompleteService.removeById(ids[i]);
            } else {

                List<TrackComplete> cs = this.findby(trackItem.getTrackHeadId()).getData();
                //判断跟单号已质检完成，报工无法取消
                if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                    msg += "跟单号已质检完成，报工无法取消！";
                }
                //判断跟单号已质检完成，报工无法取消
                if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                    msg += "跟单号已调度完成，报工无法取消！";
                }
                //判断后置工序是否已报工，否则不可回滚
                for (int j = 0; j < cs.size(); j++) {
                    TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                    if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                        return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已报工，需要先取消后序工序");
                    }
                }



                //将后置工序IS_CURRENT设置为否，状态为1

                List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", trackItem.getTrackHeadId()).orderByAsc("opt_sequence"));
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getOptSequence() > trackItem.getOptSequence() && items.get(j).getIsCurrent() == 1) {
                        items.get(j).setIsCurrent(0);
                        items.get(j).setIsDoing(0);
                        items.get(j).setCompleteQty(0);
                        items.get(j).setIsFinalComplete("0");
                        trackItemService.updateById(items.get(j));
                    }
                }
                //将当前工序设置为激活
                if (msg.equals("")) {
                    trackItem.setCompleteQty(0);
                    trackItem.setIsDoing(0);
                    trackItem.setIsCurrent(1);
                    trackItem.setIsFinalComplete("0");
                    trackItem.setIsOperationComplete(0);
                    trackItemService.updateById(trackItem);
                    TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                    trackHead.setStatus("1");
                    trackHeadService.updateById(trackHead);




                    trackCompleteService.removeById(ids[i]);
                }
            }

        }
        if (msg.equals("")) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！" + msg);
        }

    }

    @ApiOperation(value = "修改派工", notes = "修改派工")
    @ApiImplicitParam(name = "device", value = "派工", required = true, dataType = "Complete", paramType = "path")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackComplete> updateComplete(@RequestBody TrackComplete complete) {

        Assign assign = trackAssignService.getById(complete.getAssignId());
        List<TrackComplete> cs = this.findByAssignId(assign.getId()).getData();
        TrackItem trackItem = trackItemService.getById(assign.getTiId());
        if (trackItem.getIsExistQualityCheck().equals(1) && trackItem.getIsQualityComplete().equals(1)) {
            return CommonResult.failed("无法修改，当前工序【" + trackItem.getOptName() + "】已质检完成，需先取消质检审核");

        }
        if (trackItem.getIsExistScheduleCheck().equals(1) && trackItem.getIsScheduleComplete().equals(1)) {
            return CommonResult.failed("无法修改，当前工序【" + trackItem.getOptName() + "】已调度完成，需先取消调度审核");

        }
        //获取当前派工的总报工数
        Double sum = complete.getCompletedQty();
        for (int i = 0; i < cs.size(); i++) {
            if (!cs.get(i).getId().equals(complete.getId())) {
                sum += cs.get(i).getCompletedQty();
            }
            TrackItem cstrackItem = trackItemService.getById(cs.get(i).getTiId());
            if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                return CommonResult.failed("无法修改，已有后序工序【" + cstrackItem.getOptName() + "】已报工，需要先取消后序工序");

            }

        }
        // 报工数超出派工数处理
        if (sum > assign.getQty()) {
            return CommonResult.failed("超出报工数，最大报工数为" + String.valueOf(assign.getQty() - sum + complete.getCompletedQty()));
        }

        if (StringUtils.isNullOrEmpty(complete.getTiId())) {
            return CommonResult.failed("编码不能为空！");
        } else {
            complete.setModifyTime(new Date());
            boolean bool = trackCompleteService.updateById(complete);
            if (sum == assign.getQty()) {
                if (trackItem.getAssignableQty() == 0 && trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
                    this.activeTrackItem(trackItem);
                }
                assign.setState(2);
                trackAssignService.updateById(assign);
            }
            //判断整个工序是否完成，如果完成，则将完成数量和完成状态写入
            QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
            queryWrapper.eq("ti_id", trackItem.getId());
            List<Assign> assigns = trackAssignService.list(queryWrapper);
            int isComplete = 1;
            if (sum < assign.getQty()) {
                isComplete = 0;
            }

            int completeQty = sum.intValue();
            for (int j = 0; j < assigns.size(); j++) {
                if (!assigns.get(j).getId().equals(assign.getId()) && assigns.get(j).getState() != 2) {
                    isComplete = 0;
                }
                if (!assigns.get(j).getId().equals(assign.getId()) && assigns.get(j).getState() == 2) {
                    completeQty += assigns.get(j).getQty();
                }
            }
            trackItem.setCompleteQty(completeQty);
            trackItem.setIsOperationComplete(isComplete);
            if (trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
                trackItem.setIsFinalComplete(String.valueOf(isComplete));
            }
            trackItemService.updateById(trackItem);
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            trackHead.setStatus(String.valueOf(isComplete));
            trackHeadService.updateById(trackHead);
            if (sum < assign.getQty()) {

                assign.setAvailQty(assign.getQty() - sum.intValue());
                assign.setState(1);
                trackAssignService.updateById(assign);
            }
            if (bool) {
                return CommonResult.success(complete, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "报工查询", notes = "报工查询")
    @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<TrackComplete>> find(String tiId) {

        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
        if (!StringUtils.isNullOrEmpty(tiId)) {
            queryWrapper.eq("ti_id", tiId);
        }

        queryWrapper.orderByAsc("modify_time");
        List<TrackComplete> result = trackCompleteService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "报工查询", notes = "报工查询")
    @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/findByAssignId")
    public CommonResult<List<TrackComplete>> findByAssignId(String assignId) {

        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
        if (!StringUtils.isNullOrEmpty(assignId)) {
            queryWrapper.eq("assign_id", assignId);
        }

        queryWrapper.orderByAsc("modify_time");
        List<TrackComplete> result = trackCompleteService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "报工查询", notes = "报工查询")
    @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/findby")
    public CommonResult<List<TrackComplete>> findby(String id) {

        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("track_id", id);
        } else {
            queryWrapper.eq("track_id", "-1");
        }


        queryWrapper.orderByAsc("modify_time");
        List<TrackComplete> result = trackCompleteService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "删除派工", notes = "根据id删除派工")
    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "path")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackComplete> delete(@RequestBody String[] ids) {


        String msg = "";
        for (int i = 0; i < ids.length; i++) {
            TrackComplete trackComplete = trackCompleteService.getById(ids[i]);

            Assign assign = trackAssignService.getById(trackComplete.getAssignId());
            if (null == assign) {
                trackCompleteService.removeById(ids[i]);
            } else {
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                if (null == trackItem) {
                    trackCompleteService.removeById(ids[i]);
                } else {

                    List<TrackComplete> cs = this.findby(trackItem.getTrackHeadId()).getData();
                    //判断跟单号已质检完成，报工无法取消
                    if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                        msg += assign.getTrackNo() + "跟单号已质检完成，报工无法取消！";
                    }
                    //判断跟单号已质检完成，报工无法取消
                    if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                        msg += assign.getTrackNo() + "跟单号已调度完成，报工无法取消！";
                    }
                    //判断后置工序是否已报工，否则不可回滚
                    for (int j = 0; j < cs.size(); j++) {
                        TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                        if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                            return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已报工，需要先取消后序工序");
                        }
                    }
                    //判断后置工序是否已派工，否则不可回滚
                    QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
                    queryWrapper.eq("ti_id", trackItem.getId());
                    List<Assign> assigns = trackAssignService.list(queryWrapper);
                    for (int j = 0; j < assigns.size(); j++) {
                        TrackItem cstrackItem = trackItemService.getById(assigns.get(j).getTiId());
                        if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                            return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已派工，需要先取消后序工序");
                        }
                    }


                    //将后置工序IS_CURRENT设置为否，状态为1

                    List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", trackItem.getTrackHeadId()).orderByAsc("opt_sequence"));
                    for (int j = 0; j < items.size(); j++) {
                        if (items.get(j).getOptSequence() > trackItem.getOptSequence() && items.get(j).getIsCurrent() == 1) {
                            items.get(j).setIsCurrent(0);
                            items.get(j).setIsDoing(0);
                            items.get(j).setCompleteQty(0);
                            items.get(j).setIsFinalComplete("0");
                            trackItemService.updateById(items.get(j));
                        }
                    }
                    //将当前工序设置为激活
                    if (msg.equals("")) {
                        trackItem.setCompleteQty(0);
                        trackItem.setIsDoing(0);
                        trackItem.setIsCurrent(1);
                        trackItem.setIsFinalComplete("0");
                        trackItem.setIsOperationComplete(0);
                        trackItemService.updateById(trackItem);
                        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                        trackHead.setStatus("1");
                        trackHeadService.updateById(trackHead);
                        assign.setAvailQty(assign.getQty() + trackComplete.getCompletedQty().intValue());

                        assign.setState(1);
                        trackAssignService.updateById(assign);
                        trackCompleteService.removeById(ids[i]);
                    }
                }
            }
        }
        if (msg.equals("")) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！" + msg);
        }

    }

    @ApiOperation(value = "激活工序", notes = "激活工序")
    @GetMapping("/active_trackitem")
    public String activeTrackItem(TrackItem curItem) {
        String msg = "";
        TrackItemController c = new TrackItemController();
        c.trackItemService = this.trackItemService;
        List<TrackItem> items = c.selectTrackHead(null, curItem.getTrackHeadId(), null).getData();
        List<TrackItem> activeItems = new ArrayList();

        //跟单工序跳转，获取当前激活工序，并激活下个工序          
        Boolean curOrderEnable = true;
        //下道激活工序
        int nextOrder = -1;
        //获取当前激活工序

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getIsCurrent() == 1 && curItem.getOptSequence() == items.get(i).getOptSequence()) {
                //如果是当前工序序号的工序，质检不需要或者质检需要并将质检结果是1的话，才能得到当前工序状态，否则是否
                if (((items.get(i).getIsExistQualityCheck() == 1 && items.get(i).getIsQualityComplete() == 1) || items.get(i).getIsExistQualityCheck() == 0) && ((items.get(i).getIsExistScheduleCheck() == 1 && items.get(i).getIsScheduleComplete() == 1) || items.get(i).getIsExistScheduleCheck() == 0)) {
                } else {
                    curOrderEnable = false;
                }
            }
        }
        //判断同工序序号的其他工序是否已完成
        for (int i = 0; i < items.size(); i++) {
            if (curItem.getOptSequence() == items.get(i).getOptSequence() && !items.get(i).getId().equals(curItem.getId())) {
                if (!items.get(i).getIsFinalComplete().equals("1")) {
                    curOrderEnable = false;
                }
            }

        }


        //下道激活工序
        for (int i = 0; i < items.size(); i++) {
            if (curItem.getOptSequence() > -1) {
                if (items.get(i).getOptSequence() > curItem.getOptSequence()) {
                    nextOrder = items.get(i).getOptSequence();
                    break;
                }
            }
        }
        for (int i = 0; i < items.size(); i++) {
            // 将上个工序设置为完成，不是当前激活工序
            if (curItem.getOptSequence() > -1 && curOrderEnable) {
                if (items.get(i).getOptSequence() == curItem.getOptSequence()) {
                    items.get(i).setIsCurrent(0);
                    items.get(i).setIsDoing(0);
                    items.get(i).setIsFinalComplete("1");
                    trackItemService.updateById(items.get(i));
                    if (nextOrder == -1) {
                        TrackHead trackHead = trackHeadService.getById(items.get(i).getTrackHeadId());
                        trackHead.setStatus("2");
                        trackHead.setCompleteTime(new Date());
                        trackHeadService.updateById(trackHead);

                        //设置产品完工
                        lineStoreService.changeStatus(trackHead);

                        //设置计划状态
                        planService.updatePlanStatus(trackHead.getWorkPlanNo(), trackHead.getTenantId());
                        /*QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
                         queryWrapper.eq("track_no", trackHead.getTrackNo());
                         List<LineStore> lineStore =  lineStoreService.list(queryWrapper);
                         for(int ii =0;i<lineStore.size();i++) {
                         lineStore.get(ii).setStatus("1");
                         lineStoreService.updateById(lineStore.get(ii));
                         }*/
                    } else {
                    }
                }

                // 将下个工序设置为激活工序
                if (nextOrder > -1) {
                    if (items.get(i).getOptSequence() == nextOrder) {
                        items.get(i).setIsCurrent(1);
                        items.get(i).setIsDoing(0);
                        activeItems.add(items.get(i));
                        trackItemService.updateById(items.get(i));
                        // todo 如果自动派工，那么执行派工操作
                        if (null!=items.get(i-1) &&null!=items.get(i-1).getIsAutoSchedule()&&items.get(i-1).getIsAutoSchedule() == 1) {
                            String deviceId = "";
                            String userId = "ba7dd26f0a669f9e09343f9b579b0321";
                            List<SequenceSite> sequenceSites = baseServiceClient.getSequenceDevice(items.get(i).getOptId(), null, "device", null, "1").getData();
                            if (sequenceSites.size() == 1) {
                                deviceId = sequenceSites.get(0).getSiteId();
                                if (!StringUtils.isNullOrEmpty(deviceId)) {
                                   List<DevicePerson> devicePersons = baseServiceClient.getDevicePerson(deviceId, null, null, "1").getData();
                                   if (devicePersons.size() == 1) {
                                        userId = devicePersons.get(0).getUserId();
                                    }
                                }
                            }
                            if (!StringUtils.isNullOrEmpty(deviceId) && !StringUtils.isNullOrEmpty(userId)) {
                                TrackHead trackHead = trackHeadService.getById(items.get(i).getTrackHeadId());
                                Assign assign = new Assign();
                                assign.setTiId(items.get(i).getId());
                                assign.setTrackId(items.get(i).getTrackHeadId());
                                assign.setTrackNo(trackHead.getTrackNo());
                                assign.setQty(items.get(i).getBatchQty());
                                assign.setAvailQty(items.get(i).getBatchQty());
                                assign.setDeviceId(deviceId);
                                assign.setUserId(userId);
                                assign.setPriority(0);
                                assign.setState(0);
                                assign.setAssignBy("system");
                                if (null != SecurityUtils.getCurrentUser()) {
                                    String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                                    assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                                    assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                                }
                                assign.setModifyTime(new Date());
                                assign.setCreateTime(new Date());
                                trackAssignService.save(assign);
                                // 将跟单工序可派工数量改为0
                                items.get(i).setAssignableQty(0);
                                trackItemService.updateById(items.get(i));
                                //将跟单状态改为在制
                                trackHead.setStatus("1");
                                trackHeadService.updateById(trackHead);

                            }
                        }
                    }
                }
            }
        }


        return msg;

    }
}