package com.richfit.mes.base.entity;

import lombok.Data;


/**
 * @author wcy
 * @date 2023/6/5 10:07
 */

@Data
public class ConnectExtendDTO {

    /**
     * 交接单id
     */
    private String connectId;

    /**
     * 零部件图号
     */
    private String partDrawingNo;

    /**
     * 零部件名称
     */
    private String partName;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 需求数量
     */
    private Integer demandNumber;

    /**
     * 累计送货数量
     */
    private Integer sendNumber;

    /**
     * 累计接受数量
     */
    private Integer receiveNumber;

    /**
     * 本次送货数量
     */
    private Integer number;

    /**
     * 本次送货数量
     */
    private Integer alreadyNumber;

    /**
     * 单位
     */
    private String unit;

    /**
     * 重量
     **/
    private String weight;

    /**
     * 备注
     */
    private String extend;

}
