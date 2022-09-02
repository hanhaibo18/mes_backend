package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @className:MaterialReceiveDetailMapper
 * @description: 类描述
 * @author:ang
 * @date:2022/8/2 9:21
 */
@Mapper
public interface MaterialReceiveDetailMapper extends BaseMapper<MaterialReceiveDetail> {

    @Select("SELECT mrd.*,prnd.drawing_no \n" +
            "FROM produce_material_receive_detail mrd \n" +
            "LEFT JOIN (select drawing_no,material_no from produce_request_note_detail  where id in (select max(id) as id from  produce_request_note_detail \n" +
            "GROUP BY material_no)) prnd ON mrd.material_num = prnd.material_no   ${ew.customSqlSegment}")
    List<MaterialReceiveDetail> getReceiveDetail(@Param(Constants.WRAPPER) QueryWrapper<MaterialReceiveDetail> queryWrapper);
}
