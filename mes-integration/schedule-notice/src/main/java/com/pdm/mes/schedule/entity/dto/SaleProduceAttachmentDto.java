package com.pdm.mes.schedule.entity.dto;

import com.pdm.mes.schedule.entity.domain.ProduceMesPdmAttachment;
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
    List<ProduceMesPdmAttachment> attachmentList;
}
