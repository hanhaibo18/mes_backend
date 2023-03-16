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
@TableName(value = "produce_inspection_records_rt")
public class ProduceInspectionRecordRt extends BaseEntity<ProduceInspectionRecordRt> {
    private static final long serialVersionUID = -1472432735506772177L;
    @ApiModelProperty(value = "记录编号")
    private String recordNo;
    @ApiModelProperty(value = "报告编号")
    private String reportNo;
    @ApiModelProperty(value = "检测部位")
    private String detectionOfParts;
    @ApiModelProperty(value = "种类(多选/分割)")
    private String type;
    @ApiModelProperty(value = "焊接方法")
    private String weldingMethod;
    @ApiModelProperty(value = "坡口形式")
    private String grooveForm;
    @ApiModelProperty(value = "检件规格")
    private String checkSpecification;
    @ApiModelProperty(value = "检测地点")
    private String testSite;
    @ApiModelProperty(value = "检测时机")
    private String testOpportunity;
    @ApiModelProperty(value = "表面状态")
    private String surfaceState;
    @ApiModelProperty(value = "仪器名称")
    private String instrumentName;
    @ApiModelProperty(value = "仪器型号")
    private String instrumentModel;
    @ApiModelProperty(value = "焦点尺寸")
    private String focusSize;
    @ApiModelProperty(value = "底片类型")
    private String filmType;
    @ApiModelProperty(value = "胶片型号")
    private String filmModel;
    @ApiModelProperty(value = "胶片规格")
    private String filmSize;
    @ApiModelProperty(value = "透照技术")
    private String transmissionTech;
    @ApiModelProperty(value = "透照厚度")
    private String detectionThickness;
    @ApiModelProperty(value = "前后屏厚度")
    private String screenThickness;
    @ApiModelProperty(value = "增感屏类型")
    private String sensitizScreen;
    @ApiModelProperty(value = "管电压")
    private String voltag;
    @ApiModelProperty(value = "管电流")
    private String current;
    @ApiModelProperty(value = "曝光时间")
    private String exposureTime;
    @ApiModelProperty(value = "焦距")
    private String focalLength;
    @ApiModelProperty(value = "散射控制")
    private String scatterControl;
    @ApiModelProperty(value = "像质计种类型号")
    private String lineMeter;
    @ApiModelProperty(value = "灵敏度")
    private String sensitivity;
    @ApiModelProperty(value = "黑度范围")
    private String densityRange;
    @ApiModelProperty(value = "工件表面至胶片距离")
    private String filmDistance;
    @ApiModelProperty(value = "射线源至工件表面距离")
    private String rayDistance;
    @ApiModelProperty(value = "洗片温度")
    private String cleanTempera;
    @ApiModelProperty(value = "洗片方式")
    private String cleanMmethod;
    @ApiModelProperty(value = "检测比例")
    private String detectionRatio;
    @ApiModelProperty(value = "显影时间（min）")
    private String developingTime;
    @ApiModelProperty(value = "定影时间（min）")
    private String fixingTime;
    @ApiModelProperty(value = "水洗时间（min）")
    private String washingTime;
    @ApiModelProperty(value = "透照方式")
    private String transmissionWay;
    @ApiModelProperty(value = "试验规范")
    private String testSpecification;
    @ApiModelProperty(value = "验收标准")
    private String acceptanceCriteria;
    @ApiModelProperty(value = "检测示意图")
    private String diagramAttachmentId;
    @ApiModelProperty(value = "示意图文字描述")
    private String pictureRemark;
    @ApiModelProperty(value = "检验员")
    private String checkBy;
    @ApiModelProperty(value = "业主")
    private String owner;
    @ApiModelProperty(value = "评片人")
    private String evaluationPerson;
    @ApiModelProperty(value = "复评人")
    private String recheckedBy;
    @ApiModelProperty(value = "审核人")
    private String auditBy;
    @ApiModelProperty(value = "见证")
    private String witnesses;
    @ApiModelProperty(value = "检测结果（0、合格   1、不合格）")
    private String inspectionResults;
    @ApiModelProperty(value = "检测结果描述")
    private String inspectionResultsRemark;
    @ApiModelProperty(value = "模板类型")
    private String tempType = "rt";
    @TableField(exist = false,value = "缺陷记录")
    private List<ProduceDefectsInfo> defectsInfoList;
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

    @TableField(exist = false)
    @ApiModelProperty(value = "检测示意图list")
    private List<String> diagramAttachmentIdList;


    public String getDiagramAttachmentId() {
        StringBuilder stringBuilder = new StringBuilder();
        if(!ObjectUtil.isEmpty(this.diagramAttachmentIdList)){
            for (String s : this.diagramAttachmentIdList) {
                if(!StringUtils.isEmpty(String.valueOf(stringBuilder))){
                    stringBuilder.append(",");
                }
                stringBuilder.append(s);
            }
            diagramAttachmentId = String.valueOf(stringBuilder);
        }
        return diagramAttachmentId;
    }

    public List<String> getDiagramAttachmentIdList() {
        if(!StringUtils.isEmpty(diagramAttachmentId) && (ObjectUtil.isEmpty(diagramAttachmentIdList) || diagramAttachmentIdList.size()==0)){
            return Arrays.asList(diagramAttachmentId.split(","));
        }
        return diagramAttachmentIdList;
    }

}
