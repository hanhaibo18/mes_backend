package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "produce_inspection_records_mt")
public class ProduceInspectionRecordMt extends BaseEntity<ProduceInspectionRecordMt> {
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
    @ApiModelProperty(value = "提升力")
    private String liftPower;
    @ApiModelProperty(value = "磁化电流(交流、直流)")
    private String magnetizingCurrent;
    @ApiModelProperty(value = "磁化规范 0.磁轭1.线圈2.直接通电3.中心导体4.支杆5.其它6.连续法7.剩磁法8.非荧光9.荧光10.纵向11.横向12.湿粉13.退磁")
    private String magnetizationSpecification;
    @ApiModelProperty(value = "磁粉种类")
    private String typeMagneticPowder;
    @ApiModelProperty(value = "磁悬液浓度")
    private String concentrationMagneticSuspension;
    @ApiModelProperty(value = "磁粉施加方法")
    private String magneticPowderMethod;
    @ApiModelProperty(value = "试验规范")
    private String testSpecification;
    @ApiModelProperty(value = "验收标准")
    private String acceptanceCriteria;
    @ApiModelProperty(value = "上次检定时间")
    private String lastCheckDate;
    @ApiModelProperty(value = "灵敏度试片")
    private String sensitivityTestPiece;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "模板类型")
    private String tempType;
    @TableField(exist = false,value = "缺陷记录")
    private List<ProduceDefectsInfo> defectsInfoList;

}
