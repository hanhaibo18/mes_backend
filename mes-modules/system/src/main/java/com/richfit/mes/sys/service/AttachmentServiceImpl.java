package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.oss.service.FastDfsService;
import com.richfit.mes.sys.dao.AttachmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sun
 * @Description 附件服务
 */
@Service
@Slf4j
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    @Autowired
    private FastDfsService fastDfsService;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    public IPage<Attachment> selectPage(Page page, QueryWrapper<Attachment> qw) {
        return attachmentMapper.selectPage(page, qw);
    }

    @Override
    public Attachment get(String id) {
        Attachment attachment = this.getById(id);
        if (attachment == null) {
            throw new GlobalException("attachment not found with id:" + id, ResultCode.ITEM_NOT_FOUND);
        }
        return attachment;
    }

    @Override
    public List<Attachment> selectAttachmentsList(List<String> idList) {
        return this.listByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment upload(Attachment attachment, byte[] bytes) throws Exception {
        attachment.setAttachSize(String.valueOf(bytes.length));
        String fastFileId = fastDfsService.uploadFile(new ByteArrayInputStream(bytes), bytes.length, attachment.getAttachType());
        System.out.println("fastFileId--------------------------------------");
        System.out.println(fastFileId);
        if (fastFileId != null) {
            String groupName = fastFileId.substring(0, fastFileId.indexOf("/"));
            attachment.setFastFileId(fastFileId);
            attachment.setGroupName(groupName);
        }
        attachmentMapper.insert(attachment);
        return attachment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Attachment attachment) {
        return this.save(attachment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Attachment attachment) {
        this.removeById(attachment.getId());
        if (StringUtils.isNotEmpty(attachment.getGroupName()) && StringUtils.isNotEmpty(attachment.getFastFileId())) {
            fastDfsService.deleteFile(attachment.getGroupName(), attachment.getFastFileId());
        }
        return Boolean.TRUE;
    }

    @Override
    public InputStream download(Attachment attachment) {
        return fastDfsService.downloadStream(attachment.getGroupName(), attachment.getFastFileId());
    }

    public InputStream downloadZip(List<Attachment> attachments, String zipName) {

        List<String> filePaths = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        String group = attachments.get(0).getGroupName();

        for (Attachment attachment : attachments) {
            filePaths.add(attachment.getFastFileId());
            if (StringUtils.isEmpty(attachment.getAttachName())) {
                fileNames.add(attachment.getId() + "." + attachment.getAttachType());
            } else {
                fileNames.add(attachment.getAttachName());
            }
        }

        return fastDfsService.download(filePaths, fileNames, zipName, group);
    }

    @Override
    public byte[] downloadbyte(Attachment attachment) {
        return fastDfsService.downloadFile(attachment.getGroupName(), attachment.getFastFileId());
    }


    @Override
    public String getTokenUrl(Attachment attachment) {
        try {
            return fastDfsService.getTokenUrl(attachment.getFastFileId());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Attachment attachment) {
        return attachmentMapper.updateById(attachment);
    }

}
