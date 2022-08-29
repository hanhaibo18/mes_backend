package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordUt;
import com.richfit.mes.common.model.produce.ProduceItemInspectInfo;
import com.richfit.mes.produce.dao.ProduceInspectionRecordUtMapper;
import com.richfit.mes.produce.dao.ProduceItemInspectInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/***
 * 跟单工序和探伤记录关联
 * @author renzewen
 */
@Service
@Slf4j
public class ProduceItemInspectInfoServiceImpl extends ServiceImpl<ProduceItemInspectInfoMapper, ProduceItemInspectInfo> implements ProduceItemInspectInfoService {

}
