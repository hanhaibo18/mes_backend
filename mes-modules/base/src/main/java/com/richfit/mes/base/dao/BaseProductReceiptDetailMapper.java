package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.base.BaseProductReceiptDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wangchenyu
 * @description 针对表【base_product_connect(产品交接单据)】的数据库操作Mapper
 * @createDate 2023-06-05 09:23:01
 * @Entity generator.domain.BaseProductConnect
 */
@Mapper
public interface BaseProductReceiptDetailMapper extends BaseMapper<BaseProductReceiptDetail> {


    /**
     * @param wrapper
     * @Author: wcy
     * @Date: 2023/5/31 10:20
     * @return: List<BaseProductReceiptDetail>
     **/
    @Select("SELECT\n" +
            "\twork_no,\n" +
            "\tdraw_no,\n" +
            "\tpart_drawing_no,\n" +
            "\tGROUP_CONCAT( DISTINCT part_name ) part_name,\n" +
            "\tSUM( deliver_number ) deliver_number,\n" +
            "\tSUM( demand_number * number ) demand_number \n" +
            "FROM\n" +
            "\tbase_product_receipt_detail \n" +
            "GROUP BY\n" +
            "\twork_no,\n" +
            "\tdraw_no,\n" +
            "\tpart_drawing_no" + " ${ew.customSqlSegment} ")
    List<BaseProductReceiptDetail> queryDetailInfo(@Param(Constants.WRAPPER) Wrapper<BaseProductReceiptDetail> wrapper);
}




