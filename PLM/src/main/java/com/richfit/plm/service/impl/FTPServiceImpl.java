package com.richfit.plm.service.impl;

import com.richfit.plm.common.ResultCode;
import com.richfit.plm.common.exception.GlobalException;
import com.richfit.plm.service.FTPService;
import com.richfit.plm.util.FtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author HanHaiBo
 * @date 2023/6/14 10:22
 */
@Service
@Slf4j
public class FTPServiceImpl implements FTPService {

    @Override
    public ResponseEntity<InputStreamResource> downloadFiles(List<String> filePaths) {
        // 创建临时文件
        File tempFile;
        try {
            tempFile = File.createTempFile("temp", ".zip");
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
        return FtpUtils.downloadFiles(filePaths, tempFile);
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadFile(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        System.out.println(fileName);
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
        byte[] byteData = FtpUtils.downloadFile(filePath);
        if (byteData == null) {
            throw new GlobalException("获取文件失败！", ResultCode.FAILED);
        }
        ByteArrayResource resource = new ByteArrayResource(byteData);
        // 设置HTTP头部
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @Override
    public void uploadFile(MultipartFile file, String filePath) {
        try {
            log.info("即将上传文件到该路径：" + filePath + "/" + file.getOriginalFilename());
            FtpUtils.uploadFile(file, filePath);
        } catch (IOException e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }
}
