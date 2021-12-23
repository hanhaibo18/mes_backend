package com.richfit.mes.common.core.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sun
 * @Description  EntityVo基类
 */
@Data
@NoArgsConstructor
public class BaseVo<T extends BaseEntity> implements Serializable {
    private String id;
}
