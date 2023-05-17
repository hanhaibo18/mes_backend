package com.richfit.mes.common.model.produce;

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
    private Long prechargeFurnaceId;
    /**
     * 作业记录编号
     */
    private String recordNo;
    /**
     * 审核状态0未通过1通过
     */
    private Integer status;
    /**
     * 浇注时间
     */
    private Date pourTime;
    /**
     * 烘包时间H
     */
    private Float dryingTime;
    /**
     * 镇静时间H
     */
    private Float calmTime;
    /**
     * 温度
     */
    private Float temperature;
    /**
     * 湿度
     */
    private Float humidity;
    /**
     * 炉号
     */
    private String furnaceNo;
    /**
     * 浇注箱数
     */
    private Integer pourNum;
    @TableField(exist = false)
    List<TrackItem> itemList;
    /**
     * 操作者
     */
    private String operator;
    /**
     * 操作时间
     */
    private Date operatorTime;
    /**
     * 审核员
     */
    private String assessor;
    /**
     * 审核时间
     */
    private Date assessorTime;
    @ApiModelProperty(value = "钢种", dataType = "String")
    private String typeOfSteel;
    @ApiModelProperty(value = "锭型", dataType = "String")
    private String ingotCase;


}

