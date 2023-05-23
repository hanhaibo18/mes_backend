package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.OrderSyncLog;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.OrderMapper;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OrderSyncServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 订单同步
 * @CreateTime: 2022年01月19日 16:18:00
 */
@Slf4j
@Service
@PropertySource("classpath:application.yml")
@Component
public class OrderSyncServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderSyncService {

    @Resource
    private OrderSyncService orderSyncService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private OrderSyncLogService orderLogService;


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
    public CommonResult<Boolean> saveOrderSync(List<Order> orderList, String time, String controller, String erpCode, String branchCode) {
        for (Order order : orderList) {
            if (order.getMaterialCode() == null) {
                continue;
            }
            order.setBranchCode(branchCode);
            order.setOrderDate(order.getStartTime());
            order.setDeliveryDate(order.getEndTime());
            order.setPriority("1");
            order.setStatus(0);
            order.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            //同步时数据存在空格，会导致查不到图号
            order.setMaterialCode(order.getMaterialCode().trim());
            //进行校验
            if (Boolean.TRUE.equals(filterOrder(order, time, controller, erpCode))) {
                orderSyncService.save(order);
            }
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
    @Scheduled(cron = "0 0/10 * * * ? ")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingOrderSync() {
        if (execute) {
            //拿到今天的同步数据
            OrdersSynchronizationDto ordersSynchronization = new OrdersSynchronizationDto();
            //由于零点半同步所以同步当天的数据需要取前一天的时间
//            String date = DateUtil.format(DateUtil.yesterday(), "yyyy-MM-dd");
            ordersSynchronization.setDate(DateUtil.today());
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
                        order.setCreateTime(DateUtil.yesterday());
                        order.setModifyTime(DateUtil.yesterday());
                        order.setOrderDate(order.getStartTime());
                        order.setDeliveryDate(order.getEndTime());
                        order.setPriority("1");
                        order.setStatus(0);
                        order.setInChargeOrg(itemParam.getCode());
                        order.setBranchCode(collectCode.get(order.getController()));
                        if (order.getMaterialCode() == null) {
                            continue;
                        }
                        //进行校验
                        if (Boolean.TRUE.equals(filterOrder(order, DateUtil.today(), null, ordersSynchronization.getCode()))) {
                            saveData = orderSyncService.save(order);
                        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveOrderSyncOne(String id) {
        //先查询日志,获取查询参数
        OrderSyncLog orderSyncLog = orderLogService.getById(id);
        //查询erp
        List<Order> orderList = erpServiceClient.getErpOrder(orderSyncLog.getErpCode(), orderSyncLog.getOrderSyncTime(), orderSyncLog.getOrderSn(), orderSyncLog.getController()).getData();
        //调用同步接口
        return this.saveOrderSync(orderList, orderSyncLog.getOrderSyncTime(), orderSyncLog.getController(), orderSyncLog.getErpCode(), orderSyncLog.getBranchCode());
    }

    private String getBranchCode(String userName) {
        if (null == SecurityUtils.getCurrentUser()) {
            CommonResult<TenantUserVo> userAccountInner = systemServiceClient.queryByUserAccountInner(userName, SecurityConstants.FROM_INNER);
            List<Branch> branchList = baseServiceClient.queryAllBranchInner(SecurityConstants.FROM_INNER);
            for (Branch branch : branchList) {
                if (userAccountInner.getData().getBelongOrgId().replaceAll("_", "").startsWith(branch.getBranchCode().replaceAll("_", ""))) {
                    return branch.getBranchCode();
                }
            }
        } else {
            CommonResult<TenantUserVo> userAccount = systemServiceClient.queryByUserAccount(userName);
            List<Branch> branchList = baseServiceClient.queryAllBranch();
            for (Branch branch : branchList) {
                if (userAccount.getData().getBelongOrgId().replaceAll("_", "").startsWith(branch.getBranchCode().replaceAll("_", ""))) {
                    return branch.getBranchCode();
                }
            }
        }
        return null;
    }

    /**
     * 功能描述: 过滤订单
     *
     * @param order
     * @Author: xinYu.hou
     * @Date: 2022/12/1 10:10
     * @return: List<Order>
     **/
    @Transactional(rollbackFor = Exception.class)
    public Boolean filterOrder(Order order, String time, String controller, String erpCode) {
        //组装log数据
        OrderSyncLog log = new OrderSyncLog();
        log.setMaterialNo(order.getMaterialCode());
        log.setErpCode(erpCode);
        if (order.getMaterialDesc().contains(" ")) {
            List<String> list = Arrays.stream(order.getMaterialDesc().split("\\s+")).collect(Collectors.toList());
            log.setDrawingNo(list.get(0));
            log.setProductName(list.get(1));
        } else {
            log.setProductName(order.getMaterialDesc());
        }
        log.setOrderSn(order.getOrderSn());
        log.setSyncState("0");
        log.setOrderSyncTime(time);
        log.setController(controller);
        log.setBranchCode(order.getBranchCode());
        log.setTenantId(order.getTenantId());
        List<Product> list = new ArrayList<>();
        //判断当前是否存在登录人信息
        if (null == SecurityUtils.getCurrentUser()) {
            list.addAll(baseServiceClient.selectOrderProductInner(order.getMaterialCode(), "", SecurityConstants.FROM_INNER));
        } else {
            list.addAll(baseServiceClient.selectOrderProduct(order.getMaterialCode(), ""));
        }
        if (CollectionUtils.isEmpty(list)) {
            log.setOpinion("未查询到成品物料信息,请补全成品物料");
            orderLogService.save(log);
            return false;
        } else if (StrUtil.isBlank(list.get(0).getDrawingNo())) {
            log.setOpinion("未查询到成品物料图号,请补充物料图号信息");
            orderLogService.save(log);
            return false;
        }
        //图号为空,或图号不相等进入
        if (null == log.getDrawingNo() || !DrawingNoUtil.drawingNo(list.get(0).getDrawingNo()).equals(DrawingNoUtil.drawingNo(log.getDrawingNo()))) {
            if (null != log.getDrawingNo()) {
                log.setOpinion("同步图号为:" + log.getDrawingNo() + ",本地物料图号为:" + list.get(0).getDrawingNo() + ",图号不同请处理");
            } else {
                log.setOpinion("同步图号为:null,本地物料图号为:" + list.get(0).getDrawingNo() + ",图号不同请处理");
            }
            orderLogService.save(log);
            return false;
        }
        order.setDrawingNo(list.get(0).getDrawingNo());
        order.setMaterialType(list.get(0).getMaterialType());
        //根据订单号进行查询,如果存在不进行同步
        if (order.getOrderSn() != null) {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_sn", order.getOrderSn());
            List<Order> orders = new ArrayList<>(orderSyncService.list(queryWrapper));
            if (CollectionUtils.isNotEmpty(orders)) {
                //订单号已存在
                log.setSyncState("1");
                log.setOpinion("订单已存在不进行同步");
                orderLogService.save(log);
                return false;
            }
        }
        //泵业额外增加判断
        if (order.getTenantId().equals("12345678901234567890123456789002")) {
            String branchCode = getBranchCode(order.getCreateBy());
            if (StrUtil.isBlank(branchCode)) {
                order.setBranchCode("BOMCO_BF_BY");
            } else {
                order.setBranchCode(branchCode);
            }
            //通过判断同步状态为1
            log.setSyncState("1");
            log.setOpinion("同步成功");
            orderLogService.save(log);
            return true;
        }
    }
