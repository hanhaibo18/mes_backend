package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 派工表
 */
@Data
public class TrackAssembly extends BaseEntity<TrackAssembly> {

    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 跟单工序项ID
     */
    protected String tiId;
        /**
     * 跟单工序项ID
     */
    protected String thId;
      /**
     * 跟单工序项ID
     */
    protected String assignId;
      /**
     * 跟单工序项ID
     */
    protected String completeId;
    /**
     * 工位ID
     */
    protected String siteId;
     /**
     * 设备ID
     */
    protected String deviceId; 
         /**
     * 产品序号
     */
    protected int productOrder; 
     /**
     * 产品名称
     */
    protected String productNo;   
     /**
     * 组件号
     */
    protected String mainCompNo;
    /**
     * 组件编号
     */
    protected String subCompNo;
    /**
     * 组件图号
     */
    protected String subCompDrawNo;
    
 
     /**
     * 装配数量
     */
    protected int qty;

    /**
     * 装配时间
     */
    protected Date assemblyTime;
    
     /**
     * 装配人
     */
    protected String assemblyBy;
    
}
