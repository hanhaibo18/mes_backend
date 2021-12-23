package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 工序与设备关联
 */
@Data
public class OperationDevice extends BaseEntity<OperationDevice> {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 设备编码
     */
    private String operationId;
    /**
     * 设备ID
     */
    private String deviceId;

}
