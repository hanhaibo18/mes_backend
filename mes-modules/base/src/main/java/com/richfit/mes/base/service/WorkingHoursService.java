package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.WorkingHours;

import java.util.List;

/**
 * @author gwb
 */
public interface WorkingHoursService extends IService<WorkingHours> {

    List<WorkingHours> selectOrderTime();
}
