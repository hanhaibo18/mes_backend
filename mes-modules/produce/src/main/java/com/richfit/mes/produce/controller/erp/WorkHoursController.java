package com.richfit.mes.produce.controller.erp;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.CertQueryDto;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import com.richfit.mes.produce.service.erp.WorkHoursService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.richfit.mes.produce.service.bsns.AbstractCertAdditionalBsns.needScjk;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author 王瑞
 * @Description 合格证Controller
 */
@Slf4j
@Api(value = "合格证管理", tags = {"合格证管理"})
@RestController
@RequestMapping("/api/produce/work/hours")
public class WorkHoursController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private WorkHoursService workHoursService;

    @ApiOperation(value = "自动推送工时", notes = "自动推送工时")
    @PostMapping("/push")
    public CommonResult push(@ApiParam(value = "合格证", required = true) @RequestBody List<Certificate> certificateList) throws Exception {
        String message = "";
        for (Certificate certificate : certificateList) {
            try {
                workHoursService.push(certificate);
            } catch (Exception e) {
                message += e.getMessage();
            }
        }
        return CommonResult.success(message);
    }

    @ApiOperation(value = "自动推送工时", notes = "自动推送工时")
    @GetMapping("/auto/push")
    public CommonResult autoPush() throws Exception {
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("is_send_work_hour", "1");
        queryWrapper.eq("next_opt_work", "BOMCO_SC");
        queryWrapper.eq("type", "1");
        List<Certificate> certificateList = certificateService.list(queryWrapper);
        String message = "";
        for (Certificate certificate : certificateList) {
            try {
                workHoursService.push(certificate);
            } catch (Exception e) {
                message += e.getMessage();
            }
        }
        return CommonResult.success(message);
    }
}
