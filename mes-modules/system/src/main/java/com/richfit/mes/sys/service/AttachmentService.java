package com.richfit.mes.sys.service;

import com.richfit.mes.common.model.sys.Attachment;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.io.InputStream;

/**
 * @author sun
 * @Description 附件服务
 */
public interface AttachmentService {

    /**
     * 分页查询
     * @param page
     * @param qw
     * @return Attachment
     */
     IPage<Attachment> selectPage(Page page, QueryWrapper<Attachment> qw);
    
    
    /**
     * 根据id查询
     *
     * @param id
     * @return Attachment
     */
    Attachment get(String id);

    /**
     * 上传附件
     *
     * @param attachment attachment
     * @param bytes      bytes
     * @return Attachment
     */
    Attachment upload(Attachment attachment, byte[] bytes);

    /**
     * 保存附件信息
     *
     * @param attachment attachment
     * @return int
     */
    boolean add(Attachment attachment);

    /**
     * 删除附件
     *
     * @param attachment attachment
     * @return boolean
     */
    boolean delete(Attachment attachment);

    /**
     * 下载
     *
     * @param attachment attachment
     * @return InputStream
     */
    InputStream download(Attachment attachment);

    /**
     * 获取带token的url
     *
     * @param attachment attachment
     * @return String
     */
    String getTokenUrl(Attachment attachment);
    
    
    /**
     * 更新文件
     *
     * @param attachment attachment
     * @return Attachment
     */
    int update(Attachment attachment);

}
