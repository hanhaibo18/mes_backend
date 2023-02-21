package com.richfit.mes.sync.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.oss.service.FastDfsService;
import com.richfit.mes.sync.dao.AttachmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

/**
 * @author sun
 * @Description 附件服务
 */
@Service
@Slf4j
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    @Autowired
    private FastDfsService fastDfsService;

    @Autowired()
    private AttachmentMapper attachmentMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment upload(Attachment attachment, byte[] bytes) throws Exception {
        attachment.setAttachSize(String.valueOf(bytes.length));
        String fastFileId = fastDfsService.uploadFile(new ByteArrayInputStream(bytes), bytes.length, attachment.getAttachType());
        if (fastFileId != null) {
            String groupName = fastFileId.substring(0, fastFileId.indexOf("/"));
            attachment.setFastFileId(fastFileId);
            attachment.setGroupName(groupName);
            String url = getTokenUrl(attachment);
            attachment.setPreviewUrl(url);
        }
        attachmentMapper.insert(attachment);
        return attachment;
    }

    public String getTokenUrl(Attachment attachment) {
        try {
            return fastDfsService.getTokenUrl(attachment.getFastFileId());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }


}
