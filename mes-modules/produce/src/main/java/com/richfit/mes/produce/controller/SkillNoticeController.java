package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.SkillNotice;
import com.richfit.mes.produce.entity.AcceptDispatchDto;
import com.richfit.mes.produce.entity.DispatchDto;
import com.richfit.mes.produce.entity.SkillIssueNoticeDto;
import com.richfit.mes.produce.entity.SkillNoticeDto;
import com.richfit.mes.produce.service.notice.SkillNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: SkillNoticeController.java
 * @Author: Hou XinYu
 * @Description: 技术通知
 * @CreateTime: 2023年06月05日 15:04:00
 */

@Api("技术通知")
@RestController
@RequestMapping("/api/produce/skill_notice")
public class SkillNoticeController {

    @Resource
    private SkillNoticeService skillNoticeService;

    @ApiOperation(value = "接收技术通知分页查询", notes = "接收技术通知分页查询")
    @PostMapping("/query_skill_page")
    public CommonResult<IPage<SkillNotice>> querySkillPage(@RequestBody SkillNoticeDto skillNoticeDto) {
        return CommonResult.success(skillNoticeService.querySkillPage(skillNoticeDto));
    }

    @ApiOperation(value = "接受通知", notes = "接受通知")
    @PostMapping("/acceptance_of_notice")
    public CommonResult<Boolean> acceptanceOfNotice(@RequestBody List<String> idList) {
        return CommonResult.success(skillNoticeService.acceptanceOfNotice(idList));
    }

    @ApiOperation(value = "转调度通知", notes = "转调度通知")
    @PostMapping("/dispatch_notification")
    public CommonResult<Boolean> dispatchNotification(@RequestBody List<String> idList) {
        return CommonResult.success(skillNoticeService.dispatchNotification(idList));
    }

    @ApiOperation(value = "调度通知分页查询", notes = "调度通知分页查询")
    @PostMapping("/query_dispatch_page")
    public CommonResult<IPage<SkillNotice>> queryDispatchPage(@RequestBody DispatchDto dispatchDto) {
        return CommonResult.success(skillNoticeService.queryDispatchPage(dispatchDto));
    }

    @ApiOperation(value = "通知编辑", notes = "通知编辑")
    @PostMapping("/update_dispatch")
    public CommonResult<Boolean> updateDispatch(@RequestBody SkillNotice skillNotice) {
        return CommonResult.success(skillNoticeService.updateDispatch(skillNotice));
    }

    @ApiOperation(value = "通知下发", notes = "通知下发")
    @PostMapping("/dispatch_notice_delivery")
    public CommonResult<Boolean> dispatchNoticeDelivery(@RequestBody SkillIssueNoticeDto issueNoticeDto) {
        return CommonResult.success(skillNoticeService.dispatchNoticeDelivery(issueNoticeDto));
    }

    @ApiOperation(value = "接受调度通知", notes = "接受调度通知")
    @PostMapping("/receive_dispatch_notification")
    public CommonResult<IPage<SkillNotice>> receiveDispatchNotification(@RequestBody AcceptDispatchDto acceptDispatchDto) {
        return CommonResult.success(skillNoticeService.receiveDispatchNotification(acceptDispatchDto));
    }

    @ApiOperation(value = "通知确认", notes = "通知确认")
    @PostMapping("/receive_dispatch_notification_affirm")
    public CommonResult<Boolean> receiveDispatchNotificationAffirm(@RequestBody List<String> idList) {
        return CommonResult.success(skillNoticeService.receiveDispatchNotificationAffirm(idList));
    }
}
