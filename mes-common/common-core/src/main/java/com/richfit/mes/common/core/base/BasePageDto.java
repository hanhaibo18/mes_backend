package com.richfit.mes.common.core.base;

import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 10:26
 */
@Data
public class BasePageDto<T> {
    private int limit = 10;
    private int page = 1;
    private String order;
    private String orderCol;
    private T param;
}
