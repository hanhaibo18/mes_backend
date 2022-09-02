package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "produce_role_operation")
public class ProduceRoleOperation extends BaseEntity<TrackHead> {
    private static final long serialVersionUID = 2780348291041220734L;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "sys角色id")
    private String roleId;

    @ApiModelProperty(value = "base工序id")
    private String operationId;

    @ApiModelProperty(value = "工序代码(无用)")
    private String operationCode;

    @ApiModelProperty(value = "工序名称")
    private String operationName;
}
