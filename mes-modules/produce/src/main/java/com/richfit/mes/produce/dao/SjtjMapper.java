package com.richfit.mes.produce.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface SjtjMapper {

    List<Map> query1(@Param("createTime") String createTime,@Param("endTime") String endTime, @Param("branchCode")String branchCode);


    List<Map> query2(@Param("createTime") String createTime,@Param("endTime") String endTime,String branchCode);


    List<Map> query3(@Param("createTime") String createTime,@Param("endTime") String endTime,@Param("branchCode")String branchCode);


    List<Map> query4(@Param("createTime") String createTime,@Param("endTime") String endTime,@Param("branchCode")String branchCode);


}
