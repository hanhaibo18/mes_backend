package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * produce_track_head_cast
 *
 * @author
 */
@Data
public class TrackHeadCast extends BaseEntity<TrackHeadCast> {

    /**
     * 钢水重量
     */
    private BigDecimal moltenSteel;

    /**
     * 工艺保温时间
     */
    private Double processHoldingTime;

    /**
     * 工艺浇铸温度
     */
    private String pouringTemperature;

    /**
     * 工艺浇铸速度
     */
    private String pouringRate;

    /**
     * 工厂代码
     */
    private String branchCode;

    /**
     * 租户Id
     */
    private String tenantId;

    private static final long serialVersionUID = 1L;
}
