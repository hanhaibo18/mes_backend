package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.DisqualificationAttachment;
import com.richfit.mes.produce.dao.quality.DisqualificationAttachmentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: DisqualificationAttachmentServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 不合格品文件
 * @CreateTime: 2022年10月24日 11:18:00
 */
@Service
public class DisqualificationAttachmentServiceImpl extends ServiceImpl<DisqualificationAttachmentMapper, DisqualificationAttachment> implements DisqualificationAttachmentService {

    @Override
    public Boolean saveAttachment(List<DisqualificationAttachment> attachments) {
        return this.saveBatch(attachments);
    }
}
