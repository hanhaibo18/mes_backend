package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmProcess;

import java.util.List;

public interface PdmProcessService extends IService<PdmProcess> {

    IPage<PdmProcess> queryPageList(int page, int limit, PdmProcess pdmProcess);

    List<PdmProcess> queryList(PdmProcess pdmProcess);

    void synctomes(PdmProcess pdmProcess) throws Exception;
}
