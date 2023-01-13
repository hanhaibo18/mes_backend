package com.richfit.mes.common.model.produce;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * @author zhiqiang.lu
 */
@Data
@Accessors(chain = true)
@TableName("produce_track_head_flow")
@ApiModel(value = "跟单分流（生产线）")
public class TrackFlow extends BaseEntity<TrackFlow> {
    /**
     * 质量检测卡已审核
     */
    public static final String EXAMINE_CARD_DATA_YES = "Y";

    /**
     * 质量检测卡审核不通过
     */
    public static final String EXAMINE_CARD_DATA_NO = "N";

    /**
     * 质量检测卡修改后申请审核
     */
    public static final String EXAMINE_CARD_DATA_XG = "X";

    /**
     * 质量检测卡资料生成
     */
    public static final String CARD_DATA_YES = "Y";

    /**
     * 质量检测卡资料未生成
     */
    public static final String CARD_DATA_NO = "N";

    private static final long serialVersionUID = -1044825101675722165L;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @ApiModelProperty(value = "产品来源")
    private String productSource;

    @ApiModelProperty(value = "产品来源名称")
    private String productSourceName;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "跟单完工时间")
    private Date completeTime;

    @ApiModelProperty(value = "完工资料：Y是")
    private String isCompletionData;

    @ApiModelProperty(value = "完工资料附件")
    private String completionData;

    @ApiModelProperty(value = "检验记录卡审核状态  Y已审核 N审核未通过")
    private String isExamineCardData;

    @ApiModelProperty(value = "检验记录卡生成状态  Y已生成")
    private String isCardData;

    @ApiModelProperty(value = "检验记录卡文件")
    private String cardData;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "跟单id")
    private String trackHeadId;

    public static void param(
            String startDate,
            String endDate,
            String isExamineCardData,
            String isCardData,
            String templateCode,
            String status,
            String isCompletionData,
            String isCertificate,
            String productNo,
            String trackNo,
            String workNo,
            String drawingNo,
            String batchNo,
            String productionOrder,
            String workPlanId,
            String classes,
            String branchCode,
            String tenantId,
            String orderCol,
            String order,
            Map<String, String> map) {


        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("isExamineCardData", isExamineCardData);
        map.put("isCardData", isCardData);
        map.put("templateCode", templateCode);
        map.put("status", status);
        map.put("isCompletionData", isCompletionData);
        map.put("isCertificate", isCertificate);
        map.put("productNo", productNo);
        if (!StrUtil.isBlank(trackNo)) {
            map.put("trackNo", trackNo.replaceAll(" ", ""));
        }
        map.put("workNo", workNo);
        if (!StrUtil.isBlank(drawingNo)) {
            map.put("drawingNo", DrawingNoUtil.queryLikeSql("drawing_no", drawingNo));
        }
        map.put("batchNo", batchNo);
        map.put("productionOrder", productionOrder);
        map.put("workPlanId", workPlanId);
        map.put("classes", classes);
        map.put("branchCode", branchCode);
        map.put("tenantId", tenantId);
        map.put("orderCol", StrUtil.toUnderlineCase(orderCol));
        map.put("order", StrUtil.toUnderlineCase(order));
    }
}
