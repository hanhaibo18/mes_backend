package com.richfit.mes.produce.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: mes-backend
 * @description: 定时推送工时给ERP
 * @author: 王瑞
 * @create: 2022-08-01 15:11
 **/

@Configuration
@EnableScheduling   //开启定时任务
@Slf4j
public class PushWorkHourTask {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertAdditionalBsns certAdditionalBsns;

    @Value("${task-date.start_work_hour}")
    private String taskDate;

    //添加定时任务
    //@Scheduled(cron = "${time.push_work_hour}")
    private void execTask() {
        log.debug("执行定时任务时间: " + LocalDateTime.now());

        //TODO 如果是装配车间，切开启了定时任务
        //查询完工 或 已生成完工资料，且未生成合格证的跟单  =》 先生成合格证，再推送


        // 查出没有推送给ERP的合格证
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("is_send_work_hour", 1);
        queryWrapper.ge("create_time", taskDate);
        List<Certificate> list = certificateService.list(queryWrapper);
        list.forEach(c -> {
            try {
                certAdditionalBsns.pushWorkHour(c);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
