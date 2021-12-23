package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 租户菜单
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class TenantMenu extends BaseEntity<TenantMenu>{


    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 菜单id
     */
    private String menuId;


}
