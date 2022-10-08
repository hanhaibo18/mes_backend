package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import org.springframework.util.StringUtils;

/**
 * @author renzewen
 * @Description 理化检验委托单
 */
public interface PhysChemOrderService extends IService<PhysChemOrder> {

    IPage<PhysChemOrder> selectOrderList(int page, int size, String startTime, String endTime, String batchNo);
}
