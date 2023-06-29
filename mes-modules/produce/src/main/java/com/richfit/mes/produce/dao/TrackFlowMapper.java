package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.TrackHeadPublicVo;
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
            "  and ${startDate} " +
            " </if> " +
            " <if test='endDate != null and endDate != \"\"'> " +
            "  and ${endDate} " +
            " </if> " +
            " <if test='isExamineCard != null and isExamineCard != \"\"'> " +
            "  and is_examine_card = #{isExamineCard} " +
            " </if> " +
            " <if test='isExamineCardData != null and isExamineCardData != \"\"'> " +
            "  and is_examine_card_data = #{isExamineCardData} " +
            " </if> " +
            " <if test='isCardData != null and isCardData != \"\"'> " +
            "  and is_card_data = #{isCardData} " +
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
            " <if test='certificateNo != null and certificateNo != \"\"'> " +
            "  and certificate_no = #{certificateNo} " +
            " </if> " +
            " <if test='productNo != null and productNo != \"\"'> " +
            "  and product_no like concat('%',#{productNo},'%') " +
            " </if> " +
            " <if test='trackNo != null and trackNo != \"\"'> " +
            " and replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like concat('%',#{trackNo},'%') " +
            " </if> " +
            " <if test='workNo != null and workNo != \"\"'> " +
            "  and work_no like concat('%',#{workNo},'%') " +
            " </if> " +
            " <if test='drawingNo != null and drawingNo != \"\"'> " +
            "  and ${drawingNo} " +
            " </if> " +
            " <if test='batchNo != null and batchNo != \"\"'> " +
            "  and batch_no like concat('%',#{batchNo},'%') " +
            " </if> " +
            " <if test='productionOrder != null and productionOrder != \"\"'> " +
            "  and production_order = #{productionOrder} " +
            " </if> " +
            " <if test='productionOrderId != null and productionOrderId != \"\"'> " +
            "  and production_order_id = #{productionOrderId} " +
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
            " <if test='orderCol != null and orderCol != \"\"'> " +
            " order by ${orderCol} ${order},modify_time desc" +
            " </if> " +
            "</script>")
    List<TrackHead> selectTrackFlowList(Map<String, String> map);

    /**
     * 功能描述: 复写跟单分流信息查询
     *
     * @Author: longlinhui
     * @Date: 2023/3/7 9:46
     **/
    @Select("<script>" +
            " select * from v_produce_track_head where 1=1 " +
            " <if test='id != null and id != \"\"'> " +
            "  and id = #{id} " +
            " </if> " +
            " <if test='startDate != null and startDate != \"\"'> " +
            "  and ${startDate} " +
            " </if> " +
            " <if test='endDate != null and endDate != \"\"'> " +
            "  and ${endDate} " +
            " </if> " +
            " <if test='isExamineCard != null and isExamineCard != \"\"'> " +
            "  and is_examine_card = #{isExamineCard} " +
            " </if> " +
            " <if test='isExamineCardData != null and isExamineCardData != \"\"'> " +
            "  and is_examine_card_data = #{isExamineCardData} " +
            " </if> " +
            " <if test='isCardData != null and isCardData != \"\"'> " +
            "  and is_card_data = #{isCardData} " +
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
            " and replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like concat('%',#{trackNo},'%') " +
            " </if> " +
            " <if test='workNo != null and workNo != \"\"'> " +
            "  and work_no like concat('%',#{workNo},'%') " +
            " </if> " +
            " <if test='drawingNo != null and drawingNo != \"\"'> " +
            "  and ${drawingNo} " +
            " </if> " +
            " <if test='batchNo != null and batchNo != \"\"'> " +
            "  and batch_no like concat('%',#{batchNo},'%') " +
            " </if> " +
            " <if test='productionOrder != null and productionOrder != \"\"'> " +
            "  and production_order = #{productionOrder} " +
            " </if> " +
            " <if test='productionOrderId != null and productionOrderId != \"\"'> " +
            "  and production_order_id = #{productionOrderId} " +
            " </if> " +
            " <if test='workPlanId != null and workPlanId != \"\"'> " +
            "  and work_plan_id = #{workPlanId} " +
            " </if> " +
            " <if test='isTestBar != null and isTestBar != \"\"'> " +
            "  and is_test_bar = #{isTestBar} " +
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
            " <if test='orderCol != null and orderCol != \"\"'> " +
            " order by ${orderCol} ${order},modify_time desc" +
            " </if> " +
            "</script>")
    List<TrackHeadPublicVo> selectTrackFlowInfoList(Map<String, String> map);

    @Select("SELECT i.flow_id FROM produce_track_item i WHERE i.next_opt_sequence NOT IN ( SELECT original_opt_sequence FROM produce_track_item m WHERE m.flow_id = i.flow_id ) AND i.tenant_id = '12345678901234567890123456789002' AND i.next_opt_sequence <> 0 GROUP BY flow_id limit 100")
    List<String> queryBugItemFlow();
}
