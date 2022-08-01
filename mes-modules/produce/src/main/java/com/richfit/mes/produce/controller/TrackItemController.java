package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.service.TrackItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 跟单工序Controller
 */
@Slf4j
@Api("跟单工序管理")
@RestController
@RequestMapping("/api/produce/track_item")
public class TrackItemController extends BaseController {

    @Autowired
    public TrackItemService trackItemService;

    public static String TRACK_HEAD_ID_NULL_MESSAGE = "跟单ID不能为空！";
    public static String TRACK_ITEM_ID_NULL_MESSAGE = "跟单工序ID不能为空！";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败，请重试！";

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
    public CommonResult<List<TrackItem>> selectTrackHead(String id, String trackId, String optVer) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_head_id", trackId);
        }
        if (!StringUtils.isNullOrEmpty(optVer)) {
            queryWrapper.like("opt_ver", optVer);
        }
        queryWrapper.orderByAsc("sequence_order_by");
        return CommonResult.success(trackItemService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "激活工序", notes = "激活工序")
    @GetMapping("/active_trackitem")
    public CommonResult<List<TrackItem>> activeTrackItem(String trackHeadId, Boolean isGoNextOpt) {
        List<TrackItem> items = this.selectTrackHead(null, trackHeadId, null).getData();

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
    public CommonResult<String> resetStatus(String tiId, Integer resetType) {
        return CommonResult.success(trackItemService.resetStatus(tiId, resetType));
    }

    @ApiOperation(value = "更新至下工序", notes = "根据跟单ID更新至下工序")
    @GetMapping("/nextSequence")
    public CommonResult<String> nextSequence(String thId) {
        return CommonResult.success(trackItemService.nextSequence(thId));
    }

    @ApiOperation(value = "回滚至上工序", notes = "根据跟单ID回滚至上工序")
    @GetMapping("/backSequence")
    public CommonResult<String> backSequence(String thId) {
        return CommonResult.success(trackItemService.backSequence(thId));
    }

}
