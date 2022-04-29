package com.richfit.mes.base.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmObject;

import java.util.List;


public interface PdmObjectService extends IService<PdmObject> {
    /**
     * 功能描述: 根据图号和数据分组查询工装
     * @Author: xinYu.hou
     * @Date: 2022/4/21 14:58
     * @param id 图号
     * @param dataGroup 数据粉最
     * @return: List<PdmObject>
     **/
    List<PdmObject> queryIndustrialAssembly(String id,String dataGroup);
}
