package com.richfit.mes.sys.entity.param;


import com.richfit.mes.common.core.base.BaseParam;
import com.richfit.mes.common.model.sys.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * @author sun
 * @Description 角色查询参数
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleQueryParam extends BaseParam<Role> {
    private String roleCode;
    private String roleName;
    private String tenantId;
}
