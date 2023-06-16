package com.pdm.mes.schedule.controller;

import com.pdm.mes.schedule.entity.request.SaleProductionSchedulingRequest;
import com.pdm.mes.schedule.service.ProduceNoticeService;
import com.richfit.mes.common.core.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;

@Api("排产单")
@RestController
@RequestMapping("/api/produce/notice")
public class ProduceNoticeController {

    @Autowired
    private ProduceNoticeService produceNoticeService;

    @ApiOperation(value = "批量新增", notes = "批量新增排产单")
    @PostMapping("/save_batch_notice")
    public boolean saveBatchNotice(@RequestBody List<SaleProductionSchedulingRequest> schedulingDtoList) {
        return produceNoticeService.saveBatchNotice(schedulingDtoList);
    }

    @ApiOperation(value = "上传文件流", notes = "上传文件流")
    @PostMapping("/upload_file")
    public String uploadFile(InputStream inputStream, String fileName) throws Exception {
        return produceNoticeService.uploadFile(inputStream,fileName);
    }

    @ApiOperation(value = "上传文件", notes = "测试：用来获取文件的路径")
    @PostMapping("/test_file")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) throws Exception {
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
        return produceNoticeService.uploadFile(inputStream,String.valueOf(tempName));
    }

}
