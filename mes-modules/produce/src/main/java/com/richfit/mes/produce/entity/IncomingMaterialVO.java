package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @ClassName: IncomingMaterialVO.java
 * @Author: Hou XinYu
 * @Description: 来料入库合格证相关查询
 * @CreateTime: 2022年04月25日 09:32:00
 */
@Data
public class IncomingMaterialVO {
    /**
     * id
     **/
    private String id;
    /**
     * 合格证编号
     **/
    private String materialCertificateNo;
    /**
     * 产品编号
     **/
    private String productNO;
    /**
     * 来料单位
     * 暂无
     **/

    /**
     * 工作号
     **/
    private String workNo;
    /**
     * 产品名称
     **/
    private String productName;
    /**
     * 零件名称
     * 暂无
     **/

    /**
     * 炉号
     **/
    private String batchNo;
    /**
     * 图号
     **/
    private String drawingNo;
    /**
     * 订单号
     **/
    private String productOrder;
    /**
     * 材料
     **/
    private String materialName;
    /**
     * 代用材料
     **/
    private String replaceMaterial;
    /**
     * 试棒类型
     **/
    private String testBarType;
    /**
     * 试棒数量
     **/
    private String testBarNumber;
    /**
     * 重量
     **/
    private Double weight;
    /**
     * 数量
     **/
    private String number;
    /**
     * 批次数量
     * 暂无
     **/
    /**
     * 本工序
     **/
    private String operation;
    /**
     * 下工序
     **/
    private String nextOpt;
    /**
     * branchCode 车间
     **/
    private String branchCode;
    /**
     * 备注
     **/
    private String remark;
    /**
     * 推送人
     * 暂无
     **/
    /**
     * 推送时间
     * 暂无
     **/















}
