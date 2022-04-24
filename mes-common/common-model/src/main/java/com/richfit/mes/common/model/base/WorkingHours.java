package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class WorkingHours {

    /**
     * 图号
     */
    private String drawNo;
    /**
     * 版本号
     */
    private String version;
    /**
     * 准结工时
     */

    private String prepareEndHours;
    /**
     * 定额工时
     */

    private String singlePieceHours;

    /**
     * 总工时
     */
    private String totalProductiveHours;



}
