package com.tc.mes.pdm.entity.request;

import lombok.Data;

import java.util.List;

/**
 * pdm 技术通知单
 */
@Data
public class TechnicalNoticeRequest {
    /**
     * 技术通知单编号
     */
    private String tech_notice_no;
    /**
     *  技术通知单版本id
     */
    private String tech_notice_rev_id;
    /**
     *  技术通知单名称
     */
    private String tech_notice_name;
    /**
     *  工作号
     */
    private String work_no;
    /**
     *  附件
     */
    private List<String> preview_url;
}
