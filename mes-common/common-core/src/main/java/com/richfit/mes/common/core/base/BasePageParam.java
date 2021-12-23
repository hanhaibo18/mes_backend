package com.richfit.mes.common.core.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @author sun
 * @Description 带分页Param基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasePageParam<P extends BaseParam> extends BaseDto {
    /**
     * 分页查询的参数，当前页面每页显示的数量
     */
    @ApiModelProperty(value = "当前页面每页显示的数量")
    private long limit = 10;
    /**
     * 分页查询的参数，当前页数
     */
    @ApiModelProperty(value = "当前页数")
    private long page = 1;

    /**
     * Form转化为Param
     *
     * @param clazz
     * @return
     */
    public P toParam(Class<P> clazz) {
        P p = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(this, p);
        return p;
    }

    /**
     * 从form中获取page参数，用于分页查询参数
     *
     * @return
     */
    public Page toPage() {
        return new Page(this.getPage(), this.getLimit());
    }
}
