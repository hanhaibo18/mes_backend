package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.produce.entity.CountDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 马峰
 * @Description 质检Mapper
 */
@Mapper
public interface TrackCheckCountMapper extends BaseMapper<CountDto> {
    List<CountDto> count(String dateType,String startTime, String endTime);
    List<CountDto> countReason(String startTime,String endTime);
    List<CountDto> countComplete(String dateType,String startTime, String endTime);
   }
