package com.richfit.mes.produce.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.OrderSyncLog;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.OrderSyncLogMapper;
import com.richfit.mes.produce.entity.QueryOrderSyncLogPageDto;
import org.springframework.stereotype.Service;

/**
 * @ClassName: OrderSyncLogServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 订单同步日志
 * @CreateTime: 2022年12月01日 09:21:00
 */
@Service
public class OrderSyncLogServiceImpl extends ServiceImpl<OrderSyncLogMapper, OrderSyncLog> implements OrderSyncLogService {

    @Override
    public IPage<OrderSyncLog> queryLogPage(QueryOrderSyncLogPageDto queryOrderSyncLogPageDto) {
        QueryWrapper<OrderSyncLog> queryWrapper = new QueryWrapper<>();
        //物料号
        queryWrapper.eq(StrUtil.isNotBlank(queryOrderSyncLogPageDto.getMaterialNo()), "material_no", queryOrderSyncLogPageDto.getMaterialNo());
        //图号
        queryWrapper.eq(StrUtil.isNotBlank(queryOrderSyncLogPageDto.getDrawingNo()), "drawing_no", queryOrderSyncLogPageDto.getDrawingNo());
        //状态
        queryWrapper.eq(StrUtil.isNotBlank(queryOrderSyncLogPageDto.getSyncState()), "sync_state", queryOrderSyncLogPageDto.getSyncState());
        //开始时间
        queryWrapper.ge(StrUtil.isNotBlank(queryOrderSyncLogPageDto.getStartTime()), "modify_time", queryOrderSyncLogPageDto.getStartTime());
        //结束时间
        queryWrapper.ge(StrUtil.isNotBlank(queryOrderSyncLogPageDto.getEndTime()), "modify_time", queryOrderSyncLogPageDto.getEndTime());
        //根据租户查询
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return this.page(new Page<>(queryOrderSyncLogPageDto.getPage(), queryOrderSyncLogPageDto.getLimit()), queryWrapper);
    }
}
