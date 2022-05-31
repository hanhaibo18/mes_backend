package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 王瑞
 * @Date: 2020/8/11
 */
@Slf4j
@Service
@Transactional
public class LineStoreServiceImpl extends ServiceImpl<LineStoreMapper, LineStore> implements LineStoreService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    LineStoreMapper lineStoreMapper;

    @Override
    public IPage<LineStoreSum> selectGroup(Page<LineStore> page, QueryWrapper<LineStore> query) {
        return lineStoreMapper.selectGroup(page, query);
    }

    @Override
    public IPage<LineStore> selectLineStoreByProduce(Page<LineStore> page, QueryWrapper<LineStore> query) {
        return lineStoreMapper.selectLineStoreByProduce(page, query);
    }

    @Override
    public boolean changeStatus(TrackHead trackHead) {

        String pNo = trackHead.getUserProductNo(); //毛坯编号

        /*UpdateWrapper<LineStore> update = new UpdateWrapper<LineStore>();
        UpdateWrapper<LineStore> update2 = new UpdateWrapper<LineStore>();
        update.eq("workblank_no", pNo);
        update.eq("drawing_no", trackHead.getDrawingNo());
        update.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        update.apply("number = user_num");
        update.set("status", "3"); //将状态设置为已消耗
        update.set("out_time", new Date());
        update2.set("status", "1"); //将状态设置为完工
        update2.set("in_time", new Date());
        update2.eq("workblank_no", trackHead.getProductNo());
        update2.eq("drawing_no", trackHead.getDrawingNo());
        update2.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        int count = lineStoreMapper.update(null , update);
        if(count > 0){
            count = lineStoreMapper.update(null , update2);
            if(count > 0){
                return true;
            }
        }*/
        UpdateWrapper<LineStore> update2 = new UpdateWrapper<LineStore>();
        update2.set("status", "1"); //将状态设置为完工
        update2.set("in_time", new Date());
        update2.eq("workblank_no", trackHead.getProductNo());
        update2.eq("drawing_no", trackHead.getDrawingNo());
        update2.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        int count = lineStoreMapper.update(null, update2);
        if (count > 0) {
            return true;
        }
        return false;
    }

    // 材料入库
    @Override
    public boolean addStore(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo, Boolean isAutoMatchProd, Boolean isAutoMatchPur, String branchCode) {

        lineStore.setUserNum(0);
        lineStore.setStatus("1");
        lineStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        lineStore.setBranchCode(branchCode);
        lineStore.setCreateTime(new Date());
        lineStore.setInTime(new Date());

        boolean bool = false;

        if (startNo != null && startNo > 0) {
            List<LineStore> list = new ArrayList<>();
            String oldWorkblankNo = lineStore.getWorkblankNo();

            for (int i = startNo; i <= endNo; i++) {
                LineStore entity = new LineStore(lineStore);
                if (isAutoMatchProd) {
                    entity.setProductionOrder(matchProd(entity.getMaterialNo(), entity.getNumber()));
                }
                if (isAutoMatchPur) {
                    entity.setPurchaseOrder(matchPur(entity.getMaterialNo(), entity.getNumber()));
                }
                String workblankNo = oldWorkblankNo + "" + i;
                if (!StringUtils.isNullOrEmpty(suffixNo)) {
                    workblankNo += "_" + suffixNo;
                }
                entity.setWorkblankNo(workblankNo);
                entity.setProdNo(entity.getDrawingNo() + " " + entity.getWorkblankNo());
                list.add(entity);
            }

            bool = this.saveBatch(list);
        } else {
            if (isAutoMatchProd) {
                lineStore.setProductionOrder(matchProd(lineStore.getMaterialNo(), lineStore.getNumber()));
            }
            if (isAutoMatchPur) {
                lineStore.setPurchaseOrder(matchPur(lineStore.getMaterialNo(), lineStore.getNumber()));
            }

            bool = this.save(lineStore);
        }
        return bool;
    }

    //校验编号是否存在
    @Override
    public boolean checkCodeExist(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo) {
        boolean codeExist = false;

        //批次
        if (startNo != null && startNo > 0) {

            for (int i = startNo; i <= endNo; i++) {
                String oldWorkblankNo = lineStore.getWorkblankNo();

                String workblankNo = oldWorkblankNo + "" + i;
                if (!StringUtils.isNullOrEmpty(suffixNo)) {
                    workblankNo += "_" + suffixNo;
                }

                QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("workblank_no", workblankNo);
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                List<LineStore> result = this.list(queryWrapper);
                if (result != null && result.size() > 0) {
                    codeExist = true;
                    break;
                }
            }
            //单件
        } else {
            QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("workblank_no", lineStore.getWorkblankNo());
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            List<LineStore> result = this.list(queryWrapper);
            if (result != null && result.size() > 0) {
                codeExist = true;
            }
        }

        return codeExist;
    }


    private String matchProd(String materialNo, Integer number) {
        String orderNo = "";
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("material_code", materialNo);
        wrapper.orderByAsc("delivery_date");
        List<Order> orderList = orderService.list(wrapper);
        for (Order order : orderList) {
            QueryWrapper<LineStore> lWrapper = new QueryWrapper<>();
            lWrapper.select("sum(number) as number ");
            lWrapper.eq("production_order", order.getOrderSn());
            LineStore lineStore = this.getOne(lWrapper);
            int useQty = 0;
            if (lineStore != null) {
                useQty = lineStore.getNumber();
            }
            if (order.getOrderNum() - useQty >= number) {
                orderNo = order.getOrderSn();
                break;
            }
        }

        return orderNo;
    }

    private String matchPur(String materialNo, Integer number) {
        String orderNo = "";
        QueryWrapper<ProducePurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("material_no", materialNo);
        wrapper.orderByAsc("delivery_date");
        List<ProducePurchaseOrder> orderList = purchaseOrderService.list(wrapper);
        for (ProducePurchaseOrder order : orderList) {
            QueryWrapper<LineStore> lWrapper = new QueryWrapper<>();
            lWrapper.select("sum(number) as number ");
            lWrapper.eq("purchase_order", order.getOrderNo());
            LineStore lineStore = this.getOne(lWrapper);
            int useQty = 0;
            if (lineStore != null) {
                useQty = lineStore.getNumber();
            }
            if (order.getNumber() - useQty >= number) {
                orderNo = order.getOrderNo();
                break;
            }
        }

        return orderNo;
    }

}
