package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.dto.MaterialReceiveDto;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/8/3 10:52
 */
@Slf4j
@Api(tags = "物料接收(装配)")
@RestController
@RequestMapping("/api/produce/material_receive")
@Transactional(rollbackFor = Exception.class)
public class MaterialReceiveController extends BaseController {

    @Autowired
    MaterialReceiveService materialReceiveService;

    @Autowired
    MaterialReceiveLogService materialReceiveLogService;

    @Autowired
    MaterialReceiveDetailService materialReceiveDetailService;

    @Autowired
    TrackAssemblyService trackAssemblyService;

    @Autowired
    LineStoreService lineStoreService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private RequestNoteDetailService requestService;

    @ApiOperation(value = "分页查询物料接收", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询物料接收")
    @GetMapping("/query/page")
    public CommonResult<IPage<MaterialReceive>> selectTrackHead(@ApiParam(value = "跟单号") @RequestParam(required = false) String trackNo,
                                                                @ApiParam(value = "页码") @RequestParam(required = false) Integer page,
                                                                @ApiParam(value = "条数") @RequestParam(required = false) Integer limit,
                                                                @ApiParam(value = "配送单号") @RequestParam(required = false) String deliveryNo,
                                                                @ApiParam(value = "申请单号") @RequestParam(required = false) String aplyNum,
                                                                @ApiParam(value = "状态") @RequestParam(required = false) List<String> states,
                                                                @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<MaterialReceive> queryWrapper = new QueryWrapper<MaterialReceive>();
        if (ObjectUtils.isNotNull(states)) {
            queryWrapper.in("mr.state", states);
        }
        if (!StringUtils.isNullOrEmpty(deliveryNo)) {
            queryWrapper.like("mr.delivery_no", deliveryNo);
        }
        if (!StringUtils.isNullOrEmpty(aplyNum)) {
            queryWrapper.like("mr.aply_num", aplyNum);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.like("pth.track_no", trackNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("pth.branch_code", branchCode);
        }
        queryWrapper.eq("pth.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("outbound_date");
        return CommonResult.success(materialReceiveService.getPage(new Page<MaterialReceive>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "查询本次配送明细信息", notes = "根据跟单号、分页查询本次配送明细信息")
    @GetMapping("/delivery_detail")
    public CommonResult<List<MaterialReceiveDetail>> getThisDeliveryDetail(@ApiParam(value = "配送单号") @RequestParam(required = false) String deliveryNo) {
        QueryWrapper<MaterialReceiveDetail> queryWrapper = new QueryWrapper<MaterialReceiveDetail>();
        queryWrapper.eq("delivery_no", deliveryNo);
        queryWrapper.orderByDesc("material_num");
        return CommonResult.success(materialReceiveDetailService.getReceiveDetail(queryWrapper));
    }

    @ApiOperation(value = "查询已配送明细信息", notes = "根据跟单号、分页查询已配送明细信息")
    @GetMapping("/delivered_detail")
    public CommonResult<IPage<TrackAssembly>> getDeliveredDetail(@ApiParam(value = "页码") @RequestParam(required = false) Integer page,
                                                                 @ApiParam(value = "条数") @RequestParam(required = false) Integer limit,
                                                                 @ApiParam(value = "跟单id") @RequestParam(required = false) String trackHeadId) {
        return CommonResult.success(trackAssemblyService.getDeliveredDetail(new Page<TrackAssembly>(page, limit), trackHeadId));
    }

    @ApiOperation(value = "物料接收", notes = "物料接收")
    @GetMapping("/material_receive")
    public CommonResult<Boolean> materialReceive(@ApiParam(value = "配送单号") @RequestParam(required = false) String deliveryNo, String branchCode) {

        Boolean aBoolean = materialReceiveDetailService.updateState(deliveryNo, branchCode);

        return CommonResult.success(aBoolean);
    }

    @ApiOperation(value = "查询定时任务上一次保存最后一条的时间", notes = "最后一条创建时间")
    @GetMapping(value = "/get_last_time")
    @Inner
    public String getlastTime(String tenantId) {
        return materialReceiveService.getlastTime(tenantId);
    }


    @ApiOperation(value = "批量接收wms视图物料物料接收", notes = "批量接收物料")
    @ApiImplicitParam(name = "materialReceiveList", value = "materialReceiveList", paramType = "query", allowMultiple = true, dataType = "List<MaterialReceive>")
    @PostMapping(value = "/material_receive/save_batch")
    @Inner
    public Boolean materialReceiveSaveBatch(@RequestBody List<MaterialReceive> materialReceiveList) {
        return materialReceiveService.saveMaterialReceiveList(materialReceiveList);
    }

    @ApiOperation(value = "物料接收保存", notes = "批量接收物料")
    @ApiImplicitParam(name = "materialReceiveList", value = "materialReceiveList", paramType = "query", allowMultiple = true, dataType = "List<MaterialReceive>")
    @PostMapping(value = "/material_receive/save_batch_list")
    @Inner
    public CommonResult materialReceiveSaveBatchList(@RequestBody MaterialReceiveDto material) {
        boolean flag = true;
        String message = "成功";
        try {
            if (CollectionUtils.isEmpty(material.getReceived())) {
                throw new GlobalException("物料主数据为空", ResultCode.FAILED);
            }
            if (CollectionUtils.isEmpty(material.getDetailList())) {
                throw new GlobalException("物料明细数据为空", ResultCode.FAILED);
            }

            Map<String, Tenant> collect = systemServiceClient.queryTenantList(SecurityConstants.FROM_INNER).getData().stream().collect(Collectors.toMap(Tenant::getTenantErpCode, x -> x, (value1, value2) -> value2));
            material.getReceived().forEach(materialReceive -> {
                if (collect.get(materialReceive.getErpCode()) == null) {
                    throw new GlobalException("ERPCODE没有找到租户信息", ResultCode.FAILED);
                }
                materialReceive.setTenantId(collect.get(materialReceive.getErpCode()).getId());
                materialReceive.setBranchCode(collect.get(materialReceive.getErpCode()).getTenantCode());
            });
            materialReceiveService.saveMaterialReceiveList(material.getReceived());
            material.getDetailList().forEach(detail -> {
                List<RequestNoteDetail> noteDetailList = requestService.queryRequestNoteDetailDetails(detail.getMaterialNum(), detail.getAplyNum());
                if (!CollectionUtils.isEmpty(noteDetailList) && StrUtil.isNotEmpty(noteDetailList.get(0).getDrawingNo())) {
                    detail.setDrawingNo(noteDetailList.get(0).getDrawingNo());
                }
            });
            materialReceiveDetailService.saveBatch(material.getDetailList());
        } catch (GlobalException e) {
            e.printStackTrace();
            //既能实现回滚也能返回结果
            flag = false;
            message = e.getMessage();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        } finally {
            if (flag) {
                return CommonResult.success(message);
            } else {
                return CommonResult.failed(message);
            }
        }
    }

    @ApiOperation(value = "物料接收日志", notes = "物料接收日志")
    @PostMapping(value = "/material_receive/save_log")
    @Inner
    public void materialReceiveSaveLog(@ApiParam(required = true, value = "日志对象") @RequestBody MaterialReceiveLog materialReceiveLog) {
        materialReceiveLogService.save(materialReceiveLog);
    }

    @ApiOperation(value = "批量接收wms视图配送明细", notes = "批量接收物料配送明细")
    @ApiImplicitParam(name = "detailList", value = "detailList", paramType = "query", allowMultiple = true, dataType = "List<MaterialReceiveDetail>")
    @PostMapping(value = "/detail/save_batch")
    @Inner
    public Boolean detailSaveBatch(@RequestBody List<MaterialReceiveDetail> detailList) {
        return materialReceiveDetailService.saveDetailList(detailList);
    }

}
