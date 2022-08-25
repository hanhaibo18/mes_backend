package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordUt;

import java.util.List;


/**
 * @Author: renzewen
 * @Date: 2022/8/22 10:54
 */
public interface ProduceInspectionRecordUtService extends IService<ProduceInspectionRecordUt> {


    /**
     * 根据ids获取记录
     * @return
     */
    List<ProduceInspectionRecordUt> queryListByIds(List<String> ids);
}
