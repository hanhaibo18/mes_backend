package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.QueryProcessVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 马峰
 * @Description 派工Mapper
 */
@Mapper
public interface TrackAssignMapper extends BaseMapper<Assign> {

    @Select("select * from (SELECT * FROM v_produce_track_item where  (Is_Current =1)  and Assignable_Qty>0) a ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatus(@Param("page") Page page, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);


    @Select("select * from (SELECT * FROM v_produce_track_item where  (Is_Current =1)  and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where track_no LIKE CONCAT(CONCAT(#{name},'%')))) a  ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatusAndTrack(@Param("page") Page page, @Param("name") String name, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);

    //@Select("select * from (SELECT * FROM produce_track_item where  (Is_Current =1 or id in(select id from produce_track_item a,  (select track_head_id,min(opt_sequence) as opt_sequence from produce_track_item  where track_head_id in (select id from produce_track_head where status ='0' or status is null or status='')   group by track_head_id)  b where  a.track_head_id=b.track_head_id and a.opt_sequence =b.opt_sequence)) and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where drawing_no=#{name})) a  ${ew.customSqlSegment}")
    @Select("select * from (SELECT * FROM v_produce_track_item where  (Is_Current =1)  and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where drawing_no LIKE CONCAT(CONCAT(#{name},'%')))) a  ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatusAndRouter(@Param("page") Page page, @Param("name") String name, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);

    //       @Select("select * from v_produce_assign")
    IPage<Assign> queryPage(Page page, @Param("siteId") String siteId, @Param("trackNo") String trackNo, @Param("routerNo") String routerNo, @Param("startTime") String starttime, @Param("endTime") String endTime, @Param("state") String state, @Param("userId") String userId, @Param("branchCode") String branchCode);

    @Select("select u.* from v_produce_assign u ${ew.customSqlSegment}")
    IPage<Assign> queryPageNew(Page page, @Param(Constants.WRAPPER) Wrapper<Assign> wrapper);

    /**
     * 功能描述: 查询派工还视图
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/7/13 14:08
     * @return: Assign
     **/
    @Select("select u.* from v_produce_assign u where u.id = #{id}")
    Assign queryAssign(@Param("id") String id);

    /**
     * 功能描述: 根据跟单号查询
     *
     * @param trackNo
     * @Author: xinYu.hou
     * @return: List<QueryProcessVo>
     **/
    @Select("SELECT item.id,item.track_head_id,item.opt_id,item.opt_name,item.opt_ver,item.prepare_end_hours,item.single_piece_hours,item.opt_parallel_type,item.is_current FROM v_produce_track_item item WHERE item.track_head_id = #{trackNo} ORDER BY item.opt_sequence ASC")
    List<QueryProcessVo> queryProcessList(@Param("trackNo") String trackNo);

    /**
     * 功能描述: 查询当前工序是否派工
     *
     * @param trackItemId
     * @Author: xinYu.hou
     * @return: Integer
     **/
    @Select("SELECT assign.state FROM v_produce_assign assign WHERE assign.ti_id = #{trackItemId}")
    Integer isDispatching(@Param("trackItemId") String trackItemId);


    IPage<TrackHead> getPageTrackHeadByType(Page page, @Param(Constants.WRAPPER) Wrapper<TrackHead> wrapper);

}
