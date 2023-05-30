package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.Notice;
import com.richfit.mes.sys.entity.dto.SalesSchedulingDto;

/**
 * @ClassName: NoticeService.java
 * @Author: Hou XinYu
 * @Description: 通知service
 * @CreateTime: 2023年05月29日 18:22:00
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 功能描述: 销售排产报公告分页查询接口
     *
     * @param salesSchedulingDto
     * @Author: xinYu.hou
     * @Date: 2023/5/29 19:06
     * @return: IPage<Notice>
     **/
    IPage<Notice> queryPage(SalesSchedulingDto salesSchedulingDto);

    /**
     * 功能描述: 接受通知
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2023/5/29 19:08
     * @return: Boolean
     **/
    Boolean acceptanceNotice(String id);

    /**
     * 功能描述: 通知退回
     *
     * @param id
     * @param reasonReturn
     * @Author: xinYu.hou
     * @Date: 2023/5/29 19:09
     * @return: Boolean
     **/
    Boolean noticeReturn(String id, String reasonReturn);
}
