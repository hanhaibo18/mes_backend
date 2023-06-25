package com.richfit.plm.controller;

import com.richfit.plm.service.FTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HanHaiBo
 * @date 2023/6/14 10:10
 */
@RestController
@RequestMapping("/api/plm/fpt")
public class FTPController {
    @Autowired
    private FTPService ftpService;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFiles(@RequestParam String filePath) {
        return ftpService.downloadFiles(filePath);
    }
}
