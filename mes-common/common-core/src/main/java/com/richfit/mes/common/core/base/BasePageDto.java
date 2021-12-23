package com.richfit.mes.common.core.base;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 10:26
 */
@Data
public class BasePageDto<T> {
    private int limit = 10;
    private int page = 1;
    private T param;
}
