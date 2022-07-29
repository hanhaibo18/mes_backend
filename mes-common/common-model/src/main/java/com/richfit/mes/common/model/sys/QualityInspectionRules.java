package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sys_quality_inspection_rules
 *
 * @author hou
 */
@Data
public class QualityInspectionRules extends BaseEntity<QualityInspectionRules> {

    /**
     * 状态名称
     */
    @ApiModelProperty(value = "状态名称", dataType = "String")
    private String stateName;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", dataType = "String")
    private String state;

    /**
     * 是否给予工时
     */
    @ApiModelProperty(value = "是否给予工时", dataType = "String")
    private String isGiveTime;

    /**
     * 是否下一步
     */
    @ApiModelProperty(value = "是否下一步", dataType = "String")
    private String isNext;

    private String tenantId;

    /**
     * 机构代码
     */
    @ApiModelProperty(value = "机构代码", dataType = "String")
    private String branchCode;

}
