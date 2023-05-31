package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Notice;
import com.richfit.mes.sys.entity.dto.IssueNoticeDto;
import com.richfit.mes.sys.entity.dto.ProductionSchedulingDto;
import com.richfit.mes.sys.entity.dto.SalesSchedulingDto;
import com.richfit.mes.sys.entity.dto.SendBackDto;
import com.richfit.mes.sys.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: NoticeController.java
 * @Author: Hou XinYu
 * @Description: 排产通知
 * @CreateTime: 2023年05月30日 14:50:00
 */

@Api("排产通知")
@RestController
@RequestMapping("/api/sys/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @ApiOperation(value = "销售排产分页查询", notes = "根据条件查询销售排产数据")
    @PostMapping("/query_sales_page")
    public CommonResult<IPage<Notice>> queryPage(@RequestBody SalesSchedulingDto salesSchedulingDto) {
        return CommonResult.success(noticeService.queryPage(salesSchedulingDto));
    }

    @ApiOperation(value = "接受通知", notes = "根据ID修改通知状态")
    @PostMapping("/acceptance_notice")
    public CommonResult<Boolean> acceptanceNotice(@RequestBody List<String> idList) {
        return CommonResult.success(noticeService.acceptanceNotice(idList));
    }

    @ApiOperation(value = "退回通知", notes = "根据ID修改通知状态")
    @PostMapping("/notice_return")
    public CommonResult<Boolean> noticeReturn(@RequestBody SendBackDto sendBackDto) {
        return CommonResult.success(noticeService.noticeReturn(sendBackDto));
    }

    @ApiOperation(value = "生产排产接口", notes = "根据查询条件查询生产排产数据")
    @PostMapping("/query_production_scheduling_page")
    public CommonResult<IPage<Notice>> queryProductionSchedulingPage(@RequestBody ProductionSchedulingDto productionSchedulingDto) {
        return CommonResult.success(noticeService.queryProductionSchedulingPage(productionSchedulingDto));
    }


    @ApiOperation(value = "通知编辑", notes = "进行排产")
    @PostMapping("/update_production_scheduling")
    public CommonResult<Boolean> updateProductionScheduling(@RequestBody Notice notice) {
        return CommonResult.success(noticeService.updateProductionScheduling(notice));
    }

    @ApiOperation(value = "通知下发", notes = "通知下发")
    @PostMapping("/issue_notice")
    public CommonResult<Boolean> issueNotice(@RequestBody IssueNoticeDto issueNotice) {
        return CommonResult.success(noticeService.issueNotice(issueNotice));
    }

    @ApiOperation(value = "取消排产", notes = "取消排产")
    @PostMapping("/cancel_production_scheduling")
    public CommonResult<Boolean> cancelProductionScheduling(@RequestBody List<String> idList) {
        return CommonResult.success(noticeService.cancelProductionScheduling(idList));
    }
}
