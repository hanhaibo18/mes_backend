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

    @Select("SELECT mrd.*, prnd.note_id,prnd.drawing_no FROM produce_material_receive_detail mrd LEFT JOIN produce_request_note_detail prnd ON mrd.aply_num = prnd.note_id AND mrd.material_num = prnd.material_no  ${ew.customSqlSegment}")
    List<MaterialReceiveDetail> getReceiveDetail(@Param(Constants.WRAPPER) QueryWrapper<MaterialReceiveDetail> queryWrapper);
}
