package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.MaterialReceive;

import java.sql.Date;

/**
 * @className:MaterialReceiveService
 * @description: 类描述
 * @author:ang
 * @date:2022/7/29 17:54
 */
public interface MaterialReceiveService extends IService<MaterialReceive> {

    Date getlastTime();

    Page<MaterialReceive> getPage(Page<MaterialReceive> materialReceivePage, QueryWrapper<MaterialReceive> queryWrapper);
}
