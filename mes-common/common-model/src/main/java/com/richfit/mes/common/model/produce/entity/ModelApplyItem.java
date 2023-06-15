package com.richfit.mes.common.model.produce.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 工序 模型请求表(ModelApplyItem)表实体类
 *
 * @author makejava
 * @since 2023-06-14 16:02:05
 */
@Data
public class ModelApplyItem extends BaseEntity<ModelApplyItem> {

    //工序id
    private String itemId;
    //模型请求id
    private String applyId;

}

