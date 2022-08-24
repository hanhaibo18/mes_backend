package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceDefectsInfo;

import java.util.List;
import java.util.Map;


/**
 * @Author: renzewen
 * @Date: 2022/8/22 10:54
 */
public interface ProduceDefectsInfoService extends IService<ProduceDefectsInfo> {

    /**
     * 根据探伤记录ids获取缺陷map(key->探伤记录id)
     * @param ids
     * @return
     */
    Map<String, List<ProduceDefectsInfo>> getMapByRecordIds(List<String> ids);
}
