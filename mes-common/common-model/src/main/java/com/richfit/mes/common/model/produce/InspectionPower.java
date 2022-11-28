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
    @ApiModelProperty(value = "派工人", dataType = "String")
    private String assignBy;
    @TableField(exist = false)
    private String productType;

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
