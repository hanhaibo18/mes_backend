package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 设备人员关联
 */
@Data
public class DevicePerson extends BaseEntity<DevicePerson> {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 是否默认
     */
    private int isDefault;
    /**
     * 是否默认
     */
    private int orderNo;
    
}
