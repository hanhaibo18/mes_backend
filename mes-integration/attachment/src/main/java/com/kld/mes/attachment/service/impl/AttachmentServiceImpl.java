package com.kld.mes.attachment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kld.mes.attachment.mapper.AttachmentMapper;
import com.kld.mes.attachment.service.AttachmentService;
import com.kld.mes.attachment.entity.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;

/**
* @author llh
* @description 针对表【attachment(附件表)】的数据库操作Service实现
* @createDate 2023-06-20 15:07:03
*/
@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment>
    implements AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private FastDfsService fastDfsService;


    @Override
    public String uploadFile(InputStream inputStream, String fileName) throws Exception {
        int size = inputStream.available();
        Attachment attachment = new Attachment();
        attachment.setAttachSize(String.valueOf(size));
        attachment.setAttachName(fileName);
        attachment.setModifyTime(new Date());
        attachment.setCreateTime(new Date());
        attachment.setCreateBy("System");
        attachment.setModifyBy("System");
        attachment.setTenantId("test");

        String fastFileId = fastDfsService.uploadFile(inputStream,size,fileName);
        if (fastFileId != null) {
            String groupName = fastFileId.substring(0, fastFileId.indexOf("/"));
            attachment.setFastFileId(fastFileId);
            attachment.setGroupName(groupName);
            attachment.setAttachType(suffix(fileName));
            String url = getTokenUrl(attachment);
            attachment.setPreviewUrl(url);
        }
        attachmentMapper.insert(attachment);
        return attachment.getId();
    }

    private String getTokenUrl(Attachment attachment) throws Exception {
        return fastDfsService.getTokenUrl(attachment.getFastFileId());
    }

    /**
     * 获取后缀名
     * @param s
     * @return
     */
    private String suffix(String s) {
        String suffixType = "";
        for (int i = s.length() - 1;i>0;i--) {
            if (s.charAt(i) == '.') {
                suffixType = s.substring(i + 1);
                break;
            }
        }
        return suffixType;
    }
}




