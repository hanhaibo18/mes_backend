package com.richfit.mes.produce.entity.phyChemTestVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @ClassName: AdditionalMaterialDto.java
 * @Author: renzewen
 * @Description: TODO
 * @CreateTime: 2022年10月17日 17:54:00
 */
@Data
@ApiModel("PhyChemTaskVo")
public class PhyChemTaskVo{
    @ApiModelProperty(value = "page")
    private int page;
    @ApiModelProperty(value = "limit")
    private int limit;
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "委托单号")
    private String orderNo;
    @ApiModelProperty(value = "图号")
    private String drawingNo;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "送样单位")
    private String sampleDept;
    @ApiModelProperty(value = "炉批号")
    private String batchNo;
    @ApiModelProperty(value = "branchCode")
    private String branchCode;
    @ApiModelProperty(value = "tenantId")
    private String tenantId;
    @ApiModelProperty(value = "委托单状态（0待发起、1已发起、2质检确认、3质检拒绝）")
    private String status;
    private String order;
    private String orderCol;
}
