package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.TenantMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 租户菜单 Mapper 接口
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Mapper
public interface TenantMenuMapper extends BaseMapper<TenantMenu> {

    public List<Menu> queryTenantMenuByPId(String tenantId,String pId);
}
