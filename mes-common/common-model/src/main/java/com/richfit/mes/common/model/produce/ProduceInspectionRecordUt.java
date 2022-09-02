package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "produce_inspection_records_ut")
public class ProduceInspectionRecordUt extends BaseEntity<ProduceInspectionRecordUt> {
    private static final long serialVersionUID = -1472432735506772177L;
    @ApiModelProperty(value = "记录编号")
    private String recordNo;
    @ApiModelProperty(value = "委托单号")
    private String orderNo;
    @ApiModelProperty(value = "报告编号")
    private String reportNo;
    @ApiModelProperty(value = "委托单位")
    private String entrustDepartment;
    @ApiModelProperty(value = "工程名称")
    private String projectName;
    @ApiModelProperty(value = "样品名称")
    private String sampleName;
    @ApiModelProperty(value = "样品图号")
    private String sampleDrawNo;
    @ApiModelProperty(value = "出厂编号")
    private String factoryNo;
    @ApiModelProperty(value = "样品编号")
    private String sampleNo;
    @ApiModelProperty(value = "检测部位")
    private String detectionOfParts;
    @ApiModelProperty(value = "种类 0、铸 1、锻 2、 焊")
    private String type;
    @ApiModelProperty(value = "焊接方法")
    private String weldingMethod;
    @ApiModelProperty(value = "坡口形式")
    private String grooveForm;
    @ApiModelProperty(value = "检件规格")
    private String checkSpecification;
    @ApiModelProperty(value = "检件材质")
    private String checkMaterial;
    @ApiModelProperty(value = "照度")
    private String intensityOfIllumination;
    @ApiModelProperty(value = "样品状态")
    private Date sampleStatus;
    @ApiModelProperty(value = "环境条件")
    private String environmentalConditions;
    @ApiModelProperty(value = "收样日期")
    private Date receivedDate;
    @ApiModelProperty(value = "检测地点")
    private String testSite;
    @ApiModelProperty(value = "检测比例")
    private String detectionRatio;
    @ApiModelProperty(value = "检测日期")
    private Date inspectionDate;
    @ApiModelProperty(value = "透照方式")
    private String transmissionWay;
    @ApiModelProperty(value = "仪器型号")
    private String instrumentModel;
    @ApiModelProperty(value = "仪器编号")
    private String instrumentCode;
    @ApiModelProperty(value = "上次检定时间")
    private String lastCheckDate;
    /*@ApiModelProperty(value = "下次检定时间")
    private String nextCheckDate;*/
    @ApiModelProperty(value = "探头型号")
    private String probeModel;
    @ApiModelProperty(value = "直探头参数盲区")
    private String blindArea;
    @ApiModelProperty(value = "直探头参数远场分辨力")
    private String farResolution;
    @ApiModelProperty(value = "直探头参数灵敏度余量")
    private String sensitivityMargin;
    @ApiModelProperty(value = "斜探头参数双峰")
    private String bimodal;
    @ApiModelProperty(value = "斜探头参数主声束偏离")
    private String mainBeamDeviation;
    @ApiModelProperty(value = "斜探头参数探头前沿")
    private String probeFrontier;
    @ApiModelProperty(value = "斜探头参数实测K值")
    private String kValue;
    @ApiModelProperty(value = "检测面")
    private String detectionSurface;
    @ApiModelProperty(value = "耦合剂")
    private String couplingAgent;
    @ApiModelProperty(value = "试块")
    private String block;
    @ApiModelProperty(value = "试块腐蚀和机械损伤")
    private String damage;
    @ApiModelProperty(value = "灵敏度")
    private String sensitivity;
    @ApiModelProperty(value = "补偿")
    private String compensation;
    @ApiModelProperty(value = "扫描量程复核")
    private String scanRangeRecheck;
    @ApiModelProperty(value = "扫查灵敏度复核")
    private String scanSensitivityRecheck;
    @ApiModelProperty(value = "检测标准/技术等级")
    private String checkLevel;
    @ApiModelProperty(value = "验收标准")
    private String acceptanceCriteria;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "检测员及技术资格")
    private String inspectorLevel;
    @ApiModelProperty(value = "核验员")
    private String checkBy;
    @ApiModelProperty(value = "模板类型")
    private String tempType = "ut";
    @TableField(exist = false,value = "缺陷记录")
    private List<ProduceDefectsInfo> defectsInfoList;

}
