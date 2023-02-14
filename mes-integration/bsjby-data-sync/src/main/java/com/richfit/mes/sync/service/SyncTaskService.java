package com.richfit.mes.sync.service;

import com.richfit.mes.sync.config.SyncConfig;
import com.richfit.mes.sync.config.SyncTaskConfig;
import com.richfit.mes.sync.util.ConfigurationTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author gaol
 * @date 2023/2/10
 * @apiNote
 */
@Slf4j
@Service
public class SyncTaskService {

    @Autowired
    private SyncDataService syncDataService;

    @Autowired
    private SyncConfig config;
    private SyncTaskConfig syncTaskConfig;


    @PostConstruct
    public void init() throws Exception {
        try {
            syncTaskConfig = ConfigurationTools.readFileConfiguration(config.getFileName(), SyncTaskConfig.class);
            run();
        } catch (Exception e) {
            log.error("read sync configuration failed!", e);
            throw e;
        }
    }

    public void run() {
        if (syncTaskConfig == null) {
            log.error("init SyncTaskService error,syncTaskConfig is empty!");
            return;
        }
        try {
            syncDataService.runSyncDataTask(syncTaskConfig);
        } catch (Exception e) {
            log.error("run task error", e);
        }

    }

}
