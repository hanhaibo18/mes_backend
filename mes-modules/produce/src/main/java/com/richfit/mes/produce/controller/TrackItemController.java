package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.produce.aop.OperationLog;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.entity.ItemMessageDto;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.ActionService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 跟单工序Controller
 */
@Slf4j
@Api(value = "跟单工序管理", tags = {"跟单工序管理"})
@RestController
@RequestMapping("/api/produce/track_item")
public class TrackItemController extends BaseController {

    @Autowired
    public TrackItemService trackItemService;
    @Autowired
    public BaseServiceClient baseServiceClient;
    @Autowired
    public TrackHeadService trackHeadService;
    @Autowired
    private ActionService actionService;

    public static String TRACK_HEAD_ID_NULL_MESSAGE = "跟单ID不能为空！";
    public static String TRACK_ITEM_ID_NULL_MESSAGE = "跟单工序ID不能为空！";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败，请重试！";

    @ApiOperation(value = "查询跟单最大完工的工序", notes = "查询跟单最大完工的工序")
    @GetMapping("/select_final_track_item")
    public CommonResult<List<TrackItem>> selectFinalTrackItems(@RequestParam String trackHeadId) {
        return CommonResult.success(trackItemService.selectFinalTrackItems(trackHeadId), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "操作工序id查询", notes = "操作工序id查询(服务间调用接口)")
    @GetMapping("/query/id")
    public TrackItem qyeryById(@RequestParam String id) {
        return trackItemService.getById(id);
    }

    @ApiOperation(value = "新增跟单工序", notes = "新增跟单工序")
    @PostMapping("/track_item")
    public CommonResult addTrackItem(@RequestBody TrackItem trackItem) {
        if (StringUtils.isNullOrEmpty(trackItem.getTrackHeadId())) {
            return CommonResult.failed(TRACK_HEAD_ID_NULL_MESSAGE);
        } else {
            boolean bool = trackItemService.save(trackItem);
            if (bool) {
                return CommonResult.success(trackItem, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "批量新增或修改跟单工序", notes = "批量新增或修改跟单工序")
    @PostMapping("/track_items")
    public CommonResult addOrUpdateTrackItems(@RequestBody List<TrackItem> trackItems) {

        List<TrackItem> addItems = trackItems.stream().filter(t -> {
            if (StringUtils.isNullOrEmpty(t.getId())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        List<TrackItem> updateItems = trackItems.stream().filter(t -> {
            if (StringUtils.isNullOrEmpty(t.getId())) {
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());

        if (addItems.size() > 0) {
            boolean addBool = trackItemService.saveBatch(addItems);
            if (!addBool) {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }

        if (updateItems.size() > 0) {
            boolean updateBool = trackItemService.updateBatchById(updateItems);
            if (!updateBool) {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }

        return CommonResult.success(trackItems, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "修改跟单工序", notes = "修改跟单工序")
    @PutMapping("/track_item")
    public CommonResult updateTrackItems(@RequestBody List<TrackItem> trackItems) {
        boolean bool = trackItemService.updateBatchById(trackItems);
        if (bool) {
            return CommonResult.success(trackItems, SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "删除跟单工序", notes = "删除跟单工序")
    @DeleteMapping("/track_item")
    public CommonResult deleteTrackItems(@RequestBody List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return CommonResult.failed(TRACK_ITEM_ID_NULL_MESSAGE);
        } else {
            boolean bool = trackItemService.removeByIds(ids);
            if (bool) {
                return CommonResult.success(null, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "查询跟单工序", notes = "根据跟单ID查询跟单工序")
    @GetMapping("/track_item")
    public CommonResult<List<TrackItem>> selectTrackHead(String id, String trackId, String optVer, String productNo, String isCurrent) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_head_id", trackId);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", productNo);
        }
        if (!StringUtils.isNullOrEmpty(optVer)) {
            queryWrapper.like("opt_ver", optVer);
        }
        if (!StringUtils.isNullOrEmpty(isCurrent)) {
            queryWrapper.like("is_current", isCurrent);
        }
        queryWrapper.orderByAsc("sequence_order_by");
        return CommonResult.success(trackItemService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询跟单工序(新20230329)", notes = "根据跟单ID查询跟单工序(新20230329)")
    @GetMapping("/track_item_new")
    public CommonResult<List<TrackItem>> selectTrackHeadNew(String id, String trackId, String optVer, String productNo) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_head_id", trackId);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", productNo);
        }
        if (!StringUtils.isNullOrEmpty(optVer)) {
            queryWrapper.like("opt_ver", optVer);
        }
        queryWrapper.orderByDesc("opt_sequence");
        List<TrackItem> list = trackItemService.list(queryWrapper);
        //去重操作(工序号+工序名 都一样认为重复)
        ArrayList<TrackItem> collect = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(trackItem -> trackItem.getOptName() + "-" + trackItem.getOptNo()))), ArrayList::new));
        //添加工序列表排序
        Collections.sort(collect, new Comparator<TrackItem>() {
            @Override
            public int compare(TrackItem o1, TrackItem o2) {
                return o1.getOptSequence() - o2.getOptSequence();
            }
        });
        return CommonResult.success(collect, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询跟单工序(当前外协工序)", notes = "根据跟单ID查询跟单工序(当前外协工序)")
    @PostMapping("/track_item/wxItems")
    public CommonResult<List<TrackItem>> selectTrackItemByIds(@RequestBody List<String> headIds) {
        List<TrackItem> trackItems = new ArrayList<>();
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        if (headIds.size() > 0) {
            queryWrapper.in("track_head_id", headIds)
                    .eq("opt_type", "3")
                    .eq("is_operation_complete", 0)
                    .eq("is_current", 1)
                    .orderByDesc("next_opt_sequence");
            trackItems = trackItemService.list(queryWrapper);
        }
        //根据FlowID分组
        Map<String, List<TrackItem>> map = trackItems.stream().collect(Collectors.groupingBy(TrackItem::getFlowId));
        //重新创建List每组数据组装完 向当前List填充
        List<TrackItem> trackItemList = new ArrayList<>();
        //拿到后续工序
        for (List<TrackItem> trackItem : map.values()) {
            trackItemList.addAll(trackItem);
            nextOpt(trackItem, trackItemList);
        }
        if (trackItemList.size() > 0) {
            for (TrackItem trackItem : trackItemList) {
                trackItem.setTrackNo(trackHeadService.getById(trackItem.getTrackHeadId()).getTrackNo());
            }
        }
        return CommonResult.success(trackItemList, SUCCESS_MESSAGE);
    }

    private void nextOpt(List<TrackItem> trackItemList, List<TrackItem> newTrackItemList) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper();
        queryWrapper.eq("original_opt_sequence", trackItemList.get(0).getNextOptSequence());
        queryWrapper.eq("flow_id", trackItemList.get(0).getFlowId());
        queryWrapper.eq("opt_type", "3");
        queryWrapper.orderByDesc("original_opt_sequence");
        List<TrackItem> list = trackItemService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            newTrackItemList.addAll(list);
            nextOpt(list, newTrackItemList);
        }
    }

    @ApiOperation(value = "查询跟单分流工序", notes = "根据跟单ID查询跟单分流工序")
    @GetMapping("/track_flow_item")
    public CommonResult<List<TrackItem>> trackFlowItem(String id, String flowId, String optVer) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(flowId)) {
            queryWrapper.eq("flow_id", flowId);
        }
        if (!StringUtils.isNullOrEmpty(optVer)) {
            queryWrapper.like("opt_ver", optVer);
        }
        queryWrapper.orderByAsc("sequence_order_by");
        return CommonResult.success(trackItemService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询跟单分流合并工序", notes = "根据跟单ID查询跟单分流合并的工序")
    @GetMapping("/track_flow_item_merge")
    public CommonResult<List<TrackItem>> trackFlowItemMerge(String trackId) {
//        List<TrackItem> trackItemScheduleList = new ArrayList<>();
//        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
//        if (!StringUtils.isNullOrEmpty(trackId)) {
//            queryWrapper.eq("track_head_id", trackId);
//        }
//        queryWrapper.orderByAsc("sequence_order_by");
//        List<TrackItem> trackItems = trackItemService.list(queryWrapper);
        List<TrackItem> trackItemList = new ArrayList<>(trackItemService.queryItemByTrackHeadId(trackId));
//        for (TrackItem ti : trackItemList) {
//            //是否需要理化检测状态值赋值
//            String isEntrust = "0";
//            List<OperationTypeSpec> operationTypeSpecs = baseServiceClient.queryOperationTypeSpecByType(ti.getOptType(), ti.getBranchCode(), SecurityUtils.getCurrentUser().getTenantId());
//            if (CollectionUtils.isNotEmpty(operationTypeSpecs)) {
//                for (OperationTypeSpec operationTypeSpec : operationTypeSpecs) {
//                    if ("qualityFileType-10".equals(operationTypeSpec.getPropertyValue())) {
//                        isEntrust = "1";
//                    }
//                }
//            } else {
//                List<RouterCheck> routerChecks = baseServiceClient.queryRouterList(ti.getOptId(), "质量资料", ti.getBranchCode(), SecurityUtils.getCurrentUser().getTenantId());
//                List<RouterCheck> filters = routerChecks.stream().filter(item -> ("qualityFileType-10").equals(item.getPropertyDefaultvalue())).collect(Collectors.toList());
//                if (filters.size() > 0) {
//                    isEntrust = "1";
//                }
//            }
//            //材料委托单只有第一次查询的时候赋值，如果被修改过直接查item中的值
//            if (StringUtils.isNullOrEmpty(ti.getIsEntrust())) {
//                ti.setIsEntrust(isEntrust);
//                trackItemService.updateById(ti);
//            }
//        }
        return CommonResult.success(trackItemList, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "激活工序", notes = "激活工序")
    @GetMapping("/active_trackitem")
    public CommonResult<List<TrackItem>> activeTrackItem(String trackHeadId, Boolean isGoNextOpt) {
        List<TrackItem> items = this.selectTrackHead(null, trackHeadId, null, null, null).getData();

        List<TrackItem> activeItems = new ArrayList();
        // 跟单初始化，激活第1个工序
        if (!isGoNextOpt) {
            int minOrder = Integer.MAX_VALUE;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getSequenceOrderBy() > minOrder && !items.get(i).getIsFinalComplete().equals("1")) {
                    minOrder = items.get(i).getSequenceOrderBy();
                }
                items.get(i).setIsCurrent(0);
                items.get(i).setIsDoing(0);
                items.get(i).setIsFinalComplete("0");
                trackItemService.updateById(items.get(i));
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getSequenceOrderBy() == minOrder) {
                    items.get(i).setIsCurrent(1);
                    trackItemService.updateById(items.get(i));
                }
            }
        } else {
            //跟单工序跳转，获取当前激活工序，并激活下个工序
            int curOrder = -1;
            Boolean curOrderEnable = true;
            //下道激活工序
            int nextOrder = -1;
            //获取当前激活工序

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getIsDoing() == 1) {
                    curOrder = items.get(i).getSequenceOrderBy();
                    //如果质检不需要或者质检需要并将质检结果是1的话，才能得到当前工序状态，否则是否
                    if ((items.get(i).getIsExistQualityCheck() == 1 && items.get(i).getQualityResult() == 1) || items.get(i).getIsExistQualityCheck() == 0) {

                    } else {
                        curOrderEnable = false;
                    }
                }
            }
            //下道激活工序
            for (int i = 0; i < items.size(); i++) {
                if (curOrder > -1) {
                    if (items.get(i).getSequenceOrderBy() > curOrder) {
                        nextOrder = items.get(i).getSequenceOrderBy();
                    }
                }
            }
            for (int i = 0; i < items.size(); i++) {
                // 将上个工序设置为完成，不是当前激活工序
                if (curOrder > -1 && curOrderEnable) {
                    if (items.get(i).getSequenceOrderBy() == curOrder) {
                        items.get(i).setIsDoing(0);
                        items.get(i).setIsCurrent(0);
                        items.get(i).setIsFinalComplete("1");
                        trackItemService.updateById(items.get(i));
                    }

                    // 将下个工序设置为激活工序
                    if (nextOrder > -1) {
                        if (items.get(i).getSequenceOrderBy() == nextOrder) {
                            items.get(i).setIsCurrent(1);
                            activeItems.add(items.get(i));
                            trackItemService.updateById(items.get(i));
                        }
                    }
                }
            }
        }
        return CommonResult.success(activeItems, SUCCESS_MESSAGE);

    }

    /**
     * 功能描述: 根据跟单ID查询跟单工序
     *
     * @param trackNo
     * @Author: xinYu.hou
     * @Date: 2022/5/9 8:02
     * @return: List<TrackItem>
     **/
    @ApiOperation(value = "根据跟单ID查询跟单工序", notes = "根据跟单ID查询跟单工序")
    @GetMapping("/queryTrackItemByTrackNo/{trackNo}")
    public CommonResult<List<TrackItem>> queryTrackItemByTrackNo(@PathVariable String trackNo) {
        return CommonResult.success(trackItemService.queryTrackItemByTrackNo(trackNo));
    }


    @ApiOperation(value = "重置跟单工序状态", notes = "重置跟单工序状态 （resetType 1:重置派工,2:重置报工,3:重置质检,4:重置调度审核,5:重置当前工序的所有记录）")
    @GetMapping("/resetStatus")
    public CommonResult<String> resetStatus(String tiId, Integer resetType, HttpServletRequest request) {
        return CommonResult.success(trackItemService.resetStatus(tiId, resetType, request));
    }

    @ApiOperation(value = "更新至下工序", notes = "根据跟单ID更新至下工序")
    @GetMapping("/nextSequence")
    public CommonResult<String> nextSequence(String flowId, HttpServletRequest request) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        //获取该flowId的当前工序
        queryWrapper.eq("flow_id", flowId);
        queryWrapper.eq("is_current", 1);
        List<TrackItem> list = trackItemService.list(queryWrapper);
        List<String> trackNoList = new ArrayList<>();
        List<String> optNoList = new ArrayList<>();
        List<String> optNameList = new ArrayList<>();
        for (TrackItem trackItem : list) {
            trackNoList.add(trackHeadService.getById(trackItem.getTrackHeadId()).getTrackNo());
            optNoList.add(trackItem.getOptNo());
            optNameList.add(trackItem.getOptName());
        }
        String error = trackItemService.nextSequence(flowId);
        if ("success".equals(error)) {
            actionService.saveAction(
                    ActionUtil.buildAction(list.get(0).getBranchCode(), "4", "2",
                            "更新至下工序，跟单号：" + trackNoList + "，更新前工序号：" + optNoList + "，更新前工序名：" + optNameList, OperationLogAspect.getIpAddress(request)));
            return CommonResult.success("success");
        } else {
            return CommonResult.failed(error);
        }
    }

    @ApiOperation(value = "回滚至上工序", notes = "根据跟单ID回滚至上工序")
    @GetMapping("/backSequence")
    @OperationLog
    public CommonResult<String> backSequence(String flowId, HttpServletRequest request) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        //获取该flowId的当前工序
        queryWrapper.eq("flow_id", flowId);
        queryWrapper.eq("is_current", 1);
        List<TrackItem> list = trackItemService.list(queryWrapper);
        List<String> trackNoList = new ArrayList<>();
        List<String> optNoList = new ArrayList<>();
        List<String> optNameList = new ArrayList<>();
        for (TrackItem trackItem : list) {
            trackNoList.add(trackHeadService.getById(trackItem.getTrackHeadId()).getTrackNo());
            optNoList.add(trackItem.getOptNo());
            optNameList.add(trackItem.getOptName());
        }

        String result = trackItemService.backSequence(flowId);
        if ("success".equals(result)) {
            actionService.saveAction(
                    ActionUtil.buildAction(list.get(0).getBranchCode(), "4", "2",
                            "回滚至上工序，跟单号：" + trackNoList + "，回滚前工序号：" + optNoList + "，回滚前工序名：" + optNameList, OperationLogAspect.getIpAddress(request)));
            return CommonResult.success("success");
        }
        return CommonResult.failed(result);
    }


    @GetMapping("/queryItemMessageDto")
    @ApiOperation(value = "查询工序信息", notes = "根据工序Id查询工序信息")
    public CommonResult<ItemMessageDto> queryItemMessageDto(String tiId) {
        return CommonResult.success(trackItemService.queryItemMessageDto(tiId));
    }

    @GetMapping("query_disqualification_item")
    @ApiOperation(value = "质检创建不合格信息查询", notes = "质检创建你不合格查询")
    public CommonResult<DisqualificationItemVo> queryDisqualificationByItem(String tiId, String branchCode) {
        return CommonResult.success(trackItemService.queryDisqualificationByItem(tiId, branchCode));
    }

}
