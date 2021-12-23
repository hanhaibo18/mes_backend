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
public class TrackComplete extends BaseEntity<TrackComplete> {

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
    protected String assignId;
    /**
     * 完工用户ID
     */
    protected String userId;
    /**
     * 完工用户ID
     */
    protected String userName;
     /**
     * 跟单ID
     */
    protected String trackId;
     /**
     * 跟单ID
     */
    protected String trackNo;
      /**
     * 产品编号
     */
    protected String prodNo;
       /**
     * 外协单号
     */
    protected String outNo;
     /**
     * 完工设备ID
     */
    protected String deviceId;
      /**
     * 完工设备ID
     */
    protected String deviceName;
    /**
     * 完工数量
     */
    protected Double completedQty;
    /**
     * 拒绝数量
     */
    protected Double rejectQty;
    
     /**
     * 完工工时
     */
    protected Double completedHours;
    /**
     * 实际工时
     */
    protected Double actualHours;
     /**
     * 报告工时
     */
    protected Double reportHours;
    /**
     * 静态工时
     */
    protected Double staticHours;
     /**
     * 报工人
     */
    protected String completeBy;

    /**
     * 报工时间
     */
    protected Date completeTime;
    
     /**
     * 探伤结果
     */
    protected String detectionResult;
    
}
