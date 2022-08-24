package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordPt;

import java.util.List;


/**
 * @Author: renzewen
 * @Date: 2022/8/22 10:54
 */
public interface ProduceInspectionRecordPtService extends IService<ProduceInspectionRecordPt> {

    /**
     * 根据ids获取记录
     * @return
     */
    List<ProduceInspectionRecordPt> queryListByIds(List<String> ids);

    /**
     * 根据记录编号查询探伤记录信息
     */
    ProduceInspectionRecordPt queryRecordByRecordNo(String recordNo);

}
