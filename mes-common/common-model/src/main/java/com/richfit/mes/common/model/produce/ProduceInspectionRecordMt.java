package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@TableName(value = "produce_inspection_records_mt")
public class ProduceInspectionRecordMt extends BaseEntity<ProduceInspectionRecordMt> {
    private static final long serialVersionUID = -1472432735506772177L;
    @ApiModelProperty(value = "记录编号")
    private String recordNo;
    @ApiModelProperty(value = "报告编号")
    private String reportNo;
    @ApiModelProperty(value = "检测时机")
    private String testOpportunity;
    @ApiModelProperty(value = "检测部位")
    private String detectionOfParts;
    @ApiModelProperty(value = "检测地点")
    private String testSite;
    @ApiModelProperty(value = "种类")
    private String type;
    @ApiModelProperty(value = "检件规格")
    private String checkSpecification;
    @ApiModelProperty(value = "温度")
    private String tempera;
    @ApiModelProperty(value = "室温(0,1)")
    private String isRoomTemp;
    @ApiModelProperty(value = "粗糙度")
    private String roughness;
    @ApiModelProperty(value = "仪器名称")
    private String instrumentName;
    @ApiModelProperty(value = "仪器型号")
    private String instrumentModel;
    @ApiModelProperty(value = "灵敏度试片")
    private String sensitivityTestPiece;
    @ApiModelProperty(value = "检测比例")
    private String detectionRatio;
    @ApiModelProperty(value = "检测方法")
    private String detectionMethod;
    @ApiModelProperty(value = "磁化方法")
    private String magneticMethod;
    @ApiModelProperty(value = "磁化电流")
    private String magnetizingCurrent;
    @ApiModelProperty(value = "电流类型")
    private String currentType;
    @ApiModelProperty(value = "磁化方向")
    private String magneticDirection;
    @ApiModelProperty(value = "提升力")
    private String liftPower;
    @ApiModelProperty(value = "磁轭间距")
    private String yokeSpacing;
    @ApiModelProperty(value = "磁粉种类")
    private String typeMagneticPowder;
    @ApiModelProperty(value = "磁粉载体")
    private String magneticCarrier;
    @ApiModelProperty(value = "荧光/非荧光")
    private String fluorescent;
    @ApiModelProperty(value = "磁悬液浓度")
    private String concentrationMagneticSuspension;
    @ApiModelProperty(value = "磁粉施加方法")
    private String magneticPowderMethod;
    @ApiModelProperty(value = "磁化时间")
    private String magneticTime;
    @ApiModelProperty(value = "试验规范")
    private String testSpecification;
    @ApiModelProperty(value = "退磁")
    private String isMagnetic;
    @ApiModelProperty(value = "剩磁")
    private String remaMagnetic;
    @ApiModelProperty(value = "光照度")
    private String intensityOfIllumination;
    @ApiModelProperty(value = "黑光辐照度")
    private String irradiance;
    @ApiModelProperty(value = "验收标准")
    private String acceptanceCriteria;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "检测示意图文字描述")
    private String pictureRemark;
    @ApiModelProperty(value = "检验员")
    private String checkBy;
    @ApiModelProperty(value = "审核人")
    private String auditBy;
    @ApiModelProperty(value = "业主")
    private String owner;
    @ApiModelProperty(value = "见证")
    private String witnesses;
    @ApiModelProperty(value = "检测结果")
    private String inspectionResults;
    @ApiModelProperty(value = "模板类型")
    private String tempType = "mt";
    @ApiModelProperty(value = "是否审核")
    private String isAudit;

}
