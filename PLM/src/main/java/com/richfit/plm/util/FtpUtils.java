package com.richfit.plm.util;

import com.richfit.plm.common.ResultCode;
import com.richfit.plm.common.exception.GlobalException;
import com.richfit.plm.config.FtpPropertiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author HanHaiBo
 * @date 2023/6/13 15:30
 */
@Component
public class FtpUtils {
    private static final Logger log = LoggerFactory.getLogger(FtpUtils.class);

    private static FtpPropertiesConfig ftpPropertiesConfig;

    //注入ftp配置
    @Autowired
    private FtpPropertiesConfig ftpConfig;

    public static void uploadFile(MultipartFile file, String filePath) throws IOException {
        FTPClient ftpClient = null;
        try {
            ftpClient = login(ftpPropertiesConfig.getHost(), ftpPropertiesConfig.getUploadPort(),
                    ftpPropertiesConfig.getUserName(), ftpPropertiesConfig.getPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            // 检查远程路径是否存在，不存在则递归创建
            createRemoteDirectory(ftpClient, filePath);
            log.info("创建路径成功");
            String remoteFilePath = filePath + "/" + file.getOriginalFilename();
            ftpClient.storeFile(remoteFilePath, file.getInputStream());
        } catch (IOException e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }

    private static void createRemoteDirectory(FTPClient ftpClient, String remotePath) throws IOException {
        String[] directories = remotePath.split("/");
        for (String directory : directories) {
            if (!directory.isEmpty() && !ftpClient.changeWorkingDirectory(directory)) {
                ftpClient.makeDirectory(directory);
                ftpClient.changeWorkingDirectory(directory);
            }
        }
    }

    public static ResponseEntity<InputStreamResource> downloadFiles(List<String> filePaths, File tempFile) {
        FTPClient ftpClient = null;
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            // 连接 FTP 服务器
            ftpClient = login(ftpPropertiesConfig.getHost(), ftpPropertiesConfig.getDownPort(),
                    ftpPropertiesConfig.getUserName(), ftpPropertiesConfig.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 下载文件并将其添加到 ZIP
            for (String filePath : filePaths) {
                if (!isFileExists(ftpClient, filePath)) {
                    log.error("文件不存在！");
                    continue;
                }
                String fileName = getFileNameFromPath(filePath);
                InputStream inputStream = ftpClient.retrieveFileStream(filePath);
                if (inputStream != null) {
                    addToZip(fileName, inputStream, zipOut);
                    ftpClient.completePendingCommand();
                    inputStream.close();
                }
            }

            ftpClient.logout();
            ftpClient.disconnect();

            zipOut.close();
            fos.close();

            // 将 ZIP 文件作为响应返回给前端
            InputStreamResource resource = new InputStreamResource(new FileInputStream(tempFile));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(tempFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        } finally {
            // 关闭连接和删除临时文件
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tempFile.delete();
        }
    }

    private static boolean isFileExists(FTPClient ftpClient, String filePath) {
        boolean fileExists = false;
        try {
            // 获取目录路径和文件名
            log.info(filePath);
            String[] pathParts = filePath.split("/");
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            String filename = pathParts[pathParts.length - 1];
            ftpClient.changeWorkingDirectory("/");
            boolean changeResult = ftpClient.changeWorkingDirectory(filePath);
            if (!changeResult) {
                throw new GlobalException("切换目录失败！", ResultCode.FAILED);
            }
            // 获取目录下的文件列表
            String[] files = ftpClient.listNames();
            if (files != null) {
                for (String file : files) {
                    if (file.equals(filename)) {
                        fileExists = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileExists;
    }

    private static void addToZip(String fileName, InputStream inputStream, ZipOutputStream zipOut) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
    }

    private static String getFileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }


    @PostConstruct
    public void init() {
        FtpUtils.ftpPropertiesConfig = ftpConfig;
    }

    /**
     * 登陆FTP服务器
     *
     * @param host     FTPServer IP地址
     * @param port     FTPServer 端口
     * @param userName FTPServer 登陆用户名
     * @param password FTPServer 登陆密码
     * @return FTPClient
     * @throws IOException
     */
    public static FTPClient login(String host, String port, String userName, String password) throws IOException {
        FTPClient ftpClient = new FTPClient();
        // 防止中文目录乱码
        ftpClient.setAutodetectUTF8(true);
        ftpClient.setConnectTimeout(60000);
        // 连接FTP服务器
        ftpClient.connect(host, Integer.parseInt(port));

        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            // 登陆FTP服务器
            if (ftpClient.login(userName, password)) {
                // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
                        "OPTS UTF8", "ON"))) {
                    ftpClient.setControlEncoding("UTF-8");
                } else {
                    ftpClient.setControlEncoding("GBK");
                }

                // 设置传输的模式，以二进制流的方式读取
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                log.info("FTP服务连接成功！");
            } else {
                log.error("FTP服务用户名或密码错误！");
                disConnection(ftpClient);
            }
        } else {
            log.error("连接到FTP服务失败！");
            disConnection(ftpClient);
        }
        return ftpClient;
    }

    /**
     * 关闭FTP服务链接
     *
     * @throws IOException
     */
    private static void disConnection(FTPClient ftpClient) throws IOException {
        if (null != ftpClient && ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    /**
     * 获取文件夹下的所有文件信息
     *
     * @param path 文件路径
     */
    public static FTPFile[] getFTPDirectoryFiles(String path) throws IOException {
        FTPFile[] files = null;
        FTPClient ftpClient = null;
        try {
            ftpClient = login(ftpPropertiesConfig.getHost(), ftpPropertiesConfig.getDownPort(),
                    ftpPropertiesConfig.getUserName(), ftpPropertiesConfig.getPassword());
            // 判断是否存在该目录
            if (!ftpClient.changeWorkingDirectory(path)) {
                log.error("该目录不存在,filePath=" + path);
            }
            //调用FTPClient.enterLocalPassiveMode();这个方法的意思就是每次数据连接之前，
            // ftpclient告诉ftp server开通一个端口来传输数据
            ftpClient.enterLocalPassiveMode();
            files = ftpClient.listFiles();
        } catch (Exception e) {
            //关闭连接
            disConnection(ftpClient);
            log.error("FTP读取数据异常！", e);
        } finally {
            disConnection(ftpClient);
        }
        return files;
    }

    public static InputStream getFTPFileStream(String filePath, String fileName) throws IOException {
        FTPClient ftpClient = null;
        try {
            ftpClient = login(ftpPropertiesConfig.getHost(), ftpPropertiesConfig.getDownPort(),
                    ftpPropertiesConfig.getUserName(), ftpPropertiesConfig.getPassword());
            // 判断是否存在该目录
            if (!ftpClient.changeWorkingDirectory(filePath)) {
                log.error("该目录不存在,filePath=" + filePath);
            }
            // 设置被动模式，开通一个端口来传输数据
            ftpClient.enterLocalPassiveMode();
            InputStream inputStream = ftpClient.retrieveFileStream(fileName);
            return inputStream;
        } catch (Exception e) {
            //关闭连接
            disConnection(ftpClient);
            log.error("FTP读取文件流异常！", e);
        } finally {
            disConnection(ftpClient);
        }
        return null;
    }

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName
     * @throws IOException
     */
    private void listFilesByKey(String pathName) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            FTPFile[] files = FtpUtils.getFTPDirectoryFiles(pathName);
            if (null == files) {
                return;
            }

            for (int i = 0; i < files.length; i++) {

                if (files[i].isFile()) {
                    // 如果是文件则进行处理
                    String fileName = files[i].getName();
                    // do something

                } else if (files[i].isDirectory()) {
                    if (!".".equals(files[i].getName()) && !"..".equals(files[i].getName())) {
                        log.info("listFilesByKey：directoryName=" + files[i].getName());
                        // 递归调用获取子目录下的文件
                        listFilesByKey(pathName + files[i].getName() + "/");
                    }
                }
            }
        }
    }

    /**
     * 删除本地目录和文件
     *
     * @param filePath
     * @return
     */
    private boolean deleteDirectory(String filePath) {
        File dirFile = new File(filePath);
        if (dirFile.isDirectory()) {
            File[] children = dirFile.listFiles();
            log.info("deleteDirectory: files count = " + children.length);

            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                if (children[i].isFile()) {
                    children[i].delete();
                    log.info("deleteDirectory: remove fileName = " + children[i].getAbsolutePath());
                } else {
                    deleteDirectory(children[i].getAbsolutePath());
                }
            }
        }
        // 删除为空目录
        log.info("deleteDirectory: remove directory = " + dirFile.getAbsolutePath());
        return dirFile.delete();
    }

    public static List<InputStream> getAllFileStreams(String filePath) throws IOException {
        List<InputStream> fileStreams = new ArrayList<>();
        FTPClient ftpClient = null;
        try {
            ftpClient = login(ftpPropertiesConfig.getHost(), ftpPropertiesConfig.getDownPort(),
                    ftpPropertiesConfig.getUserName(), ftpPropertiesConfig.getPassword());
            // 判断是否存在该目录
            if (!ftpClient.changeWorkingDirectory(filePath)) {
                log.error("该目录不存在,filePath=" + filePath);
            }
            FTPFile[] files = ftpClient.listFiles(filePath);
            for (FTPFile file : files) {
                if (file.isFile()) {
                    String remoteFilePath = filePath + "/" + file.getName();
                    InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);
                    fileStreams.add(inputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            disConnection(ftpClient);
        } finally {
            disConnection(ftpClient);
        }
        return fileStreams;
    }

    public static byte[] downloadFile(String path) {
        FTPClient ftpClient = null;
        byte[] fileData = null;
        try {
            ftpClient = login(ftpPropertiesConfig.getHost(), ftpPropertiesConfig.getDownPort(),
                    ftpPropertiesConfig.getUserName(), ftpPropertiesConfig.getPassword());
            if (!isFileExists(ftpClient, path)) {
                log.error("文件不存在！");
                throw new GlobalException("该路径不正确！：" + path, ResultCode.FAILED);
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ftpClient.retrieveFile(path, outputStream);

            ftpClient.logout();
            ftpClient.disconnect();

            fileData = outputStream.toByteArray();
            outputStream.close();

            // 将fileData作为响应返回给前端进行下载
            // 这里你可以使用适合你的Web框架或技术进行处理

            log.info("文件下载成功！");
        } catch (IOException e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
        return fileData;
    }

    public static ByteArrayOutputStream mergeInputStreams(List<InputStream> inputStreams) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        for (InputStream inputStream : inputStreams) {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        }
        return outputStream;
    }

    public static byte[] createZipFile(ByteArrayOutputStream outputStream) throws IOException {
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(zipOutputStream);
        zip.putNextEntry(new ZipEntry("文件/")); // 替换为实际的文件夹名称
        zip.write(outputStream.toByteArray());
        zip.closeEntry();
        zip.finish();
        zip.close();
        return zipOutputStream.toByteArray();
    }

}
