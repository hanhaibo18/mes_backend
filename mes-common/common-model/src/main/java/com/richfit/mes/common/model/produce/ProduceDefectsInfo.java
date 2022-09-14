package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@TableName(value = "produce_defects_info")
public class ProduceDefectsInfo extends BaseEntity<ProduceDefectsInfo> {
    @ApiModelProperty(value = "探伤记录id")
    private String recordId;
    @ApiModelProperty(value = "探伤记录模板类型（mt pt rt ut）")
    private String type;
    @ApiModelProperty(value = "序号")
    private String serialNumber;
    @ApiModelProperty(value = "零件号及部位号")
    private String partNo;
    @ApiModelProperty(value = "底片号")
    private String filmNo;
    @ApiModelProperty(value = "缺陷性质尺寸")
    private String defectsNature;
    @ApiModelProperty(value = "等级")
    private String grade;
    @ApiModelProperty(value = "评定")
    private String evaluation;
}
