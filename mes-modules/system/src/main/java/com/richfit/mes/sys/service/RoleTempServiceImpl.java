package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.RoleTemp;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.dao.RoleMapper;
import com.richfit.mes.sys.dao.RoleTempMapper;
import com.richfit.mes.sys.entity.param.RoleQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author sun
 * @Description 角色服务
 */
@Slf4j
@Service
public class RoleTempServiceImpl extends ServiceImpl<RoleTempMapper, RoleTemp> implements RoleTempService {

}
