package com.richfit.mes.sys.entity.param;

import com.richfit.mes.common.core.base.BaseParam;
import com.richfit.mes.common.model.sys.TenantUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author sun
 * @Description 用户查询参数
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantUserQueryParam extends BaseParam<TenantUser> {
    private String userAccount;
    private String emplName;
    private String orgId;
    private String tenantId;
}
