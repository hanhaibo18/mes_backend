package com.bsjx.mes.pdm.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest
public class PdmReqListRepositoryTest {
    @Autowired
    protected DrawingApplyRepository pdmReqListRepository;
    @Autowired
    protected PdmOptionRepository pdmOptionRepository;
    @Autowired
    protected PdmObjectRepository pdmObjectRepository;
    @Autowired
    protected PdmStepRepository pdmStepRepository;
    @Test
    public void testInsert() {
//        PdmReqList reqList = new PdmReqList();
//        reqList.setDrawNo("AH100101-0100");
//        reqList.setVer("01");
//        reqList.setAudit("1");
//        reqList.setDataGroup("BOMCO_JG");
//        pdmReqListRepository.save(reqList);
    }

}
