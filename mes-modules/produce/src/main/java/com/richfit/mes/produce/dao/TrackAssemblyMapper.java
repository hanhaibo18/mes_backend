package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.TrackAssembly;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 马峰
 * @Description 报工Mapper
 */
@Mapper
public interface TrackAssemblyMapper extends BaseMapper<TrackAssembly> {

    @Select("SELECT\n" +
            "\tpta.drawing_no,\n" +
            "\tpta.NAME,\n" +
            "\tpta.material_no,\n" +
            "\tpta.unit,\n" +
            "\tmrd.quantity,\n" +
            "\tmrd.order_quantity \n" +
            "FROM\n" +
            "\tproduce_track_assembly AS pta\n" +
            "\tLEFT JOIN ( SELECT material_num, sum( quantity ) AS quantity, sum( order_quantity ) AS order_quantity FROM produce_material_receive_detail WHERE state = '0' GROUP BY material_num ) mrd ON pta.material_no = mrd.material_num \n" +
            "WHERE\n" +
            "\tpta.track_head_id = #{id} AND pta.material_no IS NOT NULL AND mrd.material_num IS NOT NULL \n" +
            "ORDER BY\n" +
            "\tpta.material_no DESC")
    Page<TrackAssembly> getDeliveredDetail(Page<TrackAssembly> trackAssemblyPage, String id);
}
