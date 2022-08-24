package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceDefectsInfo;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordMt;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordPt;
import com.richfit.mes.produce.dao.ProduceInspectionRecordPtMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 * 探伤记录
 * @author renzewen
 */
@Service
@Slf4j
public class ProduceInspectionRecordPtServiceImpl extends ServiceImpl<ProduceInspectionRecordPtMapper, ProduceInspectionRecordPt> implements ProduceInspectionRecordPtService {

    @Autowired
    private ProduceDefectsInfoService produceDefectsInfoService;

    /**
     * 根据ids获取记录
     * @return
     */
    @Override
    public List<ProduceInspectionRecordPt> queryListByIds(List<String> ids){
        List<ProduceInspectionRecordPt> produceInspectionRecordPts = this.list(new QueryWrapper<ProduceInspectionRecordPt>().in("id", ids));
        //探伤记录map
        Map<String, ProduceInspectionRecordPt> recordMap = produceInspectionRecordPts.stream().collect(Collectors.toMap(ProduceInspectionRecordPt::getId, Function.identity()));
        //缺陷记录 key->探伤记录id
        Map<String, List<ProduceDefectsInfo>> defectsInfoMap = produceDefectsInfoService.getMapByRecordIds(ids);

        recordMap.forEach((key,value)->{
            value.setDefectsInfoList(defectsInfoMap.get(key));
        });
        return new ArrayList<>(recordMap.values());
    }

    /**
     * 根据记录编码查询记录
     * @param recordNo
     * @return
     */
    @Override
    public ProduceInspectionRecordPt queryRecordByRecordNo(String recordNo) {

        return null;
    }
}
