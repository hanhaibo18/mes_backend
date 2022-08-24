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
            " <if test='id != null and id != \"\"'> " +
            "  and id = #{id} " +
            " </if> " +
            " <if test='startDate != null and startDate != \"\"'> " +
            "  and create_time &gt;= #{startDate} " +
            " </if> " +
            " <if test='endDate != null and endDate != \"\"'> " +
            "  and create_time &lt;= #{endDate} " +
            " </if> " +
            " <if test='templateCode != null and templateCode != \"\"'> " +
            "  and template_code = #{templateCode} " +
            " </if> " +
            " <if test='rollStatus != null and rollStatus != \"\"'> " +
            "  and status > 0 " +
            " </if> " +
            " <if test='status != null and status != \"\"'> " +
            "  and status in (${status}) " +
            " </if> " +
            " <if test='isCompletionData != null and isCompletionData != \"\"'> " +
            "  and is_completion_data = #{isCompletionData} " +
            " </if> " +
            " <if test='isCertificate != null and isCertificate != \"\"'> " +
            "   <if test='isCertificate == \"Y\"'> " +
            "       and certificate_no is not NULL " +
            "   </if>" +
            "   <if test='isCertificate == \"N\"'> " +
            "       and (certificate_no is null or certificate_no = '') " +
            "   </if>" +
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
            " <if test='workPlanId != null and workPlanId != \"\"'> " +
            "  and work_plan_id = #{workPlanId} " +
            " </if> " +
            " <if test='classes != null and classes != \"\"'> " +
            "  and classes = #{classes} " +
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
