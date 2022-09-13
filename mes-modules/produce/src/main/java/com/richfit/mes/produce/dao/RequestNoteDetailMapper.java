package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.RequestNoteDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 功能描述: 申请单详情
 *
 * @Author: xinYu.hou
 * @Date: 2022/8/1 14:32
 **/
@Mapper
public interface RequestNoteDetailMapper extends BaseMapper<RequestNoteDetail> {

    /**
     * 功能描述: 通过配送主表id查询配送零件详细信息
     *
     * @param noteId 配送主表id
     * @Author: zhiqiang.lu
     * @Date: 2022/9/8 14:32
     **/
    @Select("select n.*,r.quantity as number_delivery from produce_material_receive_detail r right join produce_request_note_detail n on (r.aply_num=n.request_note_number and r.material_num = n.material_no) where n.note_id = #{noteId}")
    List<RequestNoteDetail> getDeliveryInformation(String noteId);
}
