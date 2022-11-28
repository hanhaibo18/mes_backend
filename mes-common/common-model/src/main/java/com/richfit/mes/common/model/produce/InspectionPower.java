package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;


@Data
@ApiModel(value = "探伤委托单管理")
public class InspectionPower extends BaseEntity<InspectionPower> {

    @ApiModelProperty(value = "委托单状态", dataType = "Integer")
    private int status;

    @ApiModelProperty(value = "委托时间", dataType = "String")
    private String powerTime;

    @ApiModelProperty(value = "跟单id", dataType = "String")
    private String headId;
    @ApiModelProperty(value = "跟单工序id", dataType = "String")
    private String itemId;

    @ApiModelProperty(value = "委托单号", dataType = "String")
    private String orderNo;

    @ApiModelProperty(value = "钻机号", dataType = "String")
    private String drilNo;

    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawNo;

    @ApiModelProperty(value = "样品名称", dataType = "String")
    private String sampleName;

    @ApiModelProperty(value = "探伤站", dataType = "String")
    private String inspectionDepart;

    @ApiModelProperty(value = "无损检测类型", dataType = "String")
    private String tempType;
    @ApiModelProperty(value = "探伤类型", dataType = "String")
    private String checkType;
    @ApiModelProperty(value = "焊接", dataType = "Integer")
    private int weld;
    @ApiModelProperty(value = "铸造", dataType = "Integer")
    private int cast;
    @ApiModelProperty(value = "锻压", dataType = "Integer")
    private int forg;
    @ApiModelProperty(value = "荧光", dataType = "Integer")
    private int fluorescent;
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private int num;
    @ApiModelProperty(value = "单重", dataType = "double")
    private double single;
    @ApiModelProperty(value = "长度", dataType = "double")
    private double length;
    @ApiModelProperty(value = "处数", dataType = "Integer")
    private int reviseNum;
    @ApiModelProperty(value = "branchCode", dataType = "String")
    private String branchCode;
    @ApiModelProperty(value = "tenantId", dataType = "String")
    private String tenantId;
    @ApiModelProperty(value = "委托人", dataType = "String")
    private String consignor;
    @ApiModelProperty(value = "指派人", dataType = "String")
    private String assignBy;
    @ApiModelProperty(value = "指派时间", dataType = "String")
    private String assignTime;
    @ApiModelProperty(value = "指派状态（0、未派  1、已派）")
    private int assignStatus;

    @ApiModelProperty(value = "探伤结果", required = true)
    private Integer flawDetection;
    @ApiModelProperty(value = "探伤报告号", required = true)
    private String reportNo;
    @ApiModelProperty(value = "探伤备注", required = true)
    private String flawDetectionRemark;
    @ApiModelProperty(value = "探伤报告文件Id", required = true)
    private String flawDetectionPaper;
    @ApiModelProperty(value = "探伤记录编码")
    private String inspectRecordNo;
    @ApiModelProperty(value = "审核通过的探伤记录模板")
    private String inspTempType;
    @ApiModelProperty(value = "探伤检验人")
    private String checkBy;
    @ApiModelProperty(value = "探伤审核人")
    private String auditBy;
    @TableField(exist = false)
    private String productType;
    @TableField(exist = false)
    private String assignName;
    @TableField(exist = false)
    private String trackNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称")
    private String projectName;
    @TableField(exist = false)
    @ApiModelProperty(value = "工作号")
    private String workNo;

    public String getProductType() {
        StringBuilder productType = new StringBuilder();
        if(this.weld ==1){
            productType.append("焊接");
        }
        if(this.cast ==1){
            if(!StringUtils.isEmpty(String.valueOf(productType))){
                productType.append(" ");
            }
            productType.append("铸造");
        }
        if(this.forg ==1){
            if(!StringUtils.isEmpty(String.valueOf(productType))){
                productType.append(" ");
            }
            productType.append("锻压");
        }
        if(this.fluorescent ==1){
            if(!StringUtils.isEmpty(String.valueOf(productType))){
                productType.append(" ");
            }
            productType.append("荧光");
        }
        return String.valueOf(productType);
    }
}
