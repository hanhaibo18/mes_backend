package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.OrderMapper;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Autowired
    private ErpServiceClient erpServiceClient;

    @Value("${time.execute:true}")
    private Boolean execute;

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
            order.setOrderDate(order.getStartTime());
            order.setDeliveryDate(order.getEndTime());
            order.setPriority("1");
            order.setStatus(0);
            order.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            //同步时数据存在空格，会导致查不到图号
            order.setMaterialCode(order.getMaterialCode().trim());
            List<Order> orders = new ArrayList<>();
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            if (order.getOrderSn() != null) {
                queryWrapper.eq("order_sn", order.getOrderSn());
                orders.addAll(orderSyncService.list(queryWrapper));
            }
            if (CollectionUtils.isNotEmpty(orders)) {
                continue;
            }
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
    @Scheduled(cron = "${time.order}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingOrderSync() {
        if (execute) {
            //拿到今天的同步数据
            OrdersSynchronizationDto ordersSynchronization = new OrdersSynchronizationDto();
            //由于零点半同步所以同步当天的数据需要取前一天的时间
            String date = DateUtil.format(DateUtil.yesterday(), "yyyy-MM-dd");
            ordersSynchronization.setDate(date);
            //获取工厂列表
            Boolean saveData = false;
            try {
                CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
                for (ItemParam itemParam : listCommonResult.getData()) {
                    CommonResult<List<ItemParam>> controllerCodeList = systemServiceClient.selectItemClass("controllerCode", "", SecurityConstants.FROM_INNER);
                    //只通过KEY取值未隔离车间,通过erpCode的租户ID进行过滤 并返回Map
                    Map<String, String> collectCode = controllerCodeList.getData().stream().filter(item -> itemParam.getTenantId().equals(item.getTenantId())).collect(Collectors.toMap(ItemParam::getLabel, ItemParam::getCode));
                    //如果过滤之后没有数据,不进行同步
                    if (CollectionUtils.isEmpty(collectCode)) {
                        continue;
                    }
                    ordersSynchronization.setCode(itemParam.getCode());
                    List<Order> orderList = erpServiceClient.getErpOrderInner(ordersSynchronization.getCode(), ordersSynchronization.getDate(), null, "", SecurityConstants.FROM_INNER).getData();
                    for (Order order : orderList) {
                        //order.setBranchCode(itemParam.getLabel());
                        order.setTenantId(itemParam.getTenantId());
                        order.setCreateBy("system");
                        order.setModifyBy("system");
                        order.setCreateTime(DateUtil.yesterday());
                        order.setModifyTime(DateUtil.yesterday());
                        order.setOrderDate(order.getStartTime());
                        order.setDeliveryDate(order.getEndTime());
                        order.setPriority("1");
                        order.setStatus(0);
                        order.setInChargeOrg(collectCode.get(order.getController()));
                        order.setBranchCode(order.getInChargeOrg());
                        if (order.getMaterialCode() == null) {
                            continue;
                        }
                        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("order_sn", order.getOrderSn());
                        List<Order> orders = orderSyncService.list(queryWrapper);
                        if (CollectionUtils.isNotEmpty(orders)) {
                            continue;
                        }
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
        return CommonResult.success(true);

    }
}
