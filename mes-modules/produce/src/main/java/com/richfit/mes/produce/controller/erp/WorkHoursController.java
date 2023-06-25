package com.richfit.mes.produce.controller.erp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Certificate;

import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.erp.WorkHoursService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 功能描述:工时管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
@Slf4j
@Api(value = "合格证管理", tags = {"合格证管理"})
@RestController
@RequestMapping("/api/produce/erp/work/hours")
public class WorkHoursController {

    @Autowired
    CertificateService certificateService;

    @Autowired
    WorkHoursService workHoursService;

    @ApiOperation(value = "推送工时", notes = "推送工时")
    @PostMapping("/push")
    public CommonResult<Object> push(@ApiParam(value = "合格证", required = true) @RequestBody List<Certificate> certificateList) {
        StringBuilder message = new StringBuilder();
        for (Certificate certificate : certificateList) {
            try {
                workHoursService.push(certificate);
            } catch (Exception e) {
                message.append(e.getMessage());
            }
        }
        return CommonResult.success(message.toString());
    }

    @ApiOperation(value = "自动推送工时", notes = "自动推送工时")
    @GetMapping("/auto/push")
    public CommonResult<Object> autoPush() {
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("is_send_work_hour", "1");
        queryWrapper.eq("next_opt_work", "BOMCO_SC");
        queryWrapper.eq("type", "1");
        List<Certificate> certificateList = certificateService.list(queryWrapper);
        StringBuilder message = new StringBuilder();
        for (Certificate certificate : certificateList) {
            try {
                workHoursService.push(certificate);
            } catch (Exception e) {
                message.append(e.getMessage());
            }
        }
        return CommonResult.success(message.toString());
    }
}
