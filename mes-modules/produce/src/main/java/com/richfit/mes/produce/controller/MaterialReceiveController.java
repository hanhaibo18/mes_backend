package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/8/3 10:52
 */
@Slf4j
@Api(tags = "物料接收(装配)")
@RestController
@RequestMapping("/api/produce/material_receive")
public class MaterialReceiveController extends BaseController {

    @Autowired
    MaterialReceiveService materialReceiveService;

    @Autowired
    MaterialReceiveDetailService materialReceiveDetailService;

    @Autowired
    TrackAssemblyService trackAssemblyService;

    @Autowired
    LineStoreService lineStoreService;

    @ApiOperation(value = "分页查询物料接收", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询物料接收")
    @GetMapping("/query/page")
    public CommonResult<IPage<MaterialReceive>> selectTrackHead(@ApiParam(value = "跟单号") @RequestParam(required = false) String trackNo,
                                                                @ApiParam(value = "页码") @RequestParam(required = false) Integer page,
                                                                @ApiParam(value = "条数") @RequestParam(required = false) Integer limit,
                                                                @ApiParam(value = "配送单号") @RequestParam(required = false) String deliveryNo,
                                                                @ApiParam(value = "状态") @RequestParam(required = false) List<String> states) {
        QueryWrapper<MaterialReceive> queryWrapper = new QueryWrapper<MaterialReceive>();
        if (ObjectUtils.isNotNull(states)) {
            queryWrapper.in("mr.state", states);
        }
        if (!StringUtils.isNullOrEmpty(deliveryNo)) {
            queryWrapper.like("mr.delivery_no", deliveryNo);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.like("prn.track_head_id", trackNo);
        }
        return CommonResult.success( materialReceiveService.getPage(new Page<MaterialReceive>(page, limit),queryWrapper));
    }

    @ApiOperation(value = "查询本次配送明细信息", notes = "根据跟单号、分页查询本次配送明细信息")
    @GetMapping("/get_the_delivery_detail")
    public CommonResult<List<MaterialReceiveDetail>> getThisDeliveryDetail(@ApiParam(value = "配送单号") @RequestParam(required = false) String deliveryNo ) {
        QueryWrapper<MaterialReceiveDetail> queryWrapper = new QueryWrapper<MaterialReceiveDetail>();
        queryWrapper.eq("delivery_no", deliveryNo);
        return CommonResult.success(materialReceiveDetailService.getReceiveDetail(queryWrapper));
    }

    @ApiOperation(value = "查询已配送明细信息", notes = "根据跟单号、分页查询已配送明细信息")
    @GetMapping("/get_delivered_detail")
    public CommonResult<IPage<TrackAssembly>> getDeliveredDetail(@ApiParam(value = "页码") @RequestParam(required = false) Integer page,
                                                                 @ApiParam(value = "条数") @RequestParam(required = false) Integer limit,
                                                                 @ApiParam(value = "跟单id") @RequestParam(required = false) String trackHeadId ) {
        return CommonResult.success(trackAssemblyService.getDeliveredDetail(new Page<TrackAssembly>(page, limit),trackHeadId));
    }

    @ApiOperation(value = "物料接收", notes = "物料接收")
    @GetMapping("/material_receive")
    public CommonResult<Boolean> materialReceive(@ApiParam(value = "配送单号") @RequestParam(required = false) String deliveryNo, String branchCode ) {
        QueryWrapper<MaterialReceiveDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("delivery_no",deliveryNo);
        List<MaterialReceiveDetail> list = materialReceiveDetailService.list(wrapper);
        boolean b = lineStoreService.addStoreByWmsSend(list, branchCode);
        if (b){
            boolean update = materialReceiveDetailService.updateState(list);
            return CommonResult.success(update);
        } else {
            return CommonResult.success(false);
        }
    }

    @ApiOperation(value = "查询定时任务上一次保存最后一条的时间", notes = "最后一条创建时间")
    @GetMapping(value = "/get_last_time")
    public String getlastTime(){
        return materialReceiveService.getlastTime();
    };

    @ApiOperation(value = "批量接收wms视图物料物料接收", notes = "批量接收物料")
    @GetMapping(value = "/material_receive/save_batch")
    public Boolean materialReceiveSaveBatch(List<MaterialReceive> materialReceiveList){
        return materialReceiveService.saveBatch(materialReceiveList);
    };

    @ApiOperation(value = "批量接收wms视图配送明细", notes = "批量接收物料配送明细")
    @GetMapping(value = "/detail/save_batch")
    public Boolean detailSaveBatch(List<MaterialReceiveDetail> detailList){
        return materialReceiveDetailService.saveBatch(detailList);
    };
}
