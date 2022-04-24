package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.WorkingHours;
import com.richfit.mes.common.model.produce.OrderTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gwb
 */
public interface WorkingHoursService extends IService<WorkingHours> {

    List<WorkingHours> selectOrderTime( @Param(Constants.WRAPPER) QueryWrapper<List> wrapper);
}
