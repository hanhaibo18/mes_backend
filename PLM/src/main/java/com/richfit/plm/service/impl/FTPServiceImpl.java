package com.richfit.plm.service.impl;

import com.richfit.plm.service.FTPService;
import com.richfit.plm.util.FtpUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
}
