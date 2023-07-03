package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * (RecordsOfPourOperations)实体类
 *
 * @author makejava
 * @since 2023-05-12 15:39:33
 */
@Data
public class RecordsOfPourOperations extends BaseEntity<RecordsOfPourOperations> {

    /**
     * 预装炉id
     */
    @ApiModelProperty(value = "预装炉id", dataType = "Long")
    private Long prechargeFurnaceId;
    /**
     * 作业记录编号
     */
    @ApiModelProperty(value = "作业记录编号", dataType = "String")
    private String recordNo;
    /**
     * 审核状态0未通过1通过
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "审核状态0未通过1通过", dataType = "Integer")
    private Integer status;
    /**
     * 浇注时间
     */
    @ApiModelProperty(value = "浇注时间", dataType = "Date")
    private String pourTime;
    /**
     * 烘包时间H
     */
    @ApiModelProperty(value = "烘包时间H", dataType = "Float")
    private Float dryingTime;
    /**
     * 镇静时间H
     */
    @ApiModelProperty(value = "镇静时间H", dataType = "Float")
    private Float calmTime;
    /**
     * 温度
     */
    @ApiModelProperty(value = "温度", dataType = "Float")
    private Float temperature;
    /**
     * 湿度
     */
    @ApiModelProperty(value = "湿度", dataType = "Float")
    private Float humidity;
    /**
     * 炉号
     */
    @ApiModelProperty(value = "炉号", dataType = "String")
    private String furnaceNo;
    /**
     * 浇注箱数
     */
    @ApiModelProperty(value = "浇注箱数", dataType = "Integer")
    private Integer pourNum;
    @TableField(exist = false)
    List<TrackItem> itemList;
    /**
     * 操作者
     */
    @ApiModelProperty(value = "操作者", dataType = "String")
    private String operator;
    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间", dataType = "String")
    private String operatorTime;
    /**
     * 审核员
     */
    @ApiModelProperty(value = "审核员", dataType = "String")
    private String assessor;
    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间", dataType = "String")
    private String assessorTime;
    @ApiModelProperty(value = "钢种", dataType = "String")
    private String typeOfSteel;
    @ApiModelProperty(value = "锭型", dataType = "String")
    private String ingotCase;
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;
    @ApiModelProperty(value = "冶炼班组", dataType = "String")
    private String classGroup;
    @TableField(exist = false)
    private String texture;

}

