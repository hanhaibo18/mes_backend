package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.OrderSyncLog;
import com.richfit.mes.produce.entity.QueryOrderSyncLogPageDto;

/**
 * @ClassName: OrderSyncLogService.java
 * @Author: Hou XinYu
 * @Description: 订单同步日志
 * @CreateTime: 2022年12月01日 09:20:00
 */
public interface OrderSyncLogService extends IService<OrderSyncLog> {
    /**
     * 功能描述: 日志分页查询
     *
     * @param queryOrderSyncLogPageDto
     * @Author: xinYu.hou
     * @Date: 2022/12/1 9:39
     * @return: IPage<OrderSyncLog>
     **/
    IPage<OrderSyncLog> queryLogPage(QueryOrderSyncLogPageDto queryOrderSyncLogPageDto);
}
