package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class DeviceRecord extends BaseEntity<DeviceRecord> {

        /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 类别
     */
    private String typeClass;
    /**
     * 源头
     */
    private String source;
    /**
     * 编号
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private int type;
    /**
     * 备注
     */
    private String remark;
    /**
     * 签收日期
     */
    private Date assignerTime;
    /**
     * 处理日期
     */
    private Date principalTime;
    /**
     * 处理人
     */
    private String principalBy;
    
     /**
     * 签收人
     */
    private String assignerBy;
    /**
     * 状态
     */
    private int status;
    
  



}
