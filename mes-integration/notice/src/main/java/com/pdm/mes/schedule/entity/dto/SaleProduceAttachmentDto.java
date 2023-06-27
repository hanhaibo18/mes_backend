package com.pdm.mes.schedule.entity.dto;

import com.pdm.mes.schedule.entity.domain.NoticeAttachment;
import lombok.Data;

import java.util.List;

@Data
public class SaleProduceAttachmentDto {
    /**
     * 销售排产单
     */
    List<SaleProductionSchedulingDto> schedulingDtoList;

    /**
     * PDM附件
     */
    List<NoticeAttachment> attachmentList;
}
