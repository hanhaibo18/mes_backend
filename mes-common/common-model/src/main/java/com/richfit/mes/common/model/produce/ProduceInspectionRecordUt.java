package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "produce_inspection_records_ut")
public class ProduceInspectionRecordUt extends BaseEntity<ProduceInspectionRecordUt> {
    private static final long serialVersionUID = -1472432735506772177L;
    @ApiModelProperty(value = "记录编号")
    private String recordNo;
    @ApiModelProperty(value = "报告编号")
    private String reportNo;
    @ApiModelProperty(value = "检测部位")
    private String detectionOfParts;
    @ApiModelProperty(value = "种类")
    private String type;
    @ApiModelProperty(value = "焊接方法")
    private String weldingMethod;
    @ApiModelProperty(value = "检测时机")
    private String testOpportunity;
    @ApiModelProperty(value = "坡口形式")
    private String grooveForm;
    @ApiModelProperty(value = "温度")
    private String tempera;
    @ApiModelProperty(value = "室温(0,1)")
    private String isRoomTemp;
    @ApiModelProperty(value = "粗糙度")
    private String roughness;
    @ApiModelProperty(value = "检测日期")
    private String inspectionDate;
    @ApiModelProperty(value = "仪器型号")
    private String instrumentModel;
    @ApiModelProperty(value = "探头型号")
    private String probeModel;
    @ApiModelProperty(value = "检测面")
    private String detectionSurface;
    @ApiModelProperty(value = "耦合剂")
    private String couplingAgent;
    @ApiModelProperty(value = "对比试样")
    private String compareSample;
    @ApiModelProperty(value = "试验规范")
    private String testSpecification;
    @ApiModelProperty(value = "检测比例")
    private String detectionRatio;
    @ApiModelProperty(value = "灵敏度")
    private String sensitivity;
    @ApiModelProperty(value = "补偿")
    private String compensation;
    @ApiModelProperty(value = "扫描量程复核")
    private String scanRangeRecheck;
    @ApiModelProperty(value = "扫查灵敏度复核")
    private String scanSensitivityRecheck;
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
    @ApiModelProperty(value = "模板类型")
    private String tempType = "ut";
   /* @TableField(exist = false,value = "缺陷记录")
    private List<ProduceDefectsInfo> defectsInfoList;*/
    @TableField(exist = false,value = "探头集合")
    private List<ProbeInfo> probeInfoList;
    @ApiModelProperty(value = "检验结果")
    private String inspectionResults;

}
