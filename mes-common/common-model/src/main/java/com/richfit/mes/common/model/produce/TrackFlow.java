package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

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
}
