package com.richfit.mes.common.model.produce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author HanHaiBo
 * @date 2023/2/27 9:26
 */
@Data
public class MessageDto {
    @ApiModelProperty(value = "消息模板类型：目前只有一个固定值 1", dataType = "Integer")
    private Integer mblx;
    @ApiModelProperty(value = "接收人的openid(openid可以通过接口使用工号换算)", dataType = "String")
    private String openid;
    @ApiModelProperty(value = "消息的主题，例：{{first.DATA}}", dataType = "String")
    private String title;
    @ApiModelProperty(value = "消息内容，例：{{keyword1.DATA}}|{{keyword2.DATA}}|{{keyword3.DATA}}", dataType = "String")
    private String content;
    @ApiModelProperty(value = "备注内容{{remark.DATA}}", dataType = "String")
    private String baks;
}
