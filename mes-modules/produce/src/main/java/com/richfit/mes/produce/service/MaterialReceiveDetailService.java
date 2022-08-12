package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;

import java.util.List;

/**
 * @className:MaterialReceiveDetailService
 * @description: 类描述
 * @author:ang
 * @date:2022/8/2 9:19
 */
public interface MaterialReceiveDetailService extends IService<MaterialReceiveDetail> {
    Page<MaterialReceiveDetail> getReceiveDetail(QueryWrapper<MaterialReceiveDetail> queryWrapper);
}
