package com.richfit.plm.service.impl;

import com.richfit.plm.common.CommonResult;
import com.richfit.plm.common.ResultCode;
import com.richfit.plm.common.exception.GlobalException;
import com.richfit.plm.service.FTPService;
import com.richfit.plm.util.FtpUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author HanHaiBo
 * @date 2023/6/14 10:22
 */
@Service
public class FTPServiceImpl implements FTPService {

    @Override
    public ResponseEntity<byte[]> downloadFiles(String filePath) {
        try {
            List<InputStream> allFileStreams = FtpUtils.getAllFileStreams(filePath);
            ByteArrayOutputStream outputStream = FtpUtils.mergeInputStreams(allFileStreams);
            byte[] zipBytes = FtpUtils.createZipFile(outputStream);
            outputStream.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "file.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public CommonResult<ResponseEntity<ByteArrayResource>> downloadFile(String filePath) {
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
            return CommonResult.failed(ResultCode.FAILED, "获取文件失败！");
        }
        ByteArrayResource resource = new ByteArrayResource(byteData);
        // 设置HTTP头部
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        return CommonResult.success(ResponseEntity.ok()
                .headers(headers)
                .body(resource));
    }

    @Override
    public void uploadFile(MultipartFile file, String filePath) {
        FtpUtils.uploadFile(file, filePath);
    }
}
