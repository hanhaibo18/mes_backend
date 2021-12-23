package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/4 14:29
 */
@Data
public class PlanTrackItemViewDto {
    private String id;
    private String optId;
    private String optName;
    /**
     * 准结时间
     */
    private Double prepareEndHours;
    /**
     * 单件工时
     */
    private Double singlePieceHours;
    /**
     * 已完成数量
     */
    private Integer completeQty;
    private String planId;
    private String projCode;
}
