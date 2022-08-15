package com.richfit.mes.produce.utils;

import cn.hutool.core.util.ZipUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.UUID;

public class FilesUtil {

    /**
     * 系统临时文件夹
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/1 10:25
     */
    public static String tempPath() throws Exception {
        String path = "C:/temp";
        if (File.separator.equals("/")) {
            path = "/tmp";
        }
        return path;
    }

    public static File createRandomTempDirectory() throws IOException {

        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return (temp);
    }

    /**
     * 前端文件下载工具类（前端会重命名，一般不需要后端进行命名）
     *
     * @param response
     * @param path     下载文件路径
     * @Author: zhiqiang.lu
     * @Date: 2022/8/1 10:25
     */
    public static void downloads(HttpServletResponse response, String path) throws Exception {
        downloads(response, path, UUID.randomUUID().toString());
    }


    /**
     * 前端文件下载工具类
     *
     * @param response
     * @param path     下载文件路径
     * @param name     下载文件名字
     * @Author: zhiqiang.lu
     * @Date: 2022/8/1 10:25
     */
    public static void downloads(HttpServletResponse response, String path, String name) throws Exception {
        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);
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
    }

    /**
     * zip文件打包
     *
     * @param path 打包文件路径
     * @Author: zhiqiang.lu
     * @Date: 2022/8/1 10:25
     */
    public static void zip(String path) throws Exception {
        ZipUtil.zip(path);
    }
}
