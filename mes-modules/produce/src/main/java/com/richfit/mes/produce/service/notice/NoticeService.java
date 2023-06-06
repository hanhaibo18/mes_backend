package com.richfit.mes.produce.service.notice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Notice;
import com.richfit.mes.produce.entity.*;

import java.util.List;

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
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2023/5/29 19:08
     * @return: Boolean
     **/
    Boolean acceptanceNotice(List<String> idList);

    /**
     * 功能描述: 通知退回
     *
     * @param sendBackDto
     * @Author: xinYu.hou
     * @Date: 2023/5/29 19:09
     * @return: Boolean
     **/
    Boolean noticeReturn(SendBackDto sendBackDto);

    /**
     * 功能描述: 生产排产接口
     *
     * @param productionSchedulingDto
     * @Author: xinYu.hou
     * @Date: 2023/5/30 16:58
     * @return: IPage<Notice>
     **/
    IPage<Notice> queryProductionSchedulingPage(ProductionSchedulingDto productionSchedulingDto);

    /**
     * 功能描述:
     *
     * @param notice
     * @Author: xinYu.hou
     * @Date: 2023/5/30 17:20
     * @return: Boolean
     **/
    Boolean updateProductionScheduling(Notice notice);

    /**
     * 功能描述: 通知下发
     *
     * @param issueNoticeDto
     * @Author: xinYu.hou
     * @Date: 2023/5/30 17:33
     * @return: Boolean
     **/
    Boolean issueNotice(IssueNoticeDto issueNoticeDto);


    /**
     * 功能描述: 取消排产
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2023/5/30 17:36
     * @return: Boolean
     **/
    Boolean cancelProductionScheduling(List<String> idList);

    /**
     * 功能描述: 接受生产排产通知
     *
     * @param acceptingDto
     * @Author: xinYu.hou
     * @Date: 2023/5/30 19:01
     * @return: IPage<Notice>
     **/
    IPage<Notice> queryAcceptingPage(AcceptingDto acceptingDto);

    /**
     * 功能描述: 修改接受状态 确认/取消
     *
     * @param updateAcceptingState
     * @Author: xinYu.hou
     * @Date: 2023/5/30 19:03
     * @return: Boolean
     **/
    Boolean updateAcceptingState(UpdateAcceptingStateDto updateAcceptingState);
}
