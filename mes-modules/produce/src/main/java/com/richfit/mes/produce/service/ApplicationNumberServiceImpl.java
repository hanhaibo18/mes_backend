package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ApplicationNumber;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ApplicationNumberMapper;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ApplicationNumberServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 申请单号接口实现
 * @CreateTime: 2022年11月17日 10:24:00
 */
@Service
public class ApplicationNumberServiceImpl extends ServiceImpl<ApplicationNumberMapper, ApplicationNumber> implements ApplicationNumberService {

    @Override
    public int acquireApplicationNumber(String itemId, String branchCode) {
        ApplicationNumber applicationNumber = new ApplicationNumber();
        applicationNumber.applicationNumber(itemId, branchCode, SecurityUtils.getCurrentUser().getUsername(), SecurityUtils.getCurrentUser().getTenantId());
        this.save(applicationNumber);
        return applicationNumber.getId();
    }

    @Override
    public int queryApplicationNumber(String itemId) {
        QueryWrapper<ApplicationNumber> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        return this.getOne(queryWrapper).getId();
    }

    @Override
    public boolean deleteApplicationNumberByItemId(String itemId) {
        QueryWrapper<ApplicationNumber> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        return this.remove(queryWrapper);
    }

}
