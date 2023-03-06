package com.richfit.mes.produce.aop;


import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.produce.service.ActionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;

/**
 * 系统日志：切面处理类
 */
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private ActionService actionService;


    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation(OperationLog)")
    public void logPoinCut() {
    }

    //切面 配置通知
    @AfterReturning("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        //获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //订单
        Order order = null;
        //计划
        Plan plan = null;
        //跟单
        TrackHead trackHead = null;
        //库存
        LineStore lineStore = null;
        //获取参数列表
        Object[] objects = joinPoint.getArgs();
        for (Object object : objects) {
            if (object.getClass() == Order.class) {
                order = (Order) object;
            } else if (object.getClass() == Plan.class) {
                plan = (Plan) object;
            } else if (object.getClass() == TrackHead.class) {
                trackHead = (TrackHead) object;
            } else if (object.getClass() == LineStore.class) {
                lineStore = (LineStore) object;
            }
        }
        System.out.println("-------------------1");
        actionService.saveAction(ActionUtil.buildAction("", "", "", "订单号：" + "", getIpAddress(request)));
        //获取操作
        OperationLog myLog = method.getAnnotation(OperationLog.class);
//        if (myLog != null) {
//            String value = myLog.value();
//            if ("saveAction".equals(value)) {
//                String actionType = myLog.actionType();
//                String actionItem = myLog.actionItem();
//                if (order != null) {
//                    saveAction.saveAction(ActionUtil.buildAction
//                            (order.getBranchCode(), actionType, actionItem, "订单号：" + order.getOrderSn(), getIpAddress(request)));
//                } else if (plan != null) {
//                    saveAction.saveAction(ActionUtil.buildAction
//                            (plan.getBranchCode(), actionType, actionItem, "计划号：" + plan.getProjNum() + "，图号：" + plan.getDrawNo(), getIpAddress(request)));
//                } else if (trackHead != null) {
//                    saveAction.saveAction(ActionUtil.buildAction
//                            (trackHead.getBranchCode(), actionType, actionItem, "跟单号：" + trackHead.getTrackNo(), getIpAddress(request)));
//                } else if (lineStore != null) {
//                    saveAction.saveAction(ActionUtil.buildAction
//                            (lineStore.getBranchCode(), actionType, actionItem, "物料号：" + lineStore.getMaterialNo(), getIpAddress(request)));
//                }
//            }
//        }
        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //获取请求的方法名
        String methodName = method.getName();
    }


    /**
     * 获取IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                }
                if (inet.getHostAddress() != null) {
                    ipAddress = inet.getHostAddress();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}




