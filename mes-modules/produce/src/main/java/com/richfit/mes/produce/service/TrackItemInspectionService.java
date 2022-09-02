package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackItemInspection;


/**
 * @author renzewen
 * @Description 质检跟单工序
 */
public interface TrackItemInspectionService extends IService<TrackItemInspection> {

    /**
     * 功能描述: 复制跟单工序数据
     *
     * @param trackItemId
     * @Author: xinYu.hou
     * @Date: 2022/9/1 16:57
     * @return: boolean
     **/
    boolean saveItem(String trackItemId);
}
