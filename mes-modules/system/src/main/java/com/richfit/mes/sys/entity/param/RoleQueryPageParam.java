package com.richfit.mes.sys.entity.param;


import com.richfit.mes.common.core.base.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author sun
 * @Description 角色查询参数
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class RoleQueryPageParam extends BasePageParam<RoleQueryParam> {
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    @ApiModelProperty(value = "角色名称")
    private String roleName;

}
