package com.bsjx.mes.pdm;

import com.bsjx.mes.pdm.service.ProcessService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PdmApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdmApplication.class, args);
    }

    @Bean
    CommandLineRunner lookup(ProcessService processService) {
        return args -> {
            if (args.length > 0) {
                String method = args[0];
                if("getBomInfo".equals(method)){
                    String id = args[1];
                    String dataGroup = args[2];
                    processService.clearCache();
                    processService.getBomInfo(id,dataGroup);
                    processService.clearCache();
                }
                if("getProcessInfo".equals(method)){
                    String id = args[1];
                    String dataGroup = args[2];
                    processService.getProcessInfo(id,dataGroup);
                }
                if("getDocumentURL".equals(method)){
                    String id = args[1];
                    String ver = args[2];
                    String dataGroup = args[3];
                    processService.getDocumentURL(id,ver,dataGroup);
                }
                if("getPdmDataByPage".equals(method)){
                    int page = Integer.parseInt(args[1]);
                    processService.clearCache();
                    processService.getPdmDataByPage(page);
                    processService.clearCache();
                }
                if("getPdmDataByDrawNo".equals(method)){
                    String id = args[1];
                    processService.clearCache();
                    processService.getPdmDataByDrawNo(id);
                    processService.clearCache();
                }
                if("getPdmData".equals(method)){
                    processService.clearCache();
                    processService.getPdmData();
                    processService.clearCache();
                }
            }
        };
    }
}