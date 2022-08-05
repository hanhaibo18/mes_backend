package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.service.MaterialReceiveDetailService;
import com.richfit.mes.produce.service.MaterialReceiveService;
import com.richfit.mes.produce.utils.TaskUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/produce/material_recreive")
public class MaterialReceiveController extends BaseController {

    @Autowired
    MaterialReceiveService materialReceiveService;


    @Autowired
    MaterialReceiveDetailService materialReceiveDetailService;

    @Autowired
    TaskUtils taskUtils;



    @ApiOperation(value = "分页查询跟单", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询跟单")
    @GetMapping("/material_receive")
    public CommonResult<IPage<MaterialReceive>> selectTrackHead(@ApiParam(value = "跟单号") @RequestParam(required = false) String trackNo,
                                                                @ApiParam(value = "页码") @RequestParam(required = false) int page,
                                                                @ApiParam(value = "条数") @RequestParam(required = false) int limit,
                                                                @ApiParam(value = "配送单号") @RequestParam(required = false) String deliveryNo) {
        QueryWrapper<MaterialReceive> queryWrapper = new QueryWrapper<MaterialReceive>();
        if (!StringUtils.isNullOrEmpty(deliveryNo)) {
            queryWrapper.eq("delivery_no", deliveryNo);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.eq("track_no", trackNo);
        }
        return CommonResult.success(materialReceiveService.getPage(new Page<MaterialReceive>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "查询本次配送明细信息", notes = "根据跟单号、查询本次配送明细信息")
    @GetMapping("/getReceiveDetail")
    public CommonResult<List<MaterialReceiveDetail>> selectTrackHead(String deliveryNo ) {
        QueryWrapper<MaterialReceiveDetail> queryWrapper = new QueryWrapper<MaterialReceiveDetail>();
        queryWrapper.eq("delivery_no", deliveryNo);
        return CommonResult.success(materialReceiveDetailService.getReceiveDetail(queryWrapper));
    }

}
