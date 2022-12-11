package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.sys.ItemClass;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.dao.ItemParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 字典分类 服务实现类
 * </p>
 *
 * @author 王瑞
 * @since 2020-08-05
 */
@Service
public class ItemParamServiceImpl extends ServiceImpl<ItemParamMapper, ItemParam> implements ItemParamService {

    @Autowired
    ItemParamMapper itemParamMapper;

    @Resource
    private ItemClassService itemClassService;

    @Override
    public List<ItemParam> queryItemByCode(String code) throws Exception {
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<ItemClass>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        List<ItemClass> iClasses = itemClassService.list(queryWrapper);
        if (iClasses.size() > 0) {
            QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
            wrapper.eq("class_id", iClasses.get(0).getId());
            if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
                wrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            }
            wrapper.orderByAsc("order_num");
            return this.list(wrapper);
        } else {
            throw new Exception("没有找到key=" + code + "的字典！");
        }
    }

    @Override
    public List<ItemParam> queryItemByCodeAndTenantId(String code,String tenantId) throws Exception {
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<ItemClass>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
            queryWrapper.eq("tenant_id", tenantId);
        }
        List<ItemClass> iClasses = itemClassService.list(queryWrapper);
        if (iClasses.size() > 0) {
            QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
            wrapper.eq("class_id", iClasses.get(0).getId());
            if (tenantId != null) {
                wrapper.eq("tenant_id", tenantId);
            }
            wrapper.orderByAsc("order_num");
            return this.list(wrapper);
        } else {
            throw new Exception("没有找到key=" + code + "的字典！");
        }
    }
}
