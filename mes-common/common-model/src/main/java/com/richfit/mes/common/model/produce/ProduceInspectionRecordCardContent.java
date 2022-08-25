package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhiqiang.lu
 */
@Data
@Accessors(chain = true)
@TableName("produce_inspection_record_card_content")
@ApiModel(value = "质量检验记录卡质检内容")
public class ProduceInspectionRecordCardContent extends BaseEntity<ProduceInspectionRecordCardContent> {

    private static final long serialVersionUID = -1044825101675722165L;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "生产线id")
    private String flowId;

    @ApiModelProperty(value = "序号")
    private String inspectionNo;

    @ApiModelProperty(value = "项目名称")
    private String inspectionName;

    @ApiModelProperty(value = "检查内容")
    private String inspectionContent;

    @ApiModelProperty(value = "技术要求")
    private String inspectionRequirement;

    @ApiModelProperty(value = "量具或者检测方法")
    private String inspectionTesting;

    @ApiModelProperty(value = "检测结果")
    private String inspectionResult;

    @ApiModelProperty(value = "合格 N不合格、Y合格")
    private String inspectionQualified;

    @ApiModelProperty(value = "检验员名称")
    private String inspectionUser_name;

    @ApiModelProperty(value = "检验日期")
    private String inspectionDate;
}
