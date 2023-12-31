package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 锻造工序控制记录表(ProduceForgControlRecord)表实体类
 *
 * @author makejava
 * @since 2023-03-23 13:57:42
 */
@Data
@TableName(value = "produce_forg_control_record")
public class ForgControlRecord extends BaseEntity<ForgControlRecord> {

    //火次
    private String fireOrder;
    //工步内容
    private String stepContent;
    //段始温度（℃）
    private String initialForgTemp;
    //段终温度（℃）
    private String finalForgTemp;
    //记录人
    private String recorder;
    //记录日期
    private Date recordeTime;
    //工序id
    private String itemId;
    //数据类型 1-火次，2-试棒锻造，3-备注
    private String type;
    //试棒锻造
    private String barForge;

}

