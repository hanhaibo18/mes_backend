package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.base.BaseProductReceipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author wangchenyu
 * @description 针对表【base_product_connect(产品交接单据)】的数据库操作Mapper
 * @createDate 2023-06-05 09:23:01
 * @Entity generator.domain.BaseProductConnect
 */
@Mapper
public interface BaseProductReceiptMapper extends BaseMapper<BaseProductReceipt> {

    /**
     * @param page
     * @param wrapper
     * @Author: wcy
     * @Date: 2023/5/31 10:20
     * @return: IPage<BaseProductReceipt>
     **/
    @Select("SELECT\n" +
            "\twork_no,\n" +
            "\tdraw_no,\n" +
            "\t`status`,\n" +
            "\tGROUP_CONCAT( DISTINCT connect_no ) connect_no,\n" +
            "\tGROUP_CONCAT( DISTINCT product_no ) product_no,\n" +
            "\tGROUP_CONCAT( DISTINCT bom_name ) bom_name,\n" +
            "\tMAX( DISTINCT check_date ) check_date,\n" +
            "\tGROUP_CONCAT( DISTINCT receive_unit ) receive_unit,\n" +
            "\tGROUP_CONCAT( DISTINCT branch_name ) branch_name,\n" +
            "\tsum( number ) number\n" +
            "FROM\n" +
            "\tbase_product_receipt \n" +
            "GROUP BY\n" +
            "\twork_no,\n" +
            "\tdraw_no,\n" +
            "\t`status` \n" + " ${ew.customSqlSegment} ")
    IPage<BaseProductReceipt> queryPage(@Param("page") Page<BaseProductReceipt> page, @Param(Constants.WRAPPER) Wrapper<BaseProductReceipt> wrapper);

}




