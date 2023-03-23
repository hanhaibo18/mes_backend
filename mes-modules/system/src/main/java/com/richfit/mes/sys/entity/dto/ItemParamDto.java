package com.richfit.mes.sys.entity.dto;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author llh
 */
@Data
public class ItemParamDto extends BaseEntity<ItemParamDto> {

    private String tenantId;

    /**
     * 编码序号
     */
    private String ruleNo;

    /**
     * 分类ID
     */
    private String classId;

    /**
     * 编码
     */
    private String code;

    /**
     * 参数名称
     */
    private String label;

    /**
     * 参数类型 0整数型 1浮点型 2布尔型
     */
    private String type;

    /**
     * 单位
     */
    private String unit;

    /**
     * 排序
     */
    private Integer orderNum;
}
