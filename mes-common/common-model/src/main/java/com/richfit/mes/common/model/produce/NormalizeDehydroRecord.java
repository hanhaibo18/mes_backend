package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表实体类
 *
 * @author makejava
 * @since 2023-03-23 14:13:03
 */
@Data
@TableName("produce_normalize_dehydro_record")
public class NormalizeDehydroRecord extends BaseEntity<NormalizeDehydroRecord> {

    @ApiModelProperty(value = "跟单工序id", dataType = "String")
    private String itemId;
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @ApiModelProperty(value = "图    号", dataType = "String")
    private String drawNo;
    @ApiModelProperty(value = "记录编号", dataType = "String")
    private String serialNo;
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer number;
    @ApiModelProperty(value = "0 正火  1 去氢", dataType = "Integer")
    private Integer type;

    @ApiModelProperty(value = "设备编号", dataType = "String")
    private String equipmentNo;
    @ApiModelProperty(value = "审核状态 0 未通过  1 通过", dataType = "Integer")
    private Integer auditStatus;
    @ApiModelProperty(value = "记录人", dataType = "String")
    private String recordBy;
    @ApiModelProperty(value = "班次", dataType = "String")
    private String workTime;
}

