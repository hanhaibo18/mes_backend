package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.TrackItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序Mapper
 */
@Mapper
public interface TrackItemMapper extends BaseMapper<TrackItem> {

    List<TrackItem> selectTrackItem(@Param(Constants.WRAPPER) Wrapper<TrackItem> query);

    List<TrackItem> selectTrackItemAssign(@Param(Constants.WRAPPER) Wrapper<TrackItem> query);

    List<TrackItem> selectNextItem(@Param("tiId") String tiId);

    int updateTrackItemIsCurrent(@Param("tiId") String tiId);

    /**
     * 功能描述: 查询当前跟单下最后一条已派工的工序
     *
     * @param trackHeadId
     * @Author: xinYu.hou
     * @Date: 2022/10/27 10:00
     * @return: TrackItem
     **/
    @Select("SELECT * FROM produce_track_item item WHERE item.is_current = 1 AND item.track_head_id = #{trackHeadId} ORDER BY item.sequence_order_by DESC LIMIT 1")
    TrackItem getTrackItemByHeadId(@Param("trackHeadId") String trackHeadId);

    /**
     * 功能描述: 查询已派工最大工序号以下所有工序
     *
     * @param trackHeadId
     * @Author: xinYu.hou
     * @Date: 2022/10/28 17:22
     * @return: List<TrackItem>
     **/
    @Select("SELECT * FROM produce_track_item item WHERE item.sequence_order_by <= (SELECT MAX(item.sequence_order_by) FROM produce_track_item item WHERE item.is_schedule = 1 AND item.track_head_id = #{trackHeadId}) AND item.track_head_id = #{trackHeadId} ORDER BY item.opt_sequence DESC")
    List<TrackItem> getTrackItems(@Param("trackHeadId") String trackHeadId);

    //查询跟单最大完工的工序
    @Select("SELECT *,(select product_no from produce_track_head_flow f where f.id = s.flow_id) as product_no_flow FROM produce_track_item item,(SELECT i.flow_id,max( i.opt_sequence ) AS opt_sequence FROM produce_track_item i WHERE i.is_final_complete = '1' and i.track_head_id = #{trackHeadId} GROUP BY i.flow_id   ORDER BY opt_sequence LIMIT 1) s WHERE item.track_head_id = #{trackHeadId} AND item.opt_sequence = s.opt_sequence ORDER BY item.opt_sequence;")
    List<TrackItem> getFinalTrackItems(@Param("trackHeadId") String trackHeadId);
}
