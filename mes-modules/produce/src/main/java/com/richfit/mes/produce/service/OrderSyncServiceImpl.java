package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.OrderMapper;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: OrderSyncServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年01月19日 16:18:00
 */
@Slf4j
@Service
public class OrderSyncServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderSyncService {

    @Resource
    private OrderSyncService orderSyncService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Value("${interface.erp.orders-synchronization}")
    private String url;

    @Autowired
    private ErpServiceClient erpServiceClient;

    @Override
    public List<Order> queryOrderSynchronization(OrdersSynchronizationDto orderSynchronizationDto) {
        return erpServiceClient.getErpOrder(orderSynchronizationDto.getCode(), orderSynchronizationDto.getDate(), orderSynchronizationDto.getOrderSn(), orderSynchronizationDto.getController()).getData();
    }

    /**
     * 功能描述: 保存同步信息
     *
     * @param orderList
     * @Author: xinYu.hou
     * @Date: 2022年1月18日14:19:44
     * @return: CommonResult<Boolean>
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveOrderSync(List<Order> orderList) {
        for (Order order : orderList) {
            if (order.getMaterialCode() == null) {
                continue;
            }
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            if (order.getOrderSn() != null) {
                queryWrapper.eq("order_sn", order.getOrderSn());
            }
            order.setOrderDate(order.getStartTime());
            order.setDeliveryDate(order.getEndTime());
            order.setPriority("1");
            order.setStatus(0);
            order.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            orderSyncService.remove(queryWrapper);
            orderSyncService.save(order);
        }
        return CommonResult.success(true, "操作成功!");
    }

    /**
     * 功能描述: 定时保存同步信息
     *
     * @Author: xinYu.hou
     * @Date: 2022年1月18日14:19:44
     * @return: CommonResult<Boolean>
     **/
    @Override
//    @Scheduled(cron = "0 30 23 * * ? ")
    //"*/10 * * * * ? "
//    @Scheduled(cron = "${time.order}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingOrderSync() {
        //拿到今天的同步数据
        OrdersSynchronizationDto ordersSynchronization = new OrdersSynchronizationDto();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        ordersSynchronization.setDate(format.format(date));
        //获取工厂列表
        Boolean saveData = false;
        try {
            CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            for (ItemParam itemParam : listCommonResult.getData()) {
                ordersSynchronization.setCode(itemParam.getCode());
                List<Order> orderList = orderSyncService.queryOrderSynchronization(ordersSynchronization);
                for (Order order : orderList) {
                    //order.setBranchCode(itemParam.getLabel());
                    order.setTenantId(itemParam.getTenantId());
                    order.setCreateBy("system");
                    order.setModifyBy("system");
                    order.setCreateTime(date);
                    order.setModifyTime(date);
                    order.setOrderDate(order.getStartTime());
                    order.setDeliveryDate(order.getEndTime());
                    order.setPriority("1");
                    order.setStatus(0);
                    order.setInChargeOrg(user.getBelongOrgId());
                    if (order.getMaterialCode() == null) {
                        continue;
                    }
                    QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_sn", order.getOrderSn());
                    orderSyncService.remove(queryWrapper);
                    saveData = orderSyncService.save(order);
                }
            }
        } catch (Exception e) {
            saveData = false;
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return CommonResult.success(saveData);
    }
}
