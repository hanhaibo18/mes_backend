package com.richfit.mes.produce.aop;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;
import com.richfit.mes.produce.service.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;

import static com.richfit.mes.produce.aop.LogConstant.*;

/**
 * 系统日志：切面处理类
 */
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private ActionService actionService;
    @Autowired
    private PlanService planService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private LineStoreService lineStoreService;


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
        //跟单dto
        TrackHeadPublicDto trackHeadPublicDto = null;
        //库存
        LineStore lineStore = null;
        //计划拆分实体
        PlanSplitDto planSplitDto = null;
        //合格证
        Certificate certificate = null;
        //id
        String id = null;
        //获取参数列表
        Object[] objects = joinPoint.getArgs();
        for (Object object : objects) {
            if (object != null) {
                if (object.getClass() == Order.class) {
                    order = (Order) object;
                } else if (object.getClass() == Plan.class) {
                    plan = (Plan) object;
                } else if (object.getClass() == TrackHead.class) {
                    trackHead = (TrackHead) object;
                } else if (object.getClass() == LineStore.class) {
                    lineStore = (LineStore) object;
                } else if (object.getClass() == PlanSplitDto.class) {
                    planSplitDto = (PlanSplitDto) object;
                } else if (object.getClass() == TrackHeadPublicDto.class) {
                    trackHeadPublicDto = (TrackHeadPublicDto) object;
                } else if (object.getClass() == Certificate.class) {
                    certificate = (Certificate) object;
                } else if (object.getClass() == String.class) {
                    id = (String) object;
                }
            }
        }
        //获取操作
        OperationLog myLog = method.getAnnotation(OperationLog.class);
        if (myLog != null) {
            String value = myLog.value();
            if ("saveAction".equals(value)) {
                String actionType = myLog.actionType();
                String actionItem = myLog.actionItem();
                String argType = myLog.argType();
                switch (actionItem) {
                    case "0":
                        //传入参数是Order实体，直接从实体中获取数据保存操作记录
                        if (ORDER.equals(argType) && order != null) {
                            actionService.saveAction(ActionUtil.buildAction(order.getBranchCode(), actionType, "0", "订单号：" + order.getOrderSn(), getIpAddress(request)));
                        }
                        //传入参数是TrackHead实体时，说明通过更改trackHead时更新了order
                        else if (TRACK_HEAD.equals(argType) && trackHead != null) {
                            //从跟单中获取订单号
                            if (!trackHead.getProductionOrder().isEmpty()) {
                                actionService.saveAction(ActionUtil.buildAction(trackHead.getBranchCode(), actionType, "0", "订单号：" + trackHead.getProductionOrder(), getIpAddress(request)));
                            }
                        }//传入参数是OrderId
                        else if (ORDER_ID.equals(argType) && !StringUtils.isNullOrEmpty(id)) {
                            Order orderById = orderService.getById(id);
                            if (orderById != null) {
                                actionService.saveAction(ActionUtil.buildAction(orderById.getBranchCode(), actionType, "0", "订单号：" + orderById.getOrderSn(), getIpAddress(request)));
                            }
                        }
                        break;
                    case "1":
                        //传入参数是Plan实体，直接从实体中获取数据保存操作记录
                        if (PLAN.equals(argType) && plan != null) {
                            actionService.saveAction(ActionUtil.buildAction(plan.getBranchCode(), actionType, "1", "计划号：" + plan.getProjNum() + "，图号：" + plan.getDrawNo(), getIpAddress(request)));
                        } //传入参数是PlanId，根据Id查到Plan实体然后保存操作记录
                        else if (PLAN_ID.equals(argType) && !StringUtils.isNullOrEmpty(id)) {
                            Plan planById = planService.getById(id);
                            if (planById != null) {
                                actionService.saveAction(ActionUtil.buildAction(planById.getBranchCode(), actionType, "1", "计划号：" + planById.getProjNum() + "，图号：" + planById.getDrawNo(), getIpAddress(request)));
                            }
                        }//传入参数是PLAN_SPLIT_DTO
                        else if (PLAN_SPLIT_DTO.equals(argType) && planSplitDto != null) {
                            actionService.saveAction(ActionUtil.buildAction
                                    (planSplitDto.getOldPlan().getBranchCode(), actionType, "1", "拆分计划，原计划号：" + planSplitDto.getOldPlan().getProjNum(), getIpAddress(request)));
                        }
                        break;
                    case "2":
                        //传入参数是trackHeadId，根据Id查询TrackHead实体
                        if (TRACK_HEAD_ID.equals(argType) && StringUtils.isNullOrEmpty(id)) {
                            TrackHead trackHeadById = trackHeadService.getById(id);
                            if (trackHeadById != null) {
                                actionService.saveAction(ActionUtil.buildAction
                                        (trackHeadById.getBranchCode(), actionType, "2", "取消跟单计划，跟单号：" + trackHeadById.getTrackNo(), getIpAddress(request)));
                            }
                        }//传入参数为TRACK_HEAD_PUBLIC_DTO
                        else if (TRACK_HEAD_PUBLIC_DTO.equals(argType) && trackHeadPublicDto != null) {
                            actionService.saveAction(ActionUtil.buildAction
                                    (trackHeadPublicDto.getBranchCode(), actionType, "2", "跟单号：" + trackHeadPublicDto.getTrackNo(), getIpAddress(request)));
                        }
                        break;
                    case "3":
                        //传入参数是LineStore实体，直接从实体中获取数据保存操作记录
                        if (LINE_STORE.equals(argType) && lineStore != null) {
                            actionService.saveAction(ActionUtil.buildAction(lineStore.getBranchCode(), actionType, actionItem, "物料号：" + lineStore.getMaterialNo(), getIpAddress(request)));
                        } //传入参数是合格证实体
                        else if (CERTIFICATE.equals(argType) && certificate != null) {
                            for (TrackCertificate tc : certificate.getTrackCertificates()) {
                                QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
                                queryWrapper.eq("workblank_no", tc.getProductNo());
                                queryWrapper.eq("branch_code", certificate.getNextOptWork());
                                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                                LineStore lineStoreCurr = lineStoreService.getOne(queryWrapper);
                                if (null == lineStoreCurr) {
                                    actionService.saveAction(ActionUtil.buildAction
                                            (lineStore.getBranchCode(), actionType, "3", "物料号：" + lineStore.getMaterialNo(), getIpAddress(request)));
                                }
                            }
                        }
                        break;
                }
            }
        }
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




