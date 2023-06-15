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


    private static final long serialVersionUID = 1L;

    /**
     * 跟单id
     */
    private String headId;
    /**
     * 钢水重量
     */
    private BigDecimal weightMolten;

    /**
     * 工艺保温时间
     */
    private Double processHoldTime;
    /**
     * 试棒型号
     */
    private String testBar;

    /**
     * 工艺浇铸温度
     */
    private String pourTemp;
    /**
     * 浇筑时间
     */
    private String pourTime;
    /**
     * 毛坯调质
     */
    private String blankControl;
    /**
     * 毛坯探伤
     */
    private String blankInspect;

    /**
     * 工厂代码
     */
    private String branchCode;

    /**
     * 租户Id
     */
    private String tenantId;

}
