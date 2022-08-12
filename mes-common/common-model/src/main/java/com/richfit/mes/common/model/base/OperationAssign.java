package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 马峰
 * @Description 派工表
 */
@Data
public class OperationAssign extends BaseEntity<OperationAssign> {

    @ApiModelProperty(value = "id", dataType = "String")
    private String id;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;
    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码", dataType = "String")
    private String branchCode;
    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String userId;
    /**
     * 派工用户名称
     */
    @ApiModelProperty(value = "派工用户名称", dataType = "String")
    private String userName;
    /**
     * 派工工位ID
     */
    @ApiModelProperty(value = "派工工位ID", dataType = "String")
    private String siteId;
    /**
     * 派工工位名称
     */
    @ApiModelProperty(value = "派工工位名称", dataType = "String")
    private String siteName;
    /**
     * 派工设备ID
     */
    @ApiModelProperty(value = "派工设备ID", dataType = "String")
    private String deviceId;
    /**
     * 派工设备名称
     */
    @ApiModelProperty(value = "派工设备名称", dataType = "String")
    private String deviceName;
    /**
     * 派工优先级  3=High、2=Medium、1=Normal、0=Low
     */
    @ApiModelProperty(value = "派工优先级  3=High、2=Medium、1=Normal、0=Low", dataType = "int")
    private int priority;
    /**
     * 派工数量
     */
    @ApiModelProperty(value = "派工数量", dataType = "int")
    private int qty;


    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String operationId;

}
