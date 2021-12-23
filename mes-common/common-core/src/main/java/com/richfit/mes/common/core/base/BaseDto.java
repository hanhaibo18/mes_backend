package com.richfit.mes.common.core.base;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author sun
 * @Description Dto基类
 */
@ApiModel
@Data
public class BaseDto<T extends BaseEntity>{
    /**
     * Dto转化为Po，进行后续业务处理
     *
     * @param clazz
     * @return
     */
    public T toPo(Class<T> clazz) {
        T t = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(this, t);
        return t;
    }

    /**
     * Dto转化为Po，进行后续业务处理
     *
     * @param id
     * @param clazz
     * @return
     */
    public T toPo(String id, Class<T> clazz) {
        T t = BeanUtils.instantiateClass(clazz);
        t.setId(id);
        BeanUtils.copyProperties(this, t);
        return t;
    }
}
