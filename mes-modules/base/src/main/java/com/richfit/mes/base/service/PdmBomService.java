package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmBom;

import java.util.List;


public interface PdmBomService extends IService<PdmBom> {

    PdmBom getBomByProcessIdAndRev(String id, String ver);

    List<PdmBom> getBomByProcessIdAndRevTree(String id, String ver);
}
