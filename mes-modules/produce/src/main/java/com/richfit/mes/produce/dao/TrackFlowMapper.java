package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author zhiqiang.lu
 * @Description 跟单分流Mapper
 */
@Mapper
public interface TrackFlowMapper extends BaseMapper<TrackFlow> {
    /**
     * 功能描述: 跟单分流信息查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     **/
    @Select("<script>" +
            " select * from v_produce_track_head_flow where 1=1 " +
            " <if test='startDate != null and startDate != \"\"'> " +
            "  and create_time &gt;= #{startDate} " +
            " </if> " +
            " <if test='endDate != null and endDate != \"\"'> " +
            "  and create_time &lt;= #{endDate} " +
            " </if> " +
            " <if test='templateCode != null and templateCode != \"\"'> " +
            "  and template_code = #{templateCode} " +
            " </if> " +
            " <if test='status != null and status != \"\"'> " +
            "  and status = #{status} " +
            " </if> " +
            " <if test='productNo != null and productNo != \"\"'> " +
            "  and product_no like concat('%',#{productNo},'%') " +
            " </if> " +
            " <if test='trackNo != null and trackNo != \"\"'> " +
            "  and track_no like concat('%',#{trackNo},'%') " +
            " </if> " +
            " <if test='workNo != null and workNo != \"\"'> " +
            "  and work_no like concat('%',#{workNo},'%') " +
            " </if> " +
            " <if test='drawingNo != null and drawingNo != \"\"'> " +
            "  and drawing_no like concat('%',#{drawingNo},'%') " +
            " </if> " +
            " <if test='batchNo != null and batchNo != \"\"'> " +
            "  and batch_no like concat('%',#{batchNo},'%') " +
            " </if> " +
            " <if test='productionOrder != null and productionOrder != \"\"'> " +
            "  and production_order like concat('%',#{productionOrder},'%') " +
            " </if> " +
            " <if test='branchCode != null and branchCode != \"\"'> " +
            "  and branch_code = #{branchCode} " +
            " </if> " +
            " <if test='tenantId != null and tenantId != \"\"'> " +
            "  and tenant_id = #{tenantId} " +
            " </if> " +
            " order by modify_time desc,product_no asc" +
            "</script>")
    List<TrackHead> selectTrackFlowList(Map<String, String> map);
}
