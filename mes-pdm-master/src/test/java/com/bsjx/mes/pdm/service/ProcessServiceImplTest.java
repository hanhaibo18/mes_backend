package com.bsjx.mes.pdm.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessServiceImplTest {

    @Autowired
    private ProcessService processService;
    @Autowired
    private PdmMapService pdmMapService;
    @Test
    public void getProcessInfo() {
        //processService.clearCache();
        //processService.getProcessInfo("AD450001-11","BOMCO_JM");
    }
    @Test
    public void getBomInfo() {
        //processService.clearCache();
        //processService.getBomInfo("AH300202-00","BOMCO_BY");
    }
    @Test
    public void getDocumentURL() {
        //processService.getDocumentURL("AH080101-0101120001","01","BOMCO_JG");
    }
    @Test
    public void getPdmData() {
        //processService.clearCache();
        //processService.getPdmData();
        //processService.getPdmDataByPage(32);
        //pdmMapService.pdmMesMap("TC8SslfMadeitem.xml");
    }
    @Test
    public void getPdmDataByDrawNo() {
        //processService.clearCache();
        //processService.getPdmDataByDrawNo("AH300202-00");
        //processService.getPdmDataByDrawNo("AH080101-0213");
       // processService.getPdmDataByDrawNo("AH100101-0102B3");
    }
}