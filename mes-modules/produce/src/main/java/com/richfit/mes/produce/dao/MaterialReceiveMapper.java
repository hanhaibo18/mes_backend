package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.MaterialReceive;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @className:MaterialReceiveMapper
 * @description: 类描述
 * @author:ang
 * @date:2022/7/29 17:56
 */
@Mapper
public interface MaterialReceiveMapper extends BaseMapper<MaterialReceive> {

    Date getlastTime(String tenantId);

    Page<MaterialReceive> getPage(Page<MaterialReceive> materialReceivePage, @Param(Constants.WRAPPER) QueryWrapper<MaterialReceive> queryWrapper);
}
