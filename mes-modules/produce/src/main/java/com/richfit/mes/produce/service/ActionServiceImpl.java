package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ActionMapper;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 王瑞
 * @Description 操作信息服务
 */
@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action> implements ActionService {

    @Autowired
    private ActionMapper actionMapper;
    @Resource
    private TrackHeadService trackHeadService;
    @Resource
    private OrderService orderService;
    @Resource
    private PlanService planService;
    @Resource
    private LineStoreService lineStoreService;

    @Override
    public Boolean saveAction(Action action) {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        action.setUser(user.getUsername());
        action.setTenantId(user.getTenantId());
        action.setActionTime(new Date());
        return actionMapper.insert(action) > 0 ? true : false;
    }

    @Override
    public Boolean saveHeadActionEntity(TrackHeadPublicDto trackHead, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("跟单", actionItem, actionType, className, methodName, ipAddress);
        assemble.setRemark("跟单号: " + trackHead.getTrackNo());
        return this.save(assemble);
    }

    @Override
    public Boolean saveHeadActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("跟单", actionItem, actionType, className, methodName, ipAddress);
        TrackHead trackHead = trackHeadService.getById(id);
        assemble.setRemark("跟单号: " + trackHead.getTrackNo());
        return this.save(assemble);
    }

    @Override
    public Boolean saveOrderActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("订单", actionItem, actionType, className, methodName, ipAddress);
        Order order = orderService.getById(id);
        assemble.setRemark("订单号: " + order.getOrderSn());
        return this.save(assemble);
    }

    @Override
    public Boolean saveOrderActionEntity(Order order, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("订单", actionItem, actionType, className, methodName, ipAddress);
        assemble.setRemark("订单号: " + order.getOrderSn());
        return this.save(assemble);
    }

    @Override
    public Boolean savePlanActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("计划", actionItem, actionType, className, methodName, ipAddress);
        Plan plan = planService.getById(id);
        assemble.setRemark("计划号: " + plan.getProjNum() + ", 图号: " + plan.getDrawNo());
        return this.save(assemble);
    }

    @Override
    public Boolean savePlanActionEntity(Plan plan, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("计划", actionItem, actionType, className, methodName, ipAddress);
        assemble.setRemark("计划号: " + plan.getProjNum() + ", 图号: " + plan.getDrawNo());
        return this.save(assemble);
    }

    @Override
    public Boolean saveLineStoreActionById(String id, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("库存", actionItem, actionType, className, methodName, ipAddress);
        LineStore lineStore = lineStoreService.getById(id);
        assemble.setRemark("物料号: " + lineStore.getMaterialNo());
        return this.save(assemble);
    }

    @Override
    public Boolean saveLineStoreActionEntity(LineStore lineStore, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action assemble = assemble("库存", actionItem, actionType, className, methodName, ipAddress);
        assemble.setRemark("物料号: " + lineStore.getMaterialNo());
        return this.save(assemble);
    }

    private Action assemble(String business, String actionItem, String actionType, String className, String methodName, String ipAddress) {
        Action action = new Action();
        action.setActionType(actionType);
        action.setType(business);
        action.setActionTime(new Date());
        action.setActionItem(actionItem);
        action.setIpAddress(ipAddress);
        action.setClassName(className);
        action.setMethodName(methodName);
        return action;
    }
}
