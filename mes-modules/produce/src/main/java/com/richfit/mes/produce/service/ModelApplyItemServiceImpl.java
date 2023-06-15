package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.entity.ModelApplyItem;
import com.richfit.mes.produce.dao.ModelApplyItemMapper;
import org.springframework.stereotype.Service;

/**
 * 工序 模型请求表(ModelApplyItem)表服务实现类
 *
 * @author makejava
 * @since 2023-06-14 16:04:24
 */
@Service
public class ModelApplyItemServiceImpl extends ServiceImpl<ModelApplyItemMapper, ModelApplyItem> implements ModelApplyItemService {

}

