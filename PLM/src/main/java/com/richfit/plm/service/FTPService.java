package com.richfit.plm.service;

import org.springframework.http.ResponseEntity;

/**
 * @author HanHaiBo
 * @date 2023/6/14 10:21
 */
public interface FTPService {
    ResponseEntity<byte[]> downloadFiles(String filePath);
}
