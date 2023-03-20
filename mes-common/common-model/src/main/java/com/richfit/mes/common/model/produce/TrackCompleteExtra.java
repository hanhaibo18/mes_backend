package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 报工额外信息表
 *
 * @author 任泽文
 * @since 2023-3-20
 */
@Data
public class TrackCompleteExtra extends BaseEntity<TrackCompleteExtra> {

    @ApiModelProperty(value = "工序id", dataType = "String")
    protected String itemId;
    @ApiModelProperty(value = "下料长度(下料)", dataType = "String")
    protected String blankLength;
    @ApiModelProperty(value = "完成数量(下料)", dataType = "String")
    public String completeNumber;
    @ApiModelProperty(value = "操作人(下料)", dataType = "String")
    public String operator;
    @ApiModelProperty(value = "操作时间(下料)", dataType = "String")
    public  int operatTime;
    @ApiModelProperty(value = "收料人(下料)", dataType = "String")
    public int receiver;
    @ApiModelProperty(value = "操作时间(下料)", dataType = "String")
    public int receiveTime;
    @ApiModelProperty(value = "设备编号", dataType = "String")
    public String deviceCode;
    @ApiModelProperty(value = "火次", dataType = "String")
    public String fireOrder;
    @ApiModelProperty(value = "工步内容", dataType = "String")
    public String stepContent;
    @ApiModelProperty(value = "段始温度", dataType = "String")
    public String initialForgTemp;
    @ApiModelProperty(value = "段终温度", dataType = "String")
    public String finalForgTemp;
    @ApiModelProperty(value = "记录人", dataType = "String")
    public String recorder;
    @ApiModelProperty(value = "日期", dataType = "String")
    public String recordTime;
    @ApiModelProperty(value = "司炉工", dataType = "String")
    public String stoker;
    @ApiModelProperty(value = "正火、退火记录号", dataType = "String")
    public String recordNo;
}
