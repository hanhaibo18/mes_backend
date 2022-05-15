package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: saveTrackHead.java
 * @Author: Hou XinYu
 * @Description: 跟单拆分
 * @CreateTime: 2022年05月16日 06:00:00
 */
@Data
public class SaveTrackHeadDto {
    @ApiModelProperty(value = "跟单号", required = true)
    private String trackHead;
    @ApiModelProperty(value = "拆分数量", required = true)
    private Integer number;
    @ApiModelProperty(value = "新跟单号", required = true)
    private String newTrackHead;
}
