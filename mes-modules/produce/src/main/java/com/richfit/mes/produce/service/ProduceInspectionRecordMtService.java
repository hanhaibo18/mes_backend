package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordMt;

import java.util.List;


/**
 * @Author: renzewen
 * @Date: 2022/8/22 10:54
 */
public interface ProduceInspectionRecordMtService extends IService<ProduceInspectionRecordMt> {

    /**
     * 根据ids获取探伤记录
     * @param ids
     * @return
     */
    List<ProduceInspectionRecordMt> queryListByIds(List<String> ids);
}
