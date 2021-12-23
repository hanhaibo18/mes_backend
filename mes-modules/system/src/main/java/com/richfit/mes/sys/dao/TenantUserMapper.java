package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 租户用户 Mapper 接口
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Mapper
public interface TenantUserMapper extends BaseMapper<TenantUser> {

    IPage<TenantUserVo> queryTenantUser(Page page, @Param("param")TenantUserQueryParam tenantUserQueryParam);
}
