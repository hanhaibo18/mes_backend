package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 操作信息服务
 */
@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action> implements ActionService{

    @Autowired
    private ActionMapper actionMapper;

    @Override
    public Boolean saveAction(Action action) {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        action.setUser(user.getUsername());
        action.setTenantId(user.getTenantId());
        action.setBranchId(user.getOrgId());
        action.setActionTime(new Date());
        return actionMapper.insert(action) > 0 ? true : false;
    }
}
