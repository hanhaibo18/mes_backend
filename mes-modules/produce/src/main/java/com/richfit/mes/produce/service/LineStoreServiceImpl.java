package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.code.StoreItemStatusEnum;
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
    private StoreAttachRelService storeAttachRelService;

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

    //TODO 方法的业务不明确，可能造成误用
    @Override
    public boolean changeStatus(TrackHead trackHead) {

        String pNo = trackHead.getUserProductNo(); //毛坯编号


        UpdateWrapper<LineStore> update2 = new UpdateWrapper<LineStore>();
        update2.set("status", StoreItemStatusEnum.FINISH.getCode()); //将状态设置为完工
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

    // 料单投用
    @Override
    public LineStore useItem(int num, String drawingNo, String workblankNo) {
        int userNum = 0; //本次使用数量
        //修改库存状态
        LineStore lineStore1 = lineStoreMapper.selectOne(
                new QueryWrapper<LineStore>().eq("drawing_no", drawingNo)
                        .eq("workblank_no", workblankNo)
                        .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId()));
        if (lineStore1 != null) {
            if (lineStore1.getNumber() - lineStore1.getUseNum() <= num) {
                userNum = lineStore1.getNumber() - lineStore1.getUseNum();
                num -= lineStore1.getNumber() - lineStore1.getUseNum();
                lineStore1.setUseNum(lineStore1.getNumber());
            } else {
                userNum = num;
                lineStore1.setUseNum(lineStore1.getUseNum() + num);
                num = 0;
            }
            if (lineStore1.getMaterialType().equals("0")) {
                lineStore1.setOutTime(new Date());
            }
            changeStatus(lineStore1);

            lineStoreMapper.updateById(lineStore1);
        } else {

        }


        return lineStore1;
    }

    //料单投用回滚
    // 如果已用归0，料单状态应为Finish，已用>0, 状态应为MAKING
    @Override
    public boolean rollBackItem(int num, String id) {

        LineStore lineStore = lineStoreMapper.selectOne(
                new QueryWrapper<LineStore>().eq("id", id)
                        .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId()));

        lineStore.setUseNum(lineStore.getUseNum() - num);
        changeStatus(lineStore);
        lineStoreMapper.updateById(lineStore);

        return true;
    }

    //料单 直接入库并直接全部投用
    @Override
    public LineStore autoInAndOutStoreByTrackHead(TrackHead trackHead, String workblankNo) {
        //TODO 增加自动料单入库 再出库的逻辑
        LineStore lineStore = new LineStore();
        lineStore.setBranchCode(trackHead.getBranchCode());
        lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());

        lineStore.setDrawingNo(trackHead.getDrawingNo());
        lineStore.setMaterialNo(trackHead.getSelectedMaterialNo());
        lineStore.setMaterialName(trackHead.getMaterialName());
        lineStore.setMaterialType("0");
        lineStore.setCertificateNo(trackHead.getMaterialCertificateNo());
        lineStore.setWeight(trackHead.getWeight());
        lineStore.setTexture(trackHead.getTexture());

        lineStore.setWorkNo(trackHead.getWorkNo());
        lineStore.setProdNo(trackHead.getProductNo());
        lineStore.setProductName(trackHead.getProductName());

        //物料编号： QAXXX   跟单产品编号=图号+物料号  这里通过跟单产品编号反推出物料编号
        lineStore.setWorkblankNo(workblankNo.replace(trackHead.getDrawingNo(), ""));

        lineStore.setInTime(new Date());
        lineStore.setOutTime(new Date());
        lineStore.setNumber(trackHead.getNumber());
        lineStore.setUseNum(trackHead.getNumber());
        changeStatus(lineStore);
        lineStore.setStockType("1"); //自动
        lineStore.setTrackType(trackHead.getTrackType());

        this.save(lineStore);

        return lineStore;
    }

    //根据料单原始数量和已投用数量对比，修改料单状态
    private void changeStatus(LineStore lineStore) {

        if (lineStore.getUseNum().equals(lineStore.getNumber())) {
            lineStore.setStatus(StoreItemStatusEnum.USED_ALL.getCode());
        } else if (lineStore.getUseNum() < lineStore.getNumber()) {
            lineStore.setStatus(StoreItemStatusEnum.MAKING.getCode());
        } else if (lineStore.getUseNum() == 0) {
            lineStore.setStatus(StoreItemStatusEnum.FINISH.getCode());
        }

    }

    // 材料入库
    @Override
    public boolean addStore(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo,
                            Boolean isAutoMatchProd, Boolean isAutoMatchPur, String branchCode) {

        lineStore.setUseNum(0);
        lineStore.setStatus(StoreItemStatusEnum.FINISH.getCode());
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

            //保存料单-附件关系
            for (LineStore s : list) {
                storeAttachRelService.batchSaveStoreFile(s.getId(), branchCode, lineStore.getFileIds());
            }

        } else {
            if (isAutoMatchProd) {
                lineStore.setProductionOrder(matchProd(lineStore.getMaterialNo(), lineStore.getNumber()));
            }
            if (isAutoMatchPur) {
                lineStore.setPurchaseOrder(matchPur(lineStore.getMaterialNo(), lineStore.getNumber()));
            }

            bool = this.save(lineStore);
            //保存料单-附件关系
            storeAttachRelService.batchSaveStoreFile(lineStore.getId(), branchCode, lineStore.getFileIds());
        }


        return bool;
    }

    //校验编号是否存在
    @Override
    public boolean checkCodeExist(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo) {
        boolean codeExist = false;

        //循环编号
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
            //单件编号
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

    //匹配生产订单
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

    //匹配采购订单
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
