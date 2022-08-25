package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author sun
 * @Description 角色
 * @modifierTime: 2022年8月25日14:53:51
 * @modifier: hou
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class UserRole extends BaseEntity<UserRole> {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 角色ID
     */
    private String roleId;

    @ApiModelProperty(value = "用户类型(0=普通,1=普通质检,2=质检租户)", dataType = "String")
    private String userType;

}
