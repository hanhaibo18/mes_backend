package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceDefectsInfo;
import com.richfit.mes.produce.dao.ProduceDefectsInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * 探伤记录
 * @author renzewen
 */
@Service
@Slf4j
public class ProduceDefectsInfoServiceImpl extends ServiceImpl<ProduceDefectsInfoMapper, ProduceDefectsInfo> implements ProduceDefectsInfoService {


    @Override
    public Map<String, List<ProduceDefectsInfo>> getMapByRecordIds(List<String> ids) {
        //获取缺陷记录
        List<ProduceDefectsInfo> produceDefectsInfos = this.list(new QueryWrapper<ProduceDefectsInfo>().in("record_id", ids).orderByAsc("serial_number"));
        //根据探伤id分组
        return produceDefectsInfos.stream().collect(Collectors.groupingBy(ProduceDefectsInfo::getRecordId));
    }
}
