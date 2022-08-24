package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceDefectsInfo;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordRt;
import com.richfit.mes.produce.dao.ProduceInspectionRecordRtMapper;
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
public class ProduceInspectionRecordRtServiceImpl extends ServiceImpl<ProduceInspectionRecordRtMapper, ProduceInspectionRecordRt> implements ProduceInspectionRecordRtService {


    @Autowired
    private ProduceDefectsInfoService produceDefectsInfoService;

    /**
     * 根据ids获取记录
     * @return
     */
    @Override
    public List<ProduceInspectionRecordRt> queryListByIds(List<String> ids){
        List<ProduceInspectionRecordRt> produceInspectionRecordPts = this.list(new QueryWrapper<ProduceInspectionRecordRt>().in("id", ids));
        //探伤记录map
        Map<String, ProduceInspectionRecordRt> recordMap = produceInspectionRecordPts.stream().collect(Collectors.toMap(ProduceInspectionRecordRt::getId, Function.identity()));
        //缺陷记录 key->探伤记录id
        Map<String, List<ProduceDefectsInfo>> defectsInfoMap = produceDefectsInfoService.getMapByRecordIds(ids);

        recordMap.forEach((key,value)->{
            value.setDefectsInfoList(defectsInfoMap.get(key));
        });
        return new ArrayList<>(recordMap.values());
    }
}
