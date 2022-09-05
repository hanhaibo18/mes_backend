package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceRoleOperation;
import com.richfit.mes.produce.dao.ProduceRoleOperationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: zhiqiang.lu
 * @Date: 2020.9.2 9:54
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ProduceRoleOperationServiceImpl extends ServiceImpl<ProduceRoleOperationMapper, ProduceRoleOperation> implements ProduceRoleOperationService {

}
