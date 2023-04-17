package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;

/**
 * @author 王瑞
 * @Description 跟单工序
 */
public interface ActionService extends IService<Action> {

    Boolean saveAction(Action action);

    /**
     * 功能描述: 跟单实体日志
     *
     * @param trackHead
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:25
     * @return: Boolean
     **/
    Boolean saveHeadActionEntity(TrackHeadPublicDto trackHead, String actionItem, String actionType, String className, String methodName, String ipAddress);

    /**
     * 功能描述:跟单ID日志
     *
     * @param id
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:25
     * @return: Boolean
     **/
    Boolean saveHeadActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress);

    /**
     * 功能描述: 订单日志
     *
     * @param id
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:25
     * @return: Boolean
     **/
    Boolean saveOrderActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress);

    /**
     * 功能描述:订单Id日志
     *
     * @param order
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:25
     * @return: Boolean
     **/
    Boolean saveOrderActionEntity(Order order, String actionItem, String actionType, String className, String methodName, String ipAddress);

    /**
     * 功能描述:计划Id
     *
     * @param id
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:26
     * @return: Boolean
     **/
    Boolean savePlanActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress);

    /**
     * 功能描述:计划日志
     *
     * @param plan
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:26
     * @return: Boolean
     **/
    Boolean savePlanActionEntity(Plan plan, String actionItem, String actionType, String className, String methodName, String ipAddress);

    /**
     * 功能描述:仓储日志
     *
     * @param id
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:27
     * @return: Boolean
     **/
    Boolean saveLineStoreActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress);

    /**
     * 功能描述:仓储Id
     *
     * @param lineStore
     * @param actionItem
     * @param actionType
     * @param className
     * @param methodName
     * @param ipAddress
     * @Author: xinYu.hou
     * @Date: 2023/4/7 16:27
     * @return: Boolean
     **/
    Boolean saveLineStoreActionEntity(LineStore lineStore, String actionItem, String actionType, String className, String methodName, String ipAddress);
}
