package com.tc.mes.plm.entity.dto;

import lombok.Data;

/**
 * pdm 技术通知单
 */
@Data
public class TechnicalNoticeDto {
    /**
     * id
     */
    private String id;
    /**
     * 技术通知单编号
     */
    private String techNoticeNo;
    /**
     *  技术通知单版本id
     */
    private String techNoticeRevId;
    /**
     *  技术通知单名称
     */
    private String techNoticeName;
    /**
     *  工作号
     */
    private String workNo;
}
