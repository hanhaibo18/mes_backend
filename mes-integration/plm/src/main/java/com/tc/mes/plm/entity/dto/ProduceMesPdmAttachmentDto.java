package com.tc.mes.plm.entity.dto;

import com.tc.mes.plm.entity.domain.ProduceMesPdmAttachment;
import lombok.Data;

import java.util.List;

@Data
public class ProduceMesPdmAttachmentDto {

    private List<TechnicalNoticeDto> technicalNoticeDtoList;

    private List<ProduceMesPdmAttachment> attachmentList;
}
