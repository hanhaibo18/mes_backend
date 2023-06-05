package com.richfit.mes.produce.service.notice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.SkillNotice;
import com.richfit.mes.produce.entity.AcceptDispatchDto;
import com.richfit.mes.produce.entity.DispatchDto;
import com.richfit.mes.produce.entity.SkillIssueNoticeDto;
import com.richfit.mes.produce.entity.SkillNoticeDto;

import java.util.List;

/**
 * @ClassName: SkillNoticeService.java
 * @Author: Hou XinYu
 * @Description: 技术通知
 * @CreateTime: 2023年06月02日 15:46:00
 */
public interface SkillNoticeService extends IService<SkillNotice> {

    /**
     * 功能描述: 接受技术通知
     *
     * @param skillNoticeDto
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:37
     * @return: IPage<SkillNotice>
     **/
    IPage<SkillNotice> querySkillPage(SkillNoticeDto skillNoticeDto);

    /**
     * 功能描述: 接受通知
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:38
     * @return: Boolean
     **/
    Boolean acceptanceOfNotice(List<String> idList);

    /**
     * 功能描述: 转调度通知
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:38
     * @return: Boolean
     **/
    Boolean dispatchNotification(List<String> idList);

    /**
     * 功能描述: 调度通知
     *
     * @param dispatchDto
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:38
     * @return: IPage<SkillNotice>
     **/
    IPage<SkillNotice> queryDispatchPage(DispatchDto dispatchDto);

    /**
     * 功能描述: 通知编辑
     *
     * @param skillNotice
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:38
     * @return: Boolean
     **/
    Boolean updateDispatch(SkillNotice skillNotice);

    /**
     * 功能描述: 通知下发
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:38
     * @return: Boolean
     **/
    Boolean dispatchNoticeDelivery(SkillIssueNoticeDto issueNoticeDto);

    /**
     * 功能描述: 接受调度通知
     *
     * @param acceptDispatchDto
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:38
     * @return: IPage<SkillNotice>
     **/
    IPage<SkillNotice> receiveDispatchNotification(AcceptDispatchDto acceptDispatchDto);

    /**
     * 功能描述: 通知确认
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2023/6/2 17:38
     * @return: Boolean
     **/
    Boolean receiveDispatchNotificationAffirm(List<String> idList);
}
