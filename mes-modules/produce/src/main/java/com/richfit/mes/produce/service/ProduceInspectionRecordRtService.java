package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordPt;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordRt;

import java.util.List;


/**
 * @Author: renzewen
 * @Date: 2022/8/22 10:54
 */
public interface ProduceInspectionRecordRtService extends IService<ProduceInspectionRecordRt> {

    /**
     * 根据ids获取记录
     * @return
     */
    List<ProduceInspectionRecordRt> queryListByIds(List<String> ids);

}
