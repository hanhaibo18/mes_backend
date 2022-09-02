package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhiqiang.lu
 * @Description 角色工序配置表
 */
@Data
@ApiModel(value = "角色工序配置表")
public class ProduceRoleOperation extends BaseEntity<TrackHead> {
    private static final long serialVersionUID = 2780348291041220734L;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "sys角色id")
    private String role_id;

    @ApiModelProperty(value = "base工序id")
    private String operation_id;

    @ApiModelProperty(value = "工序代码(无用)")
    private String operation_code;

    @ApiModelProperty(value = "工序名称")
    private String operation_name;
}
