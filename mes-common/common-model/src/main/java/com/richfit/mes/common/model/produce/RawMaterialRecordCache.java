package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 原材料消耗记录缓存表(RawMaterialRecordCache)表实体类
 *
 * @author makejava
 * @since 2023-04-27 14:10:13
 */
@Data
public class RawMaterialRecordCache extends BaseEntity<RawMaterialRecordCache> {

    //租户ID
    private String tenantId;
    //车间编码
    private String branchCode;
    //工序id
    private String itemId;
    //原材料名称
    private String rawMaterialName;
    //原材料id
    private String rawMaterialId;
    //消耗数量
    private Integer usedNum;
    //单位
    private String unit;

    }

