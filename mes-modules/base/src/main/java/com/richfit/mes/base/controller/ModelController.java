package com.richfit.mes.base.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

import java.net.URLEncoder;

/**
 * @author luzhiqiang
 * @date 2022-05-13 17:16
 */
@Slf4j
@Api(value = "模板下载", tags = {"模板下载"})
@RestController
@RequestMapping("/api/base/model")
public class ModelController {

    @GetMapping("/download")
    @ApiOperation(value = "文件下载", notes = "以流的方式下载")
    public void download(@ApiIgnore HttpServletResponse response,
                         @ApiParam(value = "模板名称", required = true) @RequestParam String name) throws Exception {
        // 下载本地
        // 以流的形式下载文件。
        // 读到流中
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/" + name);
            InputStream inputStream = classPathResource.getInputStream();
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            // 从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
