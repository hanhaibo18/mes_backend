package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表实体类
 *
 * @author makejava
 * @since 2023-03-23 14:13:03
 */
@Data
@TableName("produce_normalize_dehydro_record")
public class NormalizeDehydroRecord extends BaseEntity<NormalizeDehydroRecord> {

    //跟单工序id
    private String itemId;
    //产品名称
    private String productName;
    //图    号
    private String drawNo;
    //编号
    private String serialNo;
    //数量
    private Integer number;
    //(0、正火     1、去氢)
    private String type;

}

