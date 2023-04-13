package com.bsjx.mes.pdm.service;

import com.bsjx.mes.pdm.entity.PdmBom;

import java.util.List;

public interface ProcessService {
    void clearCache();
    void executeMonitorTask();
    void getPdmData() throws Exception;
    void getPdmDataByPage(int page);
    void getPdmDataByDrawNo(String drawNo);
    boolean getProcessInfo(String id, String dataGroup);
    List<PdmBom> getBomInfo(String id, String dataGroup);
    boolean getDocumentURL(String id, String revId, String dataGroup);
}
