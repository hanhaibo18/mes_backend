package com.bsjx.mes.pdm.task;

import com.bsjx.mes.pdm.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableAsync
public class PdmSchedulerTask {

    @Value("${pdm.webservice.cron.enable:true}")
    private String batchScheduledEnable;
    @Value("${pdm.webservice.monitor.enable:false}")
    private String monitorScheduledEnable;
    @Value("${pdm.webservice.cron}")
    private String pdmWebServiceCron;

    @Autowired
    private ProcessService processService;

    private volatile boolean clearCacheLock = false;

    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    //默认每天23:15触发
    @Async
    @PostConstruct
    public void batch(){
        threadPoolTaskScheduler.schedule(()-> {
            if(!Boolean.parseBoolean(batchScheduledEnable)){
                return;
            }
            clearCacheLock = true;
            processService.clearCache();
            log.info("开始执行批量处理任务......");
            try {
                processService.getPdmData();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }finally {
                processService.clearCache();
                clearCacheLock = false;
            }
        },new CronTrigger(pdmWebServiceCron));
    }


    @Scheduled(fixedDelayString = "${pdm.webservice.monitor.fixedDelay:5000}")
    public void monitorScheduled(){
        if(!Boolean.parseBoolean(monitorScheduledEnable)){
            return;
        }
        if(!clearCacheLock){
            processService.clearCache();
            log.info("开始执行监控任务......");
            processService.executeMonitorTask();
            processService.clearCache();
        }else {
            log.info("与批量任务同时执行，开始执行监控任务......");
            processService.executeMonitorTask();
        }
    }
}
