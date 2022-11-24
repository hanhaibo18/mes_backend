package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "探伤委托单管理")
public class InspectionPower extends BaseEntity<InspectionPower> {

    @ApiModelProperty(value = "委托单状态", dataType = "Integer")
    private int status;

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


}
