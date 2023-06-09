package com.tc.mes.plm.controller;

import com.tc.mes.plm.entity.request.TechnicalNoticeRequest;
import com.tc.mes.plm.service.ProduceSkillNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("技术通知单")
@RestController
@RequestMapping("/api/produce/skill_notice")
public class ProduceSkillNoticeController {

    @Autowired
    private ProduceSkillNoticeService produceSkillNoticeService;

    @ApiOperation(value = "批量新增", notes = "批量新增技术通知单")
    @PostMapping("/save_batch_skill_notice")
    public boolean saveBatchNotice(@RequestBody List<TechnicalNoticeRequest> noticeRequestList) {
        return produceSkillNoticeService.saveBatchNotice(noticeRequestList);
    }
}
