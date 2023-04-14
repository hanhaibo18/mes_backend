package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.AssignHot;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.QueryProcessVo;
import org.apache.ibatis.annotations.Delete;
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


    @Select("select * from (SELECT * FROM v_produce_track_item where  (Is_Current =1)  and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like concat('%',#{name},'%'))) a  ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatusAndTrack(@Param("page") Page page, @Param("name") String name, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);

    //@Select("select * from (SELECT * FROM produce_track_item where  (Is_Current =1 or id in(select id from produce_track_item a,  (select track_head_id,min(opt_sequence) as opt_sequence from produce_track_item  where track_head_id in (select id from produce_track_head where status ='0' or status is null or status='')   group by track_head_id)  b where  a.track_head_id=b.track_head_id and a.opt_sequence =b.opt_sequence)) and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where drawing_no=#{name})) a  ${ew.customSqlSegment}")
    @Select("select * from (SELECT * FROM v_produce_track_item where  (Is_Current =1)  and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where drawing_no LIKE CONCAT('%',CONCAT(#{name},'%')))) a  ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatusAndRouter(@Param("page") Page page, @Param("name") String name, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);

    //       @Select("select * from v_produce_assign")
    IPage<Assign> queryPage(Page page, @Param("siteId") String siteId, @Param("trackNo") String trackNo, @Param("routerNo") String routerNo, @Param("startTime") String starttime, @Param("endTime") String endTime, @Param("state") String state, @Param("userId") String userId, @Param("branchCode") String branchCode);

    @Select("select u.* from v_produce_assign u ${ew.customSqlSegment}")
    IPage<Assign> queryPageNew(Page page, @Param(Constants.WRAPPER) Wrapper<Assign> wrapper);

    @Select("select u.* from v_produce_assign_furnace u ${ew.customSqlSegment}")
    IPage<Assign> queryPageAssignTrackStore(Page page, @Param(Constants.WRAPPER) Wrapper<Assign> wrapper);

    @Select("select u.* from v_produce_assign_furnace_hot u ${ew.customSqlSegment}")
    IPage<AssignHot> queryPageAssignTrackStoreHot(Page page, @Param(Constants.WRAPPER) Wrapper<AssignHot> wrapper);
    @Select("select u.* from v_produce_assign_furnace u ${ew.customSqlSegment}")
    List<Assign> queryListAssignTrackStore(@Param(Constants.WRAPPER) Wrapper<Assign> wrapper);

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
     * @param flowId
     * @Author: xinYu.hou
     * @return: List<QueryProcessVo>
     **/
    @Select("SELECT item.id,item.track_head_id,item.opt_id,item.opt_name,item.opt_ver,item.prepare_end_hours,item.single_piece_hours,item.opt_parallel_type,item.is_current,item.is_doing,item.temp_work,item.hold_time,item.cool_type FROM v_produce_track_item item WHERE item.flow_id = #{flowId} ORDER BY item.opt_sequence ASC")
    List<QueryProcessVo> queryProcessList(@Param("flowId") String flowId);

    /**
     * 功能描述: 查询当前工序是否派工
     *
     * @param trackItemId
     * @Author: xinYu.hou
     * @return: Integer
     **/
    @Select("SELECT * FROM v_produce_assign assign WHERE assign.ti_id = #{trackItemId}")
    List<Assign> isDispatching(@Param("trackItemId") String trackItemId);

    /**
     * 功能描述: 查询未派工工序数量
     *
     * @param wrapper
     * @Author: xinYu.hou
     * @Date: 2022/9/26 16:40
     * @return: Integer
     **/
    @Select("SELECT COUNT(1) FROM (SELECT * FROM v_produce_track_item WHERE (Is_Current = 1) AND Assignable_Qty > 0) a ${ew.customSqlSegment}")
    Integer queryDispatchingNumber(@Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);

    IPage<TrackHead> getPageTrackHeadByType(Page page, @Param(Constants.WRAPPER) Wrapper<TrackHead> wrapper);

    /**
     * 功能描述: 派工和派工人员级联删除
     *
     * @param itemId
     * @Author: xinYu.hou
     * @Date: 2022/10/11 11:32
     * @return: boolean
     **/
    @Delete("DELETE assign,person FROM produce_assign AS assign LEFT JOIN produce_assign_person AS person ON assign.id = person.assign_id WHERE assign.ti_id = #{itemId}")
    boolean deleteAssignAndPerson(String itemId);
}
