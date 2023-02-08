package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.CheckAttachment;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.TrackCheckAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private SystemServiceClient systemServiceClient;

    @ApiOperation(value = "文件上传用接口", notes = "文件上传用接口")
    @PostMapping("/saveCheckFile")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveCheckFile(@RequestBody CheckAttachment checkAttachment) {
        return CommonResult.success(checkAttachmentService.save(checkAttachment));
    }

    @ApiOperation(value = "质检文件删除接口", notes = "质检文件删除接口")
    @DeleteMapping("/delete_check_file")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteCheckFile(String fileId, String tiId) {
        QueryWrapper<CheckAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        List<CheckAttachment> attachmentList = checkAttachmentService.list(queryWrapper);
        if (attachmentList.size() == 1) {
            //删除文件
            systemServiceClient.delete(fileId);
        }
        QueryWrapper<CheckAttachment> queryWrapperItem = new QueryWrapper<>();
        queryWrapperItem.eq("ti_id", tiId);
        queryWrapperItem.eq("file_id", fileId);
        checkAttachmentService.removeById(queryWrapperItem);
        return CommonResult.success(true);
    }
}
