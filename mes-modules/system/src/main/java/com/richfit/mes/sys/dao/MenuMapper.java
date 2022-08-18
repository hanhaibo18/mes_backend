package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.sys.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 系统菜单 Mapper 接口
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 通过角色编号查询菜单
     *
     * @param roleId 角色ID
     * @return
     */
    public List<Menu> listMenusByRoleId(String roleId, String tenantId);


    public List<Menu> listMenuByTenantId(String tenantId);

}
