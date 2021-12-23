package com.richfit.mes.common.core.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

import java.util.Date;

/**
 * @author sun
 * @Description Param基类
 */
@Data
public class BaseParam<T extends BaseEntity> {
    private Date createTimeStart;
    private Date createTimeEnd;

    public QueryWrapper<T> build() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(null != this.createTimeStart, "create_time", this.createTimeStart)
                .le(null != this.createTimeEnd, "create_time", this.createTimeEnd);
        return queryWrapper;
    }
}
