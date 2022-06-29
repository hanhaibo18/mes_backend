package com.richfit.mes.base.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmMesObject;

import java.util.List;


public interface PdmMesObjectService extends IService<PdmMesObject> {
    /**
     * 功能描述: 根据图号和数据分组查询工装
     *
     * @param id        图号
     * @param dataGroup 数据粉最
     * @Author: xinYu.hou
     * @Date: 2022/4/21 14:58
     * @return: List<PdmObject>
     **/
    List<PdmMesObject> queryIndustrialAssembly(String id, String dataGroup);

    /**
     * 功能描述: 根据工序Id查询工装
     *
     * @param optId
     * @Author: xinYu.hou
     * @Date: 2022/6/22 6:13
     * @return: List<PdmObject>
     **/
    List<PdmMesObject> selectFixtureList(String optId);
}
