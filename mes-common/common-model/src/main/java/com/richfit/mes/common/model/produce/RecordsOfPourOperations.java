package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
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


}

