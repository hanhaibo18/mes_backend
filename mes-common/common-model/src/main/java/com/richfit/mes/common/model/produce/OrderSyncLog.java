package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * order_sync_log
 *
 * @author Hou
 */
@Data
public class OrderSyncLog extends BaseEntity<OrderSyncLog> {
    @ApiModelProperty(value = "物料号", dataType = "String")
    private String materialNo;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @ApiModelProperty(value = "同步状态(0=未同步,1=已同步)", dataType = "String")
    private String syncState;
    @ApiModelProperty(value = "同步意见", dataType = "String")
    private String opinion;
    @ApiModelProperty(value = "租户Id", dataType = "String")
    private String tenantId;
    @ApiModelProperty(value = "车间", dataType = "String")
    private String branchCode;
}
