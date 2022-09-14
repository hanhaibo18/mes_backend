package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@TableName(value = "produce_inspection_records_pt")
public class ProduceInspectionRecordPt extends BaseEntity<ProduceInspectionRecordPt> {
    private static final long serialVersionUID = -1472432735506772177L;
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "记录编号")
    private String recordNo;
    @ApiModelProperty(value = "检测部位")
    private String detectionOfParts;
    @ApiModelProperty(value = "种类")
    private String type;
    @ApiModelProperty(value = "检测地点")
    private String testSite;
    @ApiModelProperty(value = "检测时机")
    private String testOpportunity;
    @ApiModelProperty(value = "温度")
    private String tempera;
    @ApiModelProperty(value = "室温")
    private String isRoomTemp;
    @ApiModelProperty(value = "粗糙度")
    private String roughness;
    @ApiModelProperty(value = "清洗")
    private String washing;
    @ApiModelProperty(value = "渗透")
    private String permeation;
    @ApiModelProperty(value = "干燥")
    private String desiccation;
    @ApiModelProperty(value = "乳化")
    private String emulsification;
    @ApiModelProperty(value = "显像")
    private String development;
    @ApiModelProperty(value = "渗透剂")
    private String penetratingAgent;
    @ApiModelProperty(value = "清洗剂")
    private String cleaner;
    @ApiModelProperty(value = "显像剂")
    private String imagingAgent;
    @ApiModelProperty(value = "检测方法")
    private String testMethod;
    @ApiModelProperty(value = "预处理方式")
    private String pretreatMethod;
    @ApiModelProperty(value = "渗透剂施加方法")
    private String osmAgentMethod;
    @ApiModelProperty(value = "去除方法")
    private String removeMethod;
    @ApiModelProperty(value = "干燥方法")
    private String dryingMethod;
    @ApiModelProperty(value = "显像剂施加方法")
    private String developMethod;
    @ApiModelProperty(value = "渗透时间")
    private String penetrationTime;
    @ApiModelProperty(value = "干燥时间")
    private String dryingTime;
    @ApiModelProperty(value = "显像时间")
    private String developTime;
    @ApiModelProperty(value = "光照度")
    private String intensityOfIllumination;
    @ApiModelProperty(value = "检测比例")
    private String detectionRatio;
    @ApiModelProperty(value = "试片")
    private String testPiece;
    @ApiModelProperty(value = "试验规范")
    private String testSpecification;
    @ApiModelProperty(value = "验收标准")
    private String acceptanceCriteria;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "检测示意图文字描述")
    private String pictureRemark;
    @ApiModelProperty(value = "检测结果")
    private String inspectionResults;
    @ApiModelProperty(value = "检验员")
    private String checkBy;
    @ApiModelProperty(value = "业主")
    private String owner;
    @ApiModelProperty(value = "见证")
    private String witnesses;
    @ApiModelProperty(value = "审核人")
    private String auditBy;
    @ApiModelProperty(value = "模板类型")
    private String tempType = "pt";
}
