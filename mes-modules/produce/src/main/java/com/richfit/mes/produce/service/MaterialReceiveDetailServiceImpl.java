package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.produce.dao.MaterialReceiveDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/8/2 9:20
 */
@Service
public class MaterialReceiveDetailServiceImpl extends ServiceImpl<MaterialReceiveDetailMapper, MaterialReceiveDetail> implements MaterialReceiveDetailService{

    @Autowired
    MaterialReceiveDetailMapper materialReceiveDetailMapper;

    @Override
    public Page<MaterialReceiveDetail> getReceiveDetail(Page<MaterialReceiveDetail> materialReceiveDetailPage, QueryWrapper<MaterialReceiveDetail> queryWrapper) {
        return materialReceiveDetailMapper.getReceiveDetail(materialReceiveDetailPage,queryWrapper);
    }
}
