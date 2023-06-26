package com.richfit.plm.controller;

import com.richfit.plm.common.CommonResult;
import com.richfit.plm.service.FTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author HanHaiBo
 * @date 2023/6/14 10:10
 */
@RestController
@RequestMapping("/api/plm/fpt")
public class FTPController {
    @Autowired
    private FTPService ftpService;

//    @GetMapping("/download_files")
//    public ResponseEntity<byte[]> downloadFiles(@RequestParam String filePath) {
//        return ftpService.downloadFiles(filePath);
//    }

    @GetMapping("/download_file")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String filePath) {
        return ftpService.downloadFile(filePath);
    }

    @PostMapping("/upload_file")
    public void uploadFile(MultipartFile file, @RequestParam String filePath) {
        ftpService.uploadFile(file, filePath);
    }
}
