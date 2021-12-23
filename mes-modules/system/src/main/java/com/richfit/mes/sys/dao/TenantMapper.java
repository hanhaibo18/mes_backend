package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author sun
 * @Description 租户Mapper
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
    String getAdditionalInfo(String tenantId);

    boolean saveAdditionalInfo(@Param("addInfo")String addInfo, @Param("tenantId")String tenantId);
}
