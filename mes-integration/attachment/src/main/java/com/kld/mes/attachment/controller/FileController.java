package com.kld.mes.attachment.controller;


import com.kld.mes.attachment.common.Result;
import com.kld.mes.attachment.service.AttachmentService;
import com.richfit.mes.common.core.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Api("附件")
@RestController
@RequestMapping("/api/integration/attachment/file")
public class FileController {

    @Autowired
    private AttachmentService attachmentService;

    @ApiOperation(value = "上传文件流", notes = "上传文件流")
    @PostMapping("/upload_file")
    public Result<String> uploadFile(InputStream inputStream, String fileName) throws Exception {
        if (StringUtils.isNotEmpty(fileName)) {
            return Result.success(attachmentService.uploadFile(inputStream,fileName));
        }
        return Result.error("N","文件名不能为空");
    }

    @ApiOperation(value = "上传文件", notes = "测试：用来获取文件的路径")
    @PostMapping("/test_file")
    public Result<String> uploadTest(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        File file = null;
        // 临时文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        // 拼接后缀名
        tempName.append(".").append(FileUtils.getFilenameExtension(multipartFile.getOriginalFilename()));
        try {
            file = File.createTempFile(String.valueOf(tempName), null);
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = new FileInputStream(file);

        return Result.success(attachmentService.uploadFile(inputStream,String.valueOf(tempName)));
    }
}
