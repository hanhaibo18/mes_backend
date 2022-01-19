package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmLog;


public interface PdmLogService extends IService<PdmLog> {

    IPage<PdmLog> queryPageList(
            int page,
            int limit,
            String type,
            String par,
            String queryTimeStart,
            String queryTimeEnd);
}
