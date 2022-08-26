package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "produce_inspection_records_pt")
public class ProduceInspectionRecordPt extends BaseEntity<ProduceInspectionRecordPt> {
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
    @ApiModelProperty(value = "检件规格")
    private String checkSpecification;
    @ApiModelProperty(value = "检件材质")
    private String checkMaterial;
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
    @ApiModelProperty(value = "检测剂有效日期")
    private String testAgentDeadline;
    @ApiModelProperty(value = "检测方法(0、清洗  1、渗透 2、干燥 3、乳化 4、显像)")
    private String testMethod;
    @ApiModelProperty(value = "渗透剂")
    private String penetratingAgent;
    @ApiModelProperty(value = "清洗剂")
    private String cleaner;
    @ApiModelProperty(value = "显像剂")
    private String imagingAgent;
    @ApiModelProperty(value = "清洗方法")
    private String cleaningMethod;
    @ApiModelProperty(value = "渗透时间")
    private String penetrationTime;
    @ApiModelProperty(value = "显像时间")
    private String developingTime;
    @ApiModelProperty(value = "试验规范")
    private String testSpecification;
    @ApiModelProperty(value = "验收标准")
    private String acceptanceCriteria;
    @ApiModelProperty(value = "灵敏度试片")
    private String sensitivityTestPiece;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "检测员及技术资格")
    private String inspectorLevel;
    @ApiModelProperty(value = "核验员")
    private String checkBy;
    @ApiModelProperty(value = "检测日期")
    private Date inspectionDate;
    @ApiModelProperty(value = "模板类型")
    private String tempType;
    @TableField(exist = false,value = "缺陷记录")
    private List<ProduceDefectsInfo> defectsInfoList;

}
