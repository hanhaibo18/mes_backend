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
     *
     */
    private String optName;
    /**
     *
     */
    private String weightUp;
    /**
     *
     */
    private String weightDown;
    /**
     *
     */
    private double hour;

}
