package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.RequestNote;
import com.richfit.mes.common.model.produce.RequestNoteDetail;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.RequestNoteDetailService;
import com.richfit.mes.produce.service.RequestNoteService;
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
@Api(tags = "物料申请单")
@RestController
@RequestMapping("/api/produce/request_note")
public class ProduceRequestNoteController extends BaseController {

    @Autowired
    RequestNoteService requestNoteService;


    @Autowired
    RequestNoteDetailService requestNoteDetailService;

    @ApiOperation(value = "分页查询物料申请单", notes = "分页查询物料申请单")
    @GetMapping("/page")
    public CommonResult<PageInfo<RequestNote>> page(@ApiParam(value = "页码", required = true) @RequestParam Integer page,
                                                    @ApiParam(value = "条数", required = true) @RequestParam Integer limit,
                                                    @ApiParam(value = "跟单号") @RequestParam(required = false) String trackNo,
                                                    @ApiParam(value = "申请单号") @RequestParam(required = false) String requestNoteNumber,
                                                    @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<RequestNote> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(requestNoteNumber)) {
            queryWrapper.like("request_note_number", requestNoteNumber);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.like("track_head_no", trackNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("modify_time");
        PageHelper.startPage(page, limit);
        List requestNoteList = requestNoteService.list(queryWrapper);
        PageInfo<RequestNote> requestNotePage = new PageInfo(requestNoteList);
        return CommonResult.success(requestNotePage);
    }

    @ApiOperation(value = "查询申请单明细信息", notes = "查询申请单明细信息")
    @GetMapping("/detail")
    public CommonResult<List<RequestNoteDetail>> getThisDeliveryDetail(@ApiParam(value = "配送单号") @RequestParam(required = false) String noteId) {
        List<RequestNoteDetail> requestNoteDetailList = requestNoteDetailService.getDeliveryInformation(noteId);
        for (RequestNoteDetail requestNoteDetail : requestNoteDetailList) {
            requestNoteDetail.setNumberMissing(requestNoteDetail.getNumber() - requestNoteDetail.getNumberDelivery());
        }
        return CommonResult.success(requestNoteDetailList);
    }
}
