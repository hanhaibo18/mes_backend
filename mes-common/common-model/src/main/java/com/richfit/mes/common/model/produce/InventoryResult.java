package com.richfit.mes.common.model.produce;

import lombok.Data;

/**
 * 实时查询通用返回类
 * @param <T>
 */
@Data
public class InventoryResult<T> {
    private String retStatus;
    private String retMsg;
    private T data;

}
