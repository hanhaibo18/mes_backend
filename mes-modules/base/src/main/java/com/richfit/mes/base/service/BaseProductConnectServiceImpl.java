package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.BaseProductConnectExtendMapper;
import com.richfit.mes.base.dao.BaseProductConnectMapper;
import com.richfit.mes.base.entity.ConnectDTO;
import com.richfit.mes.common.model.base.BaseProductConnect;
import com.richfit.mes.common.model.base.BaseProductConnectExtend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wangchenyu
 * @description 针对表【base_product_connect(产品交接单据)】的数据库操作Service实现
 * @createDate 2023-06-05 09:23:01
 */
@Service
public class BaseProductConnectServiceImpl extends ServiceImpl<BaseProductConnectMapper, BaseProductConnect> implements BaseProductConnectService {

    @Autowired
    private BaseProductConnectMapper baseProductConnectMapper;

    @Autowired
    private BaseProductConnectExtendMapper baseProductConnectExtendMapperMapper;

    @Override
    public Page queryConnectInfo(ConnectDTO connectDTO) {
        Page<BaseProductConnect> page = new Page<>(connectDTO.getPage(), connectDTO.getLimit());
        LambdaQueryWrapper<BaseProductConnect> baseProductConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        return baseProductConnectMapper.selectPage(page, baseProductConnectLambdaQueryWrapper);
    }

    @Override
    public Page queryConnectDetailInfo(String connectId, int currentPage, int limit) {
        Page<BaseProductConnectExtend> page = new Page<>(currentPage, limit);
        LambdaQueryWrapper<BaseProductConnectExtend> baseProductConnectExtendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductConnectExtendLambdaQueryWrapper.eq(BaseProductConnectExtend::getConnectId, connectId);
        return baseProductConnectExtendMapperMapper.selectPage(page, baseProductConnectExtendLambdaQueryWrapper);
    }
}




