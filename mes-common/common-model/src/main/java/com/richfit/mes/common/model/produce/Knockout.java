package com.richfit.mes.common.model.produce;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 扣箱工序报工记录表(Knockout)表实体类
 *
 * @author makejava
 * @since 2023-05-08 10:05:00
 */
@Data
public class Knockout extends BaseEntity<Knockout> {
    //跟单工序id
    protected String itemId;
    //热风机开启时间
    protected Date fanStartTime;
}

