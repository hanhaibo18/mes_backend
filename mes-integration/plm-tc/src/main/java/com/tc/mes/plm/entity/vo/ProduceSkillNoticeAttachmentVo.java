package com.tc.mes.plm.entity.vo;

import com.tc.mes.plm.entity.domain.ProduceSkillNoticeAttachment;
import com.tc.mes.plm.entity.dto.TechnicalNoticeDto;
import lombok.Data;

import java.util.List;

@Data
public class ProduceSkillNoticeAttachmentVo {

    private List<TechnicalNoticeDto> technicalNoticeDtoList;

    private List<ProduceSkillNoticeAttachment> attachmentList;
}
