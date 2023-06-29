package com.kld.mes.attachment.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.GroupState;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorageNode;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadFileWriter;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.ProtoCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author sun
 * @Description 上传到FastDfs
 */
@Slf4j
@Service
public class FastDfsService {

    @Value("${fdfs.web-server-url}")
    private String fastDfsUrl;

    @Value("${fdfs.http.secret_key}")
    private String fastDfsKey;

    /**
     * 面向普通应用的文件操作接口
     */
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 支持断点续传的文件服务接口
     */
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    /**
     * 目录服务(Tracker)客户端接口
     */
    @Autowired
    private TrackerClient trackerClient;

    /**
     * 上传文件
     *
     * @param inputStream 输入流
     * @param size        文件大小
     * @param extName     扩展名
     * @return 完整路径
     */
    public String uploadFile(InputStream inputStream, long size, String extName) throws Exception {
        return uploadFile(inputStream, size, extName, null);
    }

    /**
     * 上传文件
     *
     * @param inputStream
     * @param size        文件大小
     * @param extName     扩展名
     * @param metaDataSet 元数据集
     * @return 完整路径
     */
    public String uploadFile(InputStream inputStream, long size, String extName, Set<MetaData> metaDataSet) throws Exception {
        log.info("Attachment size: {}，extName: {}", size, extName);
        StorePath storePath = fastFileStorageClient.uploadFile(inputStream, size, extName, metaDataSet);
        log.info("Upload success, group: {}, path: {}", storePath.getGroup(), storePath.getPath());
        return storePath.getFullPath();
    }

    /**
     * 支持断点续传，适合上传大文件，需要指定组名
     *
     * @param groupName   组名
     * @param inputStream 附件输入流
     * @param size        附件大小
     * @param extName     附件扩展名
     * @return 完整路径
     */
    public String uploadAppenderFile(String groupName, InputStream inputStream, long size, String extName) {
        try {
            log.info("Attachment size: {}，extName: {}", size, extName);
            StorePath storePath = appendFileStorageClient.uploadAppenderFile(groupName, inputStream, size, extName);
            log.info("上传文件成功，group：{}，path：{}", storePath.getGroup(), storePath.getPath());
            return storePath.getFullPath();
        } catch (Exception e) {
            log.error("上传文件失败！", e);
        }
        return null;
    }

    /**
     * 续传文件
     *
     * @param groupName   组名，如group1
     * @param path        路径名，M00/00/04/wKgAUFpO84CAA4HvAAAABs4Fkco168.txt
     * @param inputStream 输入流
     * @param size        附件大小
     * @return 是否续传成功
     */
    public boolean appendFile(String groupName, String path, InputStream inputStream, long size) {
        try {
            log.info("续传文件大小{}，组名{}，路径名{}", size, groupName, path);
            appendFileStorageClient.appendFile(groupName, path, inputStream, size);
            return true;
        } catch (Exception e) {
            log.error("续传文件失败！", e);
        }
        return false;
    }

    /**
     * 下载文件
     *
     * @param groupName 组名，如：group1
     * @param path      路径名，如：M00/00/04/wKgAUFpO84CAA4HvAAAABs4Fkco168.txt
     * @return 字节数组
     * @author tangyi
     * @date 2018/1/5 11:59
     */
    public byte[] downloadFile(String groupName, String path) {
        if (path.startsWith(groupName + "/")) {
            path = path.split(groupName + "/")[1];
        }
        try {
            log.info("下载文件，group：{}，path：{}", groupName, path);
            DownloadByteArray callback = new DownloadByteArray();
            return fastFileStorageClient.downloadFile(groupName, path, callback);
        } catch (Exception e) {
            log.error("下载文件失败!", e);
        }
        return null;
    }

    /**
     * 下载附件，并保存到指定的文件，适合下载大文件
     *
     * @param groupName 组名，如：group1
     * @param path      路径名，如：M00/00/04/wKgAUFpO84CAA4HvAAAABs4Fkco168.txt
     * @param filePath  文件存放的路径，如：C:\attach\1.rar
     * @return 文件存放的路径
     */
    public String downloadFile(String groupName, String path, String filePath) {
        if (path.startsWith(groupName + "/")) {
            path = path.split(groupName + "/")[1];
        }
        try {
            log.info("下载文件，group：{}，path：{}", groupName, path);
            DownloadFileWriter callback = new DownloadFileWriter(filePath);
            return fastFileStorageClient.downloadFile(groupName, path, callback);
        } catch (Exception e) {
            log.error("下载文件失败!", e);
        }
        return null;
    }

    /**
     * 下载文件，返回流
     *
     * @param groupName 组名，如：group1
     * @param path      路径名，如：M00/00/04/wKgAUFpO84CAA4HvAAAABs4Fkco168.txt
     * @return 附件输入流
     */
    public InputStream downloadStream(String groupName, String path) {
        try {
            byte[] content = downloadFile(groupName, path);
            return new ByteArrayInputStream(content);
        } catch (Exception e) {
            log.error("下载附件失败！", e);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param groupName 组名，如：group1
     * @param path      路径名，如：M00/00/04/wKgAUFpO84CAA4HvAAAABs4Fkco168.txt
     */
    public void deleteFile(String groupName, String path) {
        if (path.startsWith(groupName + "/")) {
            path = path.split(groupName + "/")[1];
        }
        fastFileStorageClient.deleteFile(groupName, path);
        log.info("删除文件成功，group：{}，path：{}", groupName, path);
    }

    /**
     * 修改文件
     *
     * @param groupName   组名，如：group1
     * @param oldPath     旧的路径
     * @param inputStream 附件输入流
     * @param size        附件大小
     * @param extName     扩展名
     * @return 完整路径
     * @author tangyi
     * @date 2018/1/5 12:01
     */
    public String modify(String groupName, String oldPath, InputStream inputStream, long size, String extName) throws Exception {
        String path = uploadFile(inputStream, size, extName);
        if (StringUtils.isBlank(path)) {
            return null;
        }
        deleteFile(groupName, oldPath);
        return path;
    }

    /**
     * 获取一个组
     *
     * @return 组名
     * @author tangyi
     * @date 2018/3/9 10:43
     */
    public String getGroup() {
        StorageNode storageNode = trackerClient.getStoreStorage();
        if (storageNode != null) {
            return storageNode.getGroupName();
        }
        return null;
    }

    /**
     * 获取所有组
     *
     * @return 可用的组列表
     * @author tangyi
     * @date 2018/3/9 10:42
     */
    public List<String> listGroups() {
        List<String> groups = new ArrayList<String>();
        List<GroupState> groupStates = trackerClient.listGroups();
        if (CollectionUtils.isNotEmpty(groupStates)) {
            for (GroupState state : groupStates) {
                groups.add(state.getGroupName());
            }
        }
        return groups;
    }

    /**
     * 获取带有token的访问地址
     *
     * @param fileUrl 示例：group1/M00/00/00/L2ZUml6QisqAUJE3AIOPO1HT6Bo274.mp4
     * @return java.lang.String 示例：http://yourIp:port/files/group1/M00/00/00/L2ZUml6QisqAUJE3AIOPO1HT6Bo274.mp4?token=e9a6ae7f1ecca6fed51e248c6a10d3bc&ts=1589361883
     */
    public String getTokenUrl(String fileUrl) throws Exception {
        String path = StorePath.parseFromUrl(fileUrl).getPath();
        //时间戳 单位为秒
        int ts = (int) (System.currentTimeMillis() / 1000);
        String token;
        try {
            token = ProtoCommon.getToken(path, ts, fastDfsKey);
        } catch (Exception e) {
            log.error("获取token异常", e);
            throw new Exception("FastDFS获取token异常");
        }

        return fastDfsUrl + fileUrl + "?token=" + token + "&ts=" + ts;
    }

    /**
     * 批量下载fastDFS文件
     *
     * @param filePaths
     * @param zipFileName
     */
    public InputStream download(List<String> filePaths, List<String> fileNames, String zipFileName, String group) {

        if (CollectionUtils.isEmpty(filePaths)) {
//            throw new Exception();
        }
        try {
            byte[] buffer = new byte[1024];
            // 创建一个新的 byte 数组输出流，它具有指定大小的缓冲区容量
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //创建一个新的缓冲输出流，以将数据写入指定的底层输出流
            BufferedOutputStream fos = new BufferedOutputStream(baos);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (int i = 0; i < filePaths.size(); i++) {
                //获取各个文件的数据流
                String filepath = toLocal(filePaths.get(i).trim());
                // Get the file name
                String filename = fileNames.get(i);
                log.debug("Download file, the file path is: {}, filename: {}", filepath, filename);
                InputStream fis = downloadStream(group, filepath);
                //压缩文件内的文件名称
                zos.putNextEntry(new ZipEntry(filename));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    //将文件读入压缩文件内
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.flush();

            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            return is;

        } catch (Exception ioe) {
            log.error(ioe.getMessage());
        }

        return null;
    }


    /**
     * Convert '\\\\' to '/' in the path, and convert the suffix name to lowercase
     *
     * @param path the file path
     * @return The converted path
     */
    private static String toLocal(String path) {
        if (StringUtils.isNotBlank(path)) {
            path = path.replaceAll("\\\\", "/");

            if (path.contains(".")) {
                String pre = path.substring(0, path.lastIndexOf(".") + 1);
                String suffix = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
                path = pre + suffix;
            }
        }
        return path;
    }
}