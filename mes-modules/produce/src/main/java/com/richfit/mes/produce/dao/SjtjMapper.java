package com.richfit.mes.produce.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.produce.entity.SjtjDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface SjtjMapper extends BaseMapper<SjtjDto> {


    List<SjtjDto> query1(@Param("branchCode") String branchCode, @Param("createTime") String createTime, @Param("endTime") String endTime);

}
