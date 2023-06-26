package com.richfit.plm.service;

import com.richfit.plm.common.CommonResult;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author HanHaiBo
 * @date 2023/6/14 10:21
 */
public interface FTPService {
    ResponseEntity<InputStreamResource> downloadFiles(List<String> filePath);

    ResponseEntity<ByteArrayResource> downloadFile(String filePath);

    void uploadFile(MultipartFile file, String filePath);
}
