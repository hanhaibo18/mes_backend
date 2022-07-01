package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmMesProcess;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
public interface PdmMesProcessService extends IService<PdmMesProcess> {

    IPage<PdmMesProcess> queryPageList(int page, int limit, PdmMesProcess pdmProcess);

    List<PdmMesProcess> queryList(PdmMesProcess pdmProcess);

    void release(PdmMesProcess pdmMesProcess) throws Exception;
}
