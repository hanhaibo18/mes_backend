package com.richfit.mes.common.model.produce;

import lombok.Data;

import java.io.Serializable;

/**
 * produce_skill_notice_tenant
 *
 * @author
 */
@Data
public class SkillNoticeTenant implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 通知外键
     */
    private String skillId;
    /**
     * 单位
     */
    private String unit;
}
