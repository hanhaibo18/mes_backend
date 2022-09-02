package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "produce_inspection_records_rt")
public class ProduceInspectionRecordRt extends BaseEntity<ProduceInspectionRecordRt> {
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
    @ApiModelProperty(value = "线形象质计")
    private String lineMeter;
    @ApiModelProperty(value = "灵敏度")
    private String sensitivity;
    @ApiModelProperty(value = "电压")
    private String voltage;
    @ApiModelProperty(value = "电流")
    private String electricity;
    @ApiModelProperty(value = "曝光时间")
    private String exposureTime;
    @ApiModelProperty(value = "手洗")
    private String handWash;
    @ApiModelProperty(value = "显影时间（min）")
    private String developingTime;
    @ApiModelProperty(value = "定影时间（min）")
    private String fixingTime;
    @ApiModelProperty(value = "水洗时间（min）")
    private String washingTime;
    @ApiModelProperty(value = "黑度范围")
    private String blacknessScope;
    @ApiModelProperty(value = "射源尺寸")
    private String sourceSize;
    @ApiModelProperty(value = "前后屏厚度")
    private String screenThickness;
    @ApiModelProperty(value = "焦距")
    private String focalLength;
    @ApiModelProperty(value = "机洗")
    private String machineWash;
    @ApiModelProperty(value = "试验规范")
    private String testSpecification;
    @ApiModelProperty(value = "验收标准")
    private String acceptanceCriteria;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "检测员及技术资格")
    private String inspectorLevel;
    @ApiModelProperty(value = "核验员")
    private String checkBy;
    @ApiModelProperty(value = "模板类型")
    private String tempType = "rt";
    @TableField(exist = false,value = "缺陷记录")
    private List<ProduceDefectsInfo> defectsInfoList;
    @ApiModelProperty(value = "透照厚度")
    private String transThickness;

}
