package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 马峰
 * @Description 派工Mapper
 */
@Mapper
public interface TrackAssignMapper extends BaseMapper<Assign> {
    
     @Select("select * from (SELECT * FROM produce_track_item where  (Is_Current =1)  and Assignable_Qty>0) a ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatus(@Param("page") Page page, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);
    

       @Select("select * from (SELECT * FROM produce_track_item where  (Is_Current =1)  and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where track_no=#{name})) a  ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatusAndTrack(@Param("page") Page page,@Param("name") String name, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);

       //@Select("select * from (SELECT * FROM produce_track_item where  (Is_Current =1 or id in(select id from produce_track_item a,  (select track_head_id,min(opt_sequence) as opt_sequence from produce_track_item  where track_head_id in (select id from produce_track_head where status ='0' or status is null or status='')   group by track_head_id)  b where  a.track_head_id=b.track_head_id and a.opt_sequence =b.opt_sequence)) and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where drawing_no=#{name})) a  ${ew.customSqlSegment}")
    @Select("select * from (SELECT * FROM produce_track_item where  (Is_Current =1)  and Assignable_Qty>0 and track_head_id in (select id from produce_track_head where drawing_no=#{name})) a  ${ew.customSqlSegment}")
    IPage<TrackItem> getPageAssignsByStatusAndRouter(@Param("page") Page page,@Param("name") String name, @Param(Constants.WRAPPER) Wrapper<TrackItem> wrapper);
   
       IPage<Assign> queryPage(Page page, @Param("siteId") String siteId,@Param("trackNo") String trackNo,@Param("routerNo") String routerNo, @Param("startTime") String starttime, @Param("endTime") String endTime, @Param("state") String state, @Param("userId") String userId, @Param("branchCode") String branchCode);
       
}
