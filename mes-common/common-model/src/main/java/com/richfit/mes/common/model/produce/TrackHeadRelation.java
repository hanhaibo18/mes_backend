package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单库存关联表
 */
@Data
public class TrackHeadRelation  {

    /**
     * 跟单ID
     */
    private String thId;

    /**
     * 库存ID
     */
    private String lsId;

    /**
     * 类型 0输入物料 1输出物料
     */
    private String type;

    /**
     * 数量
     */
    private Integer number;

}
