package com.richfit.mes.sync.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gaol
 * @date 2023/2/9
 * @apiNote
 */

@Data
public class SyncTableConfig {

    @ApiModelProperty(value = "表名", dataType = "String")
    String tableName;

    @ApiModelProperty(value = "目前存储附件路径的字段", dataType = "String")
    String rawAttachmentColum;

    @ApiModelProperty(value = "转存fastDFS后附件路径的字段", dataType = "String")
    String targetColum;

    @ApiModelProperty(value = "主键字段", dataType = "String")
    String keyColum;

    @ApiModelProperty(value = "排序日期字段", dataType = "String")
    String orderDateColum;

    @ApiModelProperty(value = "每次处理的记录条数", dataType = "int")
    int batchCount;

    String tempUpdateDateColum;

}
