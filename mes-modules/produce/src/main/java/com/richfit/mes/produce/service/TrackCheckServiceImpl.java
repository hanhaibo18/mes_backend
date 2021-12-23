package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.produce.dao.TrackCheckCountMapper;
import com.richfit.mes.produce.dao.TrackCheckMapper;
import com.richfit.mes.produce.entity.CountDto;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mafeng
 * @Description 质检结果
 */
@Service
public class TrackCheckServiceImpl extends ServiceImpl<TrackCheckMapper, TrackCheck> implements TrackCheckService{

    @Autowired
    private TrackCheckMapper trackCheckMapper;
    
     @Autowired
    private TrackCheckCountMapper trackCheckCountMapper;
    
    public  List<CountDto> count(String dateType,String startTime,@Param("endTime") String endTime){
        return trackCheckCountMapper.count(dateType,startTime, endTime);
    }

}
