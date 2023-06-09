package com.tc.mes.plm.entity.vo;

import lombok.Data;

/**
 * 技术通知单Vo
 */
@Data
public class ProduceSkillNoticeVo {
    /**
     * id
     */
    private String id;
    /**
     * 技术通知编号
     */
    private String skillNoticeNumber;

    /**
     * 技术通知名称
     */
    private String skillNoticeName;

    /**
     * 技术通知版本
     */
    private String skillNoticeRevId;

    /**
     * 工作号
     */
    private String workNo;
}
