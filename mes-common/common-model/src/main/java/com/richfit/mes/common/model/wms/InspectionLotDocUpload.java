package com.richfit.mes.common.model.wms;


import java.util.List;

/**
 * @author LLh
 * @Description WMS报检单上传MES
 */
public class InspectionLotDocUpload {
    /**
     * 报检单ID  唯一
     */
    private String id;

    /**
     * 报检单号
     */
    private String inspectionLotDocNo;

    /**
     * 业务类型  外购产品/外协产品i
     */
    private Integer serviceType;

    /**
     * 工厂
     */
    private String factory;

    /**
     * 库存地点
     */
    private String stockPlace;

    /**
     * 采购订单
     */
    private String PurchaseOrder;

    /**
     * 行项目
     */
    private String lineItem;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 到货日期
     */
    private String arrivalDate;

    /**
     * 物料编码
     */
    private String matterCode;

    /**
     * 物料描述
     */
    private String matterDescribe;

    /**
     * 计量单位
     */
    private String unitMeasurement;

    /**
     * 报检数量
     */
    private String inspectionNumber;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 工作号
     */
    private String jobNo;

    /**
     * 炉号/批次号
     */
    private String batchNo;

    /**
     * 外协工序
     */
    private String externalProcess;

    /**
     * 下工序
     */
    private String downProcess;

    /**
     * 验收准则
     */
    private String acceptanceCriteria;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 报检单创建人
     */
    private String creater;

    /**
     * 通知单创建日期
     */
    private String createDate;

    /**
     * 产品编号明细列表
     */
    private List lineList;


}
