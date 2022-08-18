package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.TenantMenu;

import java.util.List;

/**
 * <p>
 * 租户菜单 服务类
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
public interface TenantMenuService extends IService<TenantMenu> {

    List<Menu> queryTenantMenuByPId(String tenantId, String pId);

    Boolean saveTenantMenu(List<TenantMenu> menus, String tenantId);


}
