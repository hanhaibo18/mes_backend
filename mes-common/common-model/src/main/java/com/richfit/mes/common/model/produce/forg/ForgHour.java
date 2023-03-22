package com.richfit.mes.common.model.produce.forg;

import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.produce.CodeRuleValue;
import lombok.Data;

import java.util.Date;

/**
 * @author renzewen
 * @Description 锻造工时标准
 */
@Data
public class ForgHour extends BaseEntity<ForgHour> {

    /**
     *工序id
     */
    private String optId;
    /**
     *工序名称
     */
    private String optName;
    /**
     *重量上限
     */
    private String weightUp;
    /**
     *重量下限
     */
    private String weightDown;
    /**
     * 材质
     */
    private String texture;

    /**
     *工时
     */
    private double hour;

}
