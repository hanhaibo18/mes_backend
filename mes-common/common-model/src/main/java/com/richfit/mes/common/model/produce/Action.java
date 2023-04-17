package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Action extends BaseEntity<Action> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 组织结构ID
     */
    private String branchId;

    /**
     * 操作时间
     */
    private Date actionTime;

    /**
     * 操作人
     */
    private String user;

    /**
     * 类名
     **/
    private String className;

    /**
     * 方法名
     **/
    private String methodName;


    /**
     * 操作类型 0新增 1修改 2删除 3撤回 4开工 5报工 6审核 7派工
     */
    private String actionType;

    /**
     * 操作对象 0订单 1计划 2跟单 3质检 4库存
     */
    private String actionItem;
    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 类型
     **/
    private String type;

}
