package com.richfit.mes.base.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.OperationAssignMapper;
import com.richfit.mes.common.model.base.OperationAssign;
import com.richfit.mes.common.model.produce.AssignPerson;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 王瑞
 * @Description 工艺派工服务
 */
@Service
public class OperationAssignServiceImpl extends ServiceImpl<OperationAssignMapper, OperationAssign> implements OperationAssignService {


    /**
     * 根据工序名称获取工序派工信息
     * @param optName
     * @param branchCode
     * @return
     */
    @Override
    public OperationAssign getOperatinoByParam(String optName, String branchCode) {
        QueryWrapper<OperationAssign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("opt_name", optName);
        queryWrapper.eq("branch_code", branchCode);
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        OperationAssign one = this.getOne(queryWrapper);
        ArrayList<AssignPerson> assignPeoples = new ArrayList<>();

        if (!ObjectUtil.isEmpty(one) && !StringUtils.isNullOrEmpty(one.getUserId())) {
            List<String> list = Arrays.asList(one.getUserId().split(","));
            for (String userId : list) {
                AssignPerson assignPerson = new AssignPerson();
                assignPerson.setUserId(userId);
                assignPeoples.add(assignPerson);
            }
            one.setAssignPersons(assignPeoples);
        }
        return one;
    }
}
