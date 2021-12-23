package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.sys.dao.ItemParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
