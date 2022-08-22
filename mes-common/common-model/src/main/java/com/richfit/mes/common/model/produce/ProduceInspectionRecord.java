package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "produce_inspection_records")
public class ProduceInspectionRecord extends BaseEntity<ProduceInspectionRecord> {
    private static final long serialVersionUID = -1472432735506772177L;
    @ApiModelProperty(value = "工序id")
    private String itemId;
    @ApiModelProperty(value = "检测部位")
    private String detectionOfParts;
    @ApiModelProperty(value = "检件规格")
    private String checkSpecification;
    @ApiModelProperty(value = "样品状态")
    private String sampleStatus;
    @ApiModelProperty(value = "环境条件")
    private String environmentalConditions;
    @ApiModelProperty(value = "收样日期")
    private Date receivedDate;
    @ApiModelProperty(value = "检测地点")
    private String testSite;
    @ApiModelProperty(value = "检测比例")
    private String detectionRatio;
    @ApiModelProperty(value = "照度")
    private String intensityOfIllumination;
    @ApiModelProperty(value = "提升力")
    private String liftPower;
    @ApiModelProperty(value = "磁化电流(交流、直流)")
    private String magnetizingCurrent;
    @ApiModelProperty(value = "磁化规范")
    private String magnetizationSpecification;
    @ApiModelProperty(value = "磁粉种类")
    private String typeMagneticPowder;
    @ApiModelProperty(value = "磁悬液浓度")
    private String concentrationMagneticSuspension;
    @ApiModelProperty(value = "磁粉施加方法")
    private String magneticPowderMethod;
    @ApiModelProperty(value = "灵敏度试片")
    private String sensitivityTestPiece;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "缺陷情况")
    private String defectsInfo;
    @TableField(exist = false)
    private List<String> itemIds;

}
