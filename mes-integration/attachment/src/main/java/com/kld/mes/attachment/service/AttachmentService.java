package com.kld.mes.attachment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kld.mes.attachment.entity.Attachment;

import java.io.InputStream;


/**
* @author llh
* @description 针对表【attachment(附件表)】的数据库操作Service
* @createDate 2023-06-20 15:07:03
*/
public interface AttachmentService extends IService<Attachment> {

    /**
     * 上传文件流
     * @param inputStream
     * @param fileName
     * @return
     * @throws Exception
     */
    String uploadFile(InputStream inputStream, String fileName) throws Exception;

}
