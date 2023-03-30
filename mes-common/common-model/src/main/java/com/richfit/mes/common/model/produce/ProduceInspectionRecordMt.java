package com.richfit.mes.common.model.produce;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;


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
    @TableField(exist = false)
    @ApiModelProperty(value = "仪器型号")
    private List<String> instrumentModelList;
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
    @TableField(exist = false)
    @ApiModelProperty(value = "磁化方向")
    private List<String> magneticDirectionList;
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
    @TableField(exist = false)
    @ApiModelProperty(value = "磁粉施加方法")
    private List<String> magneticPowderMethodList;
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
    @TableField(exist = false)
    @ApiModelProperty(value = "检测示意图list")
    private List<String> diagramAttachmentIdList;
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
    @ApiModelProperty(value = "检测结果（0、合格   1、不合格）")
    private String inspectionResults;
    @ApiModelProperty(value = "检测结果描述")
    private String inspectionResultsRemark;
    @ApiModelProperty(value = "模板类型")
    private String tempType = "mt";
    @ApiModelProperty(value = "是否审核")
    private String isAudit;
    @ApiModelProperty(value = "审核意见")
    private String auditRemark;
    @ApiModelProperty(value = "无源委托的项目名称")
    private String projectName;
    @ApiModelProperty(value = "无源委托的材质")
    private String texture;
    @ApiModelProperty(value = "无源委托的产品名称")
    private String productName;
    @ApiModelProperty(value = "无源委托的产品编码")
    private String productNo;



    public List<String> getInstrumentModelList() {
        if(!StringUtils.isEmpty(instrumentModel) && (ObjectUtil.isEmpty(instrumentModelList) || instrumentModelList.size()==0)){
            return Arrays.asList(instrumentModel.split(","));
        }
        return instrumentModelList;
    }

    public String getInstrumentModel() {
        StringBuilder stringBuilder = new StringBuilder();
        if(instrumentModelList instanceof List){
            for (String s : this.instrumentModelList) {
                if(!StringUtils.isEmpty(String.valueOf(stringBuilder))){
                    stringBuilder.append(",");
                }
                stringBuilder.append(s);
            }
            return String.valueOf(stringBuilder);
        }else{
            return StringUtils.isEmpty(instrumentModel)?"":instrumentModel;
        }
    }

    public String getMagneticPowderMethod() {
        StringBuilder stringBuilder = new StringBuilder();
        if(magneticPowderMethodList instanceof List){
            for (String s : this.magneticPowderMethodList) {
                if(!StringUtils.isEmpty(String.valueOf(stringBuilder))){
                    stringBuilder.append(",");
                }
                stringBuilder.append(s);
            }
            magneticPowderMethod = String.valueOf(stringBuilder);
        }
        return StringUtils.isEmpty(magneticPowderMethod)?"":magneticPowderMethod;
    }

    public List<String> getMagneticPowderMethodList() {
        if(!StringUtils.isEmpty(magneticPowderMethod) && (ObjectUtil.isEmpty(magneticPowderMethodList) || magneticPowderMethodList.size()==0)){
            return Arrays.asList(magneticPowderMethod.split(","));
        }
        return magneticPowderMethodList;
    }

    public String getMagneticDirection() {
        StringBuilder stringBuilder = new StringBuilder();
        if(magneticDirectionList instanceof List){
            for (String s : this.magneticDirectionList) {
                if(!StringUtils.isEmpty(String.valueOf(stringBuilder))){
                    stringBuilder.append(",");
                }
                stringBuilder.append(s);
            }
            magneticDirection = String.valueOf(stringBuilder);
        }
        return StringUtils.isEmpty(magneticDirection)?"":magneticDirection;
    }

    public List<String> getMagneticDirectionList() {
        if(!StringUtils.isEmpty(magneticDirection) && (ObjectUtil.isEmpty(magneticDirectionList) || magneticDirectionList.size()==0)){
            return Arrays.asList(magneticDirection.split(","));
        }
        return magneticDirectionList;
    }

    public String getDiagramAttachmentId() {
        StringBuilder stringBuilder = new StringBuilder();
        if(diagramAttachmentIdList instanceof List){
            for (String s : this.diagramAttachmentIdList) {
                if(!StringUtils.isEmpty(String.valueOf(stringBuilder))){
                    stringBuilder.append(",");
                }
                stringBuilder.append(s);
            }
            diagramAttachmentId = String.valueOf(stringBuilder);
        }
        return StringUtils.isEmpty(diagramAttachmentId)?"":diagramAttachmentId;
    }

    public List<String> getDiagramAttachmentIdList() {
        if(!StringUtils.isEmpty(diagramAttachmentId) && (ObjectUtil.isEmpty(diagramAttachmentIdList) || diagramAttachmentIdList.size()==0)){
            return Arrays.asList(diagramAttachmentId.split(","));
        }
        return diagramAttachmentIdList;
    }
}
