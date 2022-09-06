package com.richfit.mes.produce.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProbeInfo;
import com.richfit.mes.common.model.produce.ProduceDefectsInfo;
import com.richfit.mes.produce.dao.ProbeInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author renzewen
 * @Description 探头信息
 */
@Slf4j
@Service
public class ProbeInfoServiceImpl extends ServiceImpl<ProbeInfoMapper, ProbeInfo> implements ProbeInfoService {


    @Override
    public Map<String, List<ProbeInfo>> getProbeByRecordIds(List<String> ids) {
        //获取探头信息
        List<ProbeInfo> produceDefectsInfos = this.list(new QueryWrapper<ProbeInfo>().in("record_id", ids).orderByAsc("serial_num"));
        //根据探伤id分组
        return produceDefectsInfos.stream().collect(Collectors.groupingBy(ProbeInfo::getRecordId));
    }

}
