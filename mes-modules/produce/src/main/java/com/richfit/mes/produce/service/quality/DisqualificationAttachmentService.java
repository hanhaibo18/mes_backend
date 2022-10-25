package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.DisqualificationAttachment;

import java.util.List;

/**
 * @ClassName: DisqualificationAttachmentService.java
 * @Author: Hou XinYu
 * @Description: 不合格品文件中间表
 * @CreateTime: 2022年10月24日 11:10:00
 */
public interface DisqualificationAttachmentService extends IService<DisqualificationAttachment> {

    /**
     * 功能描述: 保存文件中间表数据
     *
     * @param attachments
     * @Author: xinYu.hou
     * @Date: 2022/10/24 11:14
     * @return: Boolean
     **/
    Boolean saveAttachment(List<DisqualificationAttachment> attachments);

    /**
     * 功能描述: 查询文件列表
     *
     * @param disqualificationId
     * @Author: xinYu.hou
     * @Date: 2022/10/24 17:03
     * @return: List<DisqualificationAttachment>
     **/
    List<DisqualificationAttachment> queryAttachmentsByDisqualificationId(String disqualificationId);
}
