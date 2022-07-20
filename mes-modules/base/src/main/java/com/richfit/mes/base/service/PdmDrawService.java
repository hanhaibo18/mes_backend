package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmDraw;

import java.util.List;


public interface PdmDrawService extends IService<PdmDraw> {

    /**
     * 功能描述: 根据图号查询工艺图纸
     *
     * @param itemId
     * @param dataGroup
     * @Author: xinYu.hou
     * @Date: 2022/4/21 15:09
     * @return: List<PdmDraw>
     **/
    List<PdmDraw> queryDraw(String itemId, String dataGroup);

    /**
     * 功能描述: 查询图纸列表
     *
     * @param itemId
     * @Author: xinYu.hou
     * @Date: 2022/7/18 17:24
     * @return: List<PdmDraw>
     **/
    List<PdmDraw> queryDrawList(String itemId);
}
