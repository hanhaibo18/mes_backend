package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sun
 * @Description 角色Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /**
     * 根据用户Id删除该用户的角色关系
     * @param userId 用户ID
     * @return boolean
     */
    Boolean deleteByUserId(@Param("userId") String userId);
    /**
     * 通过用户ID，查询角色信息
     * @param userId
     * @return
     */
    List<Role> queryRolesByUserId(String userId);
}
