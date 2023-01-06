package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.dto.MaterialReceiveDto;

import java.util.List;

/**
 * @className:MaterialReceiveService
 * @description: 类描述
 * @author:ang
 * @date:2022/7/29 17:54
 */
public interface MaterialReceiveService extends IService<MaterialReceive> {

    String getlastTime(String tenantId);

    Page<MaterialReceive> getPage(Page<MaterialReceive> materialReceivePage, QueryWrapper<MaterialReceive> queryWrapper);

    Boolean saveMaterialReceiveList(List<MaterialReceive> materialReceiveList);

    CommonResult materialReceiveSaveBatchList(MaterialReceiveDto material);
}
