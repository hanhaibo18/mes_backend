package com.richfit.mes.common.model.produce;

import lombok.Data;

/**
 * @Author: GaoLiang  看板进度跟踪  机加 装配
 * @Date: 2020/10/16 10:13
 */
@Data
public class ProcessTrack {

    private String drawNo;
    private String drawNoName;
    private int yearPlan;
    private int yearComp;
    private int processNum;
    private int monthPlan;
    private int monthComp;

}
