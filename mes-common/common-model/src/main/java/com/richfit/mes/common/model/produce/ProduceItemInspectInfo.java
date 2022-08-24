package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@TableName(value = "produce_item_inspect_info")
public class ProduceItemInspectInfo{
    @ApiModelProperty(value = "跟单工序id")
    private String trackItemId;
    @ApiModelProperty(value = "探伤记录id")
    private String inspectRecordId;
    @ApiModelProperty(value = "探伤记录模板类型（1、mt 2、pt 3、rt 4、ut）")
    private String tempType;
}
