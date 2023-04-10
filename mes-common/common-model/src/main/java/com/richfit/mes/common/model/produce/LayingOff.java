package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 下料表(ProduceLayingOff)表实体类
 *
 * @author makejava
 * @since 2023-03-23 14:21:06
 */
@Data
@TableName(value = "produce_laying_off")
public class LayingOff extends BaseEntity<LayingOff> {

    //跟单工序id
    private String itemId;
    //规格
    private String specification;
    //数量
    private String num;
    //炉号
    private String batchNo;
    //操作者
    private String operator;
    //操作时间
    private String operateTime;
    //材质
    private String texture;
    //重量
    private String weight;

}

