package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.code.StoreItemStatusEnum;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.model.produce.store.LineStoreSumZp;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    CertificateService certificateService;

    @Override
    public LineStore LineStoreById(String id) {
        return lineStoreMapper.selectById(id);
    }

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
    public Map useItem(int num, TrackHead trackHead, String workblankNo) {
        int useNum = 0; //本次使用数量
        //修改库存状态
        LineStore lineStore1 = lineStoreMapper.selectOne(
                new QueryWrapper<LineStore>().eq("drawing_no", trackHead.getDrawingNo())
                        .eq("workblank_no", workblankNo.replace(trackHead.getDrawingNo(), ""))
                        .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId()));
        if (lineStore1 != null) {
            if (lineStore1.getNumber() - lineStore1.getUseNum() <= num) {
                useNum = lineStore1.getNumber() - lineStore1.getUseNum();
                num -= useNum;
                lineStore1.setUseNum(lineStore1.getNumber());
                changeStatus(lineStore1);//没有消耗光不需要修改状态
            } else {
                useNum = num;
                lineStore1.setUseNum(lineStore1.getUseNum() + num);
                num = 0;
            }
            if (lineStore1.getMaterialType().equals("0")) {
                lineStore1.setOutTime(new Date());
            }

            //关联跟单号码
            lineStore1.setTrackNo(trackHead.getTrackNo());
            lineStoreMapper.updateById(lineStore1);
        } else {

        }

        Map map = new HashMap();
        map.put("lineStore", lineStore1);
        map.put("remainNum", num);
        map.put("useNum", useNum);

        return map;
    }

    //料单投用回滚
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

    /**
     * 料单 直接入库并直接全部投用
     * <p>
     * 对应的是通过物料号生成跟单的情况，如果对应物料无库存，则进行如下料单补料操作
     */

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

        //关联跟单号码
        lineStore.setTrackNo(trackHead.getTrackNo());

        this.save(lineStore);

        return lineStore;
    }

    @Override
    public LineStore addStoreByCertTransfer(Certificate certificate) throws Exception {

        //1 保存新合格证信息
        //2 如果对应物料产品编号在系统存在，说明是本车间推送出去又回来的物料（该物料在本车间状态无需变动）
        //2 需要更新物料对应的跟单当前工序状态 为 完工， 并关联新合格证号
        certificateService.saveCertificate(certificate);

        //3 如果对应物料产品编号在系统不存在 则新增料单入库
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("workblank_no", certificate.getProductNo());
        queryWrapper.eq("branch_code", certificate.getNextOptWork());
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        LineStore lineStore = this.getOne(queryWrapper);

        if (null == lineStore) {
            lineStore = new LineStore(certificate);
            changeStatus(lineStore);
            this.save(lineStore);
        }

        return lineStore;
    }


    /**
     * 根据料单原始数量和已投用数量对比，修改料单状态
     * <p>
     * 单件、批次毛坯物料： 只有入库  消耗   作废状态； 无在制状态
     * 单件、批次半成品/成品：有在制  入库  消耗 作废状态
     *
     * @param lineStore
     */
    private void changeStatus(LineStore lineStore) {

        //毛坯
        if ("0".equals(lineStore.getMaterialType())) {
            if (lineStore.getUseNum().equals(lineStore.getNumber())) {
                lineStore.setStatus(StoreItemStatusEnum.USED_ALL.getCode());
            } else {
                lineStore.setStatus(StoreItemStatusEnum.FINISH.getCode());
            }
            //半成品 成品
        } else if ("1".equals(lineStore.getMaterialType())) {
            if (lineStore.getUseNum().equals(lineStore.getNumber())) {
                lineStore.setStatus(StoreItemStatusEnum.USED_ALL.getCode());
            } else if (null == lineStore.getCertificateNo()) {
                lineStore.setStatus(StoreItemStatusEnum.MAKING.getCode());
            } else if (null != lineStore.getCertificateNo()) {
                lineStore.setStatus(StoreItemStatusEnum.FINISH.getCode());
            }

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
                LineStore entity = new LineStore();
                //改为浅拷贝
                BeanUtils.copyProperties(lineStore, entity);
                if (isAutoMatchProd) {
                    entity.setProductionOrder(matchProd(entity.getMaterialNo(), entity.getNumber()));
                }
//                if (isAutoMatchPur) {
//                    entity.setPurchaseOrder(matchPur(entity.getMaterialNo(), entity.getNumber()));
//                }
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

    @Override
    public LineStore updateCertNoByCertTrack(TrackHead trackHead) {

        UpdateWrapper<LineStore> update = new UpdateWrapper<LineStore>();

        //将状态设置为完工
        update.set("status", StoreItemStatusEnum.FINISH.getCode());
        update.set("certificate_No", trackHead.getCertificateNo());

        update.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        update.eq("branch_code", trackHead.getBranchCode());
        update.eq("track_no", trackHead.getTrackNo());

        lineStoreMapper.update(null, update);

        return null;
    }

    @Override
    public void reSetCertNoByTrackHead(String certificateNo) {
        UpdateWrapper<LineStore> update = new UpdateWrapper<LineStore>();

        //将状态设置为在制 合格证号清空
        update.set("status", StoreItemStatusEnum.MAKING.getCode());
        update.set("certificate_No", null);

        update.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        update.eq("certificate_No", certificateNo);

        lineStoreMapper.update(null, update);
    }


    @Override
    public void reSetCertNoByTrackHead(TrackHead trackHead) {
        UpdateWrapper<LineStore> update = new UpdateWrapper<LineStore>();

        //将状态设置为在制 合格证号清空
        update.set("status", StoreItemStatusEnum.MAKING.getCode());
        update.set("certificate_No", null);

        update.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        update.eq("branch_code", trackHead.getBranchCode());
        update.eq("track_no", trackHead.getTrackNo());

        lineStoreMapper.update(null, update);
    }


    @Override
    public List<LineStoreSumZp> queryLineStoreSumZp(Map parMap) throws Exception {

        //1 当前库存量
        List<LineStoreSumZp> storeList = this.lineStoreMapper.selectStoreNumForAssembly(parMap);
        log.debug("库存记录条数 [{}]", storeList.size());
        //2 当前配送量
        List<LineStoreSumZp> deliveryList = this.lineStoreMapper.selectDeliveryNumber(parMap);
        log.debug("配送记录条数 [{}]", deliveryList.size());
        //3 跟单需求量
        List<LineStoreSumZp> requireList = this.lineStoreMapper.selectRequireNum(parMap);
        log.debug("跟单需求条数 [{}]", requireList.size());
        //4 跟单已装量
        List<LineStoreSumZp> assemblyList = this.lineStoreMapper.selectAssemblyNum(parMap);
        log.debug("跟单已装条数 [{}]", assemblyList.size());

        //根据图号  把 2 、3 、4 的数据 更新到 1 中
        for (LineStoreSumZp store : storeList) {

            for (LineStoreSumZp delivery : deliveryList) {
                if (store.getDrawingNo().equals(delivery.getDrawingNo())) {
                    store.setDeliveryNumber(delivery.getDeliveryNumber());
                    deliveryList.remove(delivery);
                    break;
                }
            }

            for (LineStoreSumZp require : requireList) {
                if (store.getDrawingNo().equals(require.getDrawingNo())) {
                    store.setRequireNumber(require.getRequireNumber());
                    requireList.remove(require);
                    break;
                }
            }

            for (LineStoreSumZp assembly : assemblyList) {
                if (store.getDrawingNo().equals(assembly.getDrawingNo())) {
                    store.setAssemblyNumber(assembly.getAssemblyNumber());
                    assemblyList.remove(assembly);
                    break;
                }
            }

            store.setWaitAssemblyNumber((store.getRequireNumber() == null ? 0 : store.getRequireNumber()) - (store.getAssemblyNumber() == null ? 0 : store.getAssemblyNumber()));

        }

        return storeList;
    }
}
