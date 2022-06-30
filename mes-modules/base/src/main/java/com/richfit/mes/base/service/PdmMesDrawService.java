package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.PdmMesDraw;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
public interface PdmMesDrawService extends IService<PdmMesDraw> {

    /**
     * 功能描述: 根据图号查询工艺图纸
     *
     * @param itemId
     * @param dataGroup
     * @Author: xinYu.hou
     * @Date: 2022/4/21 15:09
     * @return: List<PdmDraw>
     **/
    List<PdmMesDraw> queryDraw(String itemId, String dataGroup);
}
