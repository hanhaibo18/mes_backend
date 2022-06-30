package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmMesBom;


public interface PdmMesBomService extends IService<PdmMesBom> {

    PdmMesBom getBomByProcessIdAndRev(String id, String ver);
}
