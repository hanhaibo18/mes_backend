package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.sys.Attachment;

import java.io.InputStream;
import java.util.List;

/**
 * @author sun
 * @Description 附件服务
 */
public interface AttachmentService {

    /**
     * 分页查询
     *
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
     * 功能描述: 获取多条记录
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2022/6/30 17:26
     * @return: List<Attachment>
     **/
    List<Attachment> selectAttachmentsList(List<String> idList);

    /**
     * 上传附件
     *
     * @param attachment attachment
     * @param bytes      bytes
     * @return Attachment
     */
    Attachment upload(Attachment attachment, byte[] bytes) throws Exception;

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
     * 下载zip包
     *
     * @param attachments zipName
     * @return InputStream
     */
    InputStream downloadZip(List<Attachment> attachments, String zipName);

    byte[] downloadbyte(Attachment attachment);

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

    /**
     * 获取图片base64编码串
     * @param id
     * @return
     */
    Object getImageStr(String id);

}
