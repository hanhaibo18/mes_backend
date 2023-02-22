package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * produce_track_head_cast
 *
 * @author Hou XinYu
 */
@Data
public class ProduceTrackHeadCast extends BaseEntity<ProduceTrackHeadCast> {

    /**
     * 钢水重量
     */
    private Double moltenSteel;

    /**
     * 铸件编号
     */
    private String castingPartsNumber;

    /**
     * 工艺保温时间
     */
    private Date processHoldingTime;

    /**
     * 浇铸温度
     */
    private String pouringTemperature;

    /**
     * 浇铸速度
     */
    private String pouringRate;

    /**
     * 毛坯调制 1=有 0=无
     */
    private boolean blankConditioning;

    /**
     * 毛坯探伤 1=有 0=无
     */
    private boolean blankInspection;

}
