package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ApplicationNumber;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ApplicationNumberMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        //获取当前时间装换成20221118格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new Date());
        //查询数据库有没有今天发送的申请单
        QueryWrapper<ApplicationNumber> queryWrapper = new QueryWrapper<>();
        //以时间为开始结尾模糊匹配
        queryWrapper.likeRight("id", Integer.parseInt(date));
        List<ApplicationNumber> numberList = this.list(queryWrapper);
        //用数量组装 格式202211181
        date = date + numberList.size();
        ApplicationNumber applicationNumber = new ApplicationNumber();
        applicationNumber.applicationNumber(itemId, branchCode, SecurityUtils.getCurrentUser().getUsername(), SecurityUtils.getCurrentUser().getTenantId());
        applicationNumber.setId(Integer.parseInt(date));
        this.save(applicationNumber);
        return Integer.parseInt(date);
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
