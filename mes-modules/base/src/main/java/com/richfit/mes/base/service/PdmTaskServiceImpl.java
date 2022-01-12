package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmTaskMapper;
import com.richfit.mes.common.model.base.PdmTask;
import org.springframework.stereotype.Service;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 * 图纸审核队列
 */
@Service
public class PdmTaskServiceImpl extends ServiceImpl<PdmTaskMapper, PdmTask> implements PdmTaskService {
}
