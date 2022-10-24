package com.richfit.mes.base.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DrawingApplyExcelEntity {
    /**
     * 图号
     */
    private String drawingNo;

    /**
     * PDM图号
     */
    private String pdmDrawingNo;


    /**
     * 描述
     */
    private String drawingDesc;

}
