package com.richfit.mes.produce.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.TrackCertificateService;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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
        System.err.println("执行定时任务时间: " + LocalDateTime.now());
        // 查出没有推送给ERP的合格证
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("is_send_work_hour", 1);
        queryWrapper.ge("create_time", taskDate);
        List<Certificate> list = certificateService.list(queryWrapper);
        list.forEach(c -> {certAdditionalBsns.pushWorkHour(c);});
    }

}
