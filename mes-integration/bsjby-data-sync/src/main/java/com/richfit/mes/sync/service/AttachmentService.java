package com.richfit.mes.sync.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.Attachment;

/**
 * @author sun
 * @Description 附件服务
 */
public interface AttachmentService extends IService<Attachment> {


    /**
     * 上传附件
     *
     * @param attachment attachment
     * @param bytes      bytes
     * @return Attachment
     */
    Attachment upload(Attachment attachment, byte[] bytes) throws Exception;


}
