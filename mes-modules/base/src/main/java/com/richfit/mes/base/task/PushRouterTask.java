package com.richfit.mes.base.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.base.provider.ErpServiceClient;
import com.richfit.mes.base.service.RouterService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: mes-backend
 * @description: 定时推送工艺给ERP
 * @author: 王瑞
 * @create: 2022-08-03 10:11
 **/

@Slf4j
@Configuration
@EnableScheduling   //开启定时任务
public class PushRouterTask {

    @Autowired
    private RouterService routerService;

    @Autowired
    private ErpServiceClient erpServiceClient;

    @Value("${task-date.start_router}")
    private String taskDate;


    //添加定时任务
    //@Scheduled(cron = "${time.push_router}")
    private void execTask() {
        log.info("执行推送工艺给ERP定时任务时间: " + LocalDateTime.now());
        // 查出没有推送给ERP的工艺
        QueryWrapper<Router> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("r.status", 1);
        queryWrapper.eq("r.is_active", 1);
        queryWrapper.apply("(r.is_send_erp is null or r.is_send_erp != 1)");
        queryWrapper.ge("r.create_time", taskDate);
        queryWrapper.groupBy("p.drawing_no");
        List<Router> routers = routerService.getList(queryWrapper);
        CommonResult<Boolean> result = erpServiceClient.pushRouter(routers, SecurityConstants.FROM_INNER);
        if (result != null && result.getData()) {
            log.info("推送工艺给ERP成功, 共计{}条", routers.size());
        } else {
            log.info("推送工艺给ERP失败，{}", routers.stream().map(Router::getRouterNo).collect(Collectors.toList()));
        }
    }

}
