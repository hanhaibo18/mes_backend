package com.richfit.mes.sync.service;

import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.sync.config.SyncTableConfig;
import com.richfit.mes.sync.config.SyncTaskConfig;
import com.richfit.mes.sync.dao.DataMapper;
import com.richfit.mes.sync.entity.RawData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * @author gaol
 * @date 2023/2/9
 * @apiNote
 */
@Service
@Slf4j
public class SyncDataService {

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    DataMapper dataMapper;

    /**
     * 支持对多张表的附件字段进行处理转换，单线程依次处理
     * 中断异常处理：增加处理后的时间戳标记，中断后可以恢复。查询指定表中的未同步附件记录，未同步的判断规则：指定字段值为null
     * 具体处理逻辑请参考代码中的 标号注释
     * 特殊处理1 附件路径很多重复的，没必要传多次，只传输1次，剩下的统一更新id即可。
     **/
    public String runSyncDataTask(SyncTaskConfig configs) throws Exception {

        StringBuilder retMsg = new StringBuilder();

        for (SyncTableConfig s : configs.getTableConfigs()) {
            syncSingleTable(configs, s);
        }
        return retMsg.toString();
    }

    private void syncSingleTable(SyncTaskConfig configs, SyncTableConfig s) throws Exception {

        //1 根据配置查询数据
        Map parMap = getParMap(configs, s);
        int page = 0;
        boolean flag = true;
        while (flag) {
            parMap.put("page", page);
            parMap.put("count", s.getBatchCount());
            List<RawData> list = dataMapper.selectRawData(parMap);

            //2 逐条解析原始存储附件路径字段，获取文件
            for (RawData data : list) {
                File file = new File(configs.getFileCatalog() + data.getRawFilePath());
                if (file.exists()) {
                    //3 调用附件存储服务，存储文件
                    Attachment attachment = uploadAttachment(configs, data, file);
                    //4 更新新路径字段
                    Map updateMap = getDataMap(s, data, attachment, data.getRawFilePath());
                    dataMapper.batchUpdateAttachmentId(updateMap);
                    log.info("file trans success [{}]", file.getAbsolutePath());
                } else {
                    log.error("file not exists [{}]", file.getAbsolutePath());
                }
            }

            // 如果分页查询数据量 < 每页数量，则认为查询到最后一页，跳出循环
            if (list == null || list.size() < s.getBatchCount() || list.size() == 0) {
                flag = false;
            }
            //page++;
        }
    }

    private Attachment uploadAttachment(SyncTaskConfig configs, RawData data, File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fis.read(fileBytes);
        fis.close();

        Attachment attachment = getAttach(fileBytes, FileUtils.getFilename(data.getRawFilePath()), configs.getTenantId());
        attachment = attachmentService.upload(attachment, fileBytes);
        return attachment;
    }

    private static Map getDataMap(SyncTableConfig s, RawData data, Attachment attachment, String oldPath) {
        Map updateMap = new HashMap(8);
        updateMap.put("tableName", s.getTableName());
        updateMap.put("targetColum", s.getTargetColum());
        updateMap.put("newFilePath", attachment.getId());
        updateMap.put("rawAttachmentColum", s.getRawAttachmentColum());
        updateMap.put("oldPath", oldPath.replace("\\", "\\\\"));
        updateMap.put("tempUpdateDateColum", s.getTempUpdateDateColum());
        return updateMap;
    }

    private static Map getParMap(SyncTaskConfig configs, SyncTableConfig s) {
        Map parMap = new HashMap<>(8);
        parMap.put("keyColum", s.getKeyColum());
        parMap.put("rawAttachmentColum", s.getRawAttachmentColum());
        parMap.put("tableName", s.getTableName());
        parMap.put("tenantId", configs.getTenantId());
        parMap.put("tempUpdateDateColum", s.getTempUpdateDateColum());
        return parMap;
    }

    private Attachment getAttach(byte[] fileBytes, String fileName, String tenantId) {

        Attachment attachment = new Attachment();
        attachment.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        attachment.setTenantId(tenantId);
        attachment.setAttachType(FileUtils.getFilenameExtension(fileName));
        attachment.setAttachSize(String.valueOf(fileBytes.length));
        attachment.setAttachName(fileName);

        attachment.setCreateBy("sync_soft");
        attachment.setModifyBy("sync_soft");
        attachment.setCreateTime(new Date());
        attachment.setModifyTime(new Date());

        return attachment;
    }

}
