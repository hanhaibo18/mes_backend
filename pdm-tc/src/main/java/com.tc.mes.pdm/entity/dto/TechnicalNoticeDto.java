package com.tc.mes.pdm.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * pdm 技术通知单
 */
@Data
public class TechnicalNoticeDto {
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
    /**
     *  附件
     */
    private List<String> previewUrl;
}
