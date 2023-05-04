package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;

/**
 * 原材料表(ProduceRawMaterial)表实体类
 *
 * @author makejava
 * @since 2023-04-27 14:13:28
 */
@SuppressWarnings("serial")
public class RawMaterial extends BaseEntity<RawMaterial> {

    //租户ID
    private String tenantId;
    //车间编码
    private String branchCode;
    //原材料名称
    private String rawMaterialName;
    //剩余数量
    private Integer num;
    //单位
    private String unit;

    }

