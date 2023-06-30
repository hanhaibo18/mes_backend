package com.tc.mes.plm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tc.mes.plm.entity.domain.ProduceSkillNotice;
import com.tc.mes.plm.entity.request.TechnicalNoticeRequest;

import java.util.List;


/**
* @author llh
* @description 针对表【produce_skill_notice(技术通知单)】的数据库操作Service
* @createDate 2023-06-09 13:59:31
*/
public interface ProduceSkillNoticeService extends IService<ProduceSkillNotice> {

    boolean saveBatchNotice(List<TechnicalNoticeRequest> noticeRequestList);
}
