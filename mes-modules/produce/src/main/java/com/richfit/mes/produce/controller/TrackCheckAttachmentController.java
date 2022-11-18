package com.richfit.mes.produce.controller;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.CheckAttachment;
import com.richfit.mes.produce.service.TrackCheckAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName: TrackCheckAttachmentController.java
 * @Author: Hou XinYu
 * @Description: 质检文件
 * @CreateTime: 2022年06月28日 16:34:00
 */

@Slf4j
@Api(value = "质检与文件中间表", tags = {"质检与文件中间表"})
@RestController
@RequestMapping("/api/produce/check_file")
public class TrackCheckAttachmentController extends BaseController {

    @Resource
    private TrackCheckAttachmentService checkAttachmentService;

    @ApiOperation(value = "文件上传用接口", notes = "文件上传用接口")
    @PostMapping("/saveCheckFile")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveCheckFile(@RequestBody CheckAttachment checkAttachment) {
        return CommonResult.success(checkAttachmentService.save(checkAttachment));
    }
}
