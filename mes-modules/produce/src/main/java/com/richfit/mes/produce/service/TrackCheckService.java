package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.produce.entity.CountDto;
import java.util.List;

/**
 * @author 马峰
 * @Description 质检结果
 */
public interface TrackCheckService extends IService<TrackCheck> {
    
    List<CountDto> count(String dateType,String startTime,String endTime);
}
