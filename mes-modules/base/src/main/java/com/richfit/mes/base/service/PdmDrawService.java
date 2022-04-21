package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmDraw;

import java.util.List;


public interface PdmDrawService extends IService<PdmDraw> {

    /**
     * 功能描述: 根据图号查询工艺图纸
     * @Author: xinYu.hou
     * @Date: 2022/4/21 15:09
     * @param itemId
     * @param dataGroup
     * @return: List<PdmDraw>
     **/
    List<PdmDraw> queryDraw(String itemId,String dataGroup);
}
