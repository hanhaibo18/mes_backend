package com.richfit.mes.produce.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.code.StoreItemStatusEnum;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.model.produce.store.LineStoreSumZp;
import com.richfit.mes.common.model.produce.store.StoreAttachRel;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackHeadRelationMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.utils.FilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: 王瑞
 * @Date: 2020/8/11
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
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

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private RequestNoteService requestNoteService;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackHeadRelationMapper trackHeadRelationMapper;

    @Autowired
    private PublicService publicService;

    @Autowired
    private ActionService actionService;

    @Resource
    private TrackHeadRelationService trackHeadRelationService;

    @Resource
    private ErpServiceClient erpServiceClient;


    @Override

    public LineStore LineStoreById(String id) {
        LineStore lineStore = lineStoreMapper.selectById(id);
        QueryWrapper<StoreAttachRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("line_store_id", lineStore.getId());
        //查询文件关联关系记录
        List<StoreAttachRel> list = storeAttachRelService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> ids = list.stream().map(x -> x.getAttachmentId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                //获取文件信息
                List<Attachment> attachments = systemServiceClient.selectAttachmentsList(ids);
                if (CollectionUtils.isNotEmpty(attachments)) {
                    //文件map
                    Map<String, Attachment> attachmentMap = attachments.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
                    for (StoreAttachRel storeAttachRel : list) {
                        storeAttachRel.setFileName(attachmentMap.get(storeAttachRel.getAttachmentId()) == null ? "" : attachmentMap.get(storeAttachRel.getAttachmentId()).getAttachName());
                    }
                }
            }
        }


        //资料文件列表
        lineStore.setFileList(list);
        return lineStore;
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

        UpdateWrapper<LineStore> update2 = new UpdateWrapper<LineStore>();
        //将状态设置为完工
        update2.set("status", StoreItemStatusEnum.FINISH.getCode());
        update2.set("in_time", new Date());
        update2.eq("workblank_no", trackHead.getProductNo());
        update2.eq("drawing_no", trackHead.getDrawingNo());
        update2.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        int count = lineStoreMapper.update(null, update2);
        return count > 0;
    }

    // 料单投用
    @Override
    public Map useItem(int num, TrackHead trackHead, String workblankNo) {
        //本次使用数量
        int useNum = 0;
        //修改库存状态
        QueryWrapper<LineStore> lineStoreQueryWrapper = new QueryWrapper<>();
        lineStoreQueryWrapper
                .eq("workblank_no", workblankNo.replace(trackHead.getDrawingNo(), ""))
                .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        DrawingNoUtil.queryEq(lineStoreQueryWrapper, "drawing_no", trackHead.getDrawingNo());
        LineStore lineStore1 = lineStoreMapper.selectOne(lineStoreQueryWrapper);
        if (lineStore1 != null) {
            if (lineStore1.getNumber() - lineStore1.getUseNum() <= num) {
                useNum = lineStore1.getNumber() - lineStore1.getUseNum();
                num -= useNum;
                lineStore1.setUseNum(lineStore1.getNumber());
                //没有消耗光不需要修改状态
                changeStatus(lineStore1);
            } else {
                useNum = num;
                lineStore1.setUseNum(lineStore1.getUseNum() + num);
                num = 0;
            }
            // 0 : 毛坯
            if ("0".equals(lineStore1.getMaterialType())) {
                lineStore1.setOutTime(new Date());
            }

            //关联跟单号码
            lineStore1.setTrackNo(trackHead.getTrackNo());
            lineStoreMapper.updateById(lineStore1);
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
    public LineStore autoInAndOutStoreByTrackHead(int num, TrackHead trackHead, String workblankNo) {
        List<Product> productList = baseServiceClient.listByMaterialNo(trackHead.getSelectedMaterialNo());
        if (productList == null || productList.size() == 0) {
            throw new GlobalException("没有匹配到选择的物料信息", ResultCode.FAILED);
        }
        Product product = productList.get(0);
        //自动料单入库 再出库的逻辑
        LineStore lineStore = new LineStore();
        lineStore.setBranchCode(trackHead.getBranchCode());
        lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());

        lineStore.setCertificateNo(trackHead.getMaterialCertificateNo());
        lineStore.setDrawingNo(trackHead.getDrawingNo());
        lineStore.setMaterialNo(product.getMaterialNo());
        lineStore.setMaterialName(product.getMaterialDesc());
        lineStore.setMaterialType("0");

        lineStore.setWeight(product.getWeight());
        lineStore.setTexture(product.getTexture());

        lineStore.setWorkNo(trackHead.getWorkNo());
        lineStore.setProdNo(trackHead.getProductNo());
        lineStore.setProductName(trackHead.getProductName());

        //物料编号： QAXXX   跟单产品编号=图号+物料号  这里通过跟单产品编号反推出物料编号
        lineStore.setWorkblankNo(workblankNo.replace(trackHead.getDrawingNo(), ""));

        lineStore.setInTime(new Date());
        lineStore.setOutTime(new Date());
        lineStore.setNumber(num);
        lineStore.setUseNum(num);
        changeStatus(lineStore);
        //自动
        lineStore.setStockType("1");
        lineStore.setTrackType(trackHead.getTrackType());

        lineStore.setInputType("2");

        //关联跟单号码
        lineStore.setTrackNo(trackHead.getTrackNo());

        this.save(lineStore);

        return lineStore;
    }

    @Override
    public Boolean addStoreByCertTransfer(Certificate certificate) throws Exception {

        //0 原合格证推送状态改为已推送
        certificateService.certPushComplete(certificate);
        //1 保存新合格证信息


        //2 合格证下包括多个物料信息，需要逐条处理
        //2 如果对应物料产品编号在系统不存在 则新增料单入库
        for (TrackCertificate tc : certificate.getTrackCertificates()) {
            QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("workblank_no", tc.getProductNo());
            queryWrapper.eq("branch_code", certificate.getNextOptWork());
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

            LineStore lineStore = this.getOne(queryWrapper);


            if (null == lineStore) {
                lineStore = new LineStore(certificate, tc);
                lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                changeStatus(lineStore);
                this.save(lineStore);
            }
            //3 如果对应物料产品编号在系统存在，说明是本车间推送出去又回来的物料（该物料在本车间状态无需变动）
            //3 需要更新物料对应的跟单当前工序状态 为 完工， 并关联新合格证号
            else {
                QueryWrapper<TrackHead> trackHeadQueryWrapper = new QueryWrapper<TrackHead>();
                //该字段可能存有多个产品编号，故需要like查询
                trackHeadQueryWrapper.like("product_no", lineStore.getProdNo());
                List<TrackHead> list = trackHeadService.list(trackHeadQueryWrapper);
                if (!list.isEmpty()) {
                    publicService.thirdPartyAction(list.get(0).getId(), null, null);
                }
            }
        }

        certificate.setCertOrigin("1");
        certificate.setBranchCode(certificate.getNextOptWork());
        certificate.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        certificate.setCreateTime(new Date());
        certificate.setModifyTime(new Date());
        certificate.setId(null);
        certificateService.savePushCert(certificate);

        return true;
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
                            Boolean isAutoMatchProd, Boolean isAutoMatchPur, String branchCode, String strartSuffix) throws Exception {
        lineStore.setUseNum(0);
        lineStore.setStatus(StoreItemStatusEnum.FINISH.getCode());
        lineStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        lineStore.setBranchCode(branchCode);
        lineStore.setCreateTime(new Date());
        lineStore.setInTime(new Date());
        boolean bool;
        if (startNo != null && startNo > 0) {
            List<LineStore> list = new ArrayList<>();
            String oldWorkblankNo = lineStore.getWorkblankNo();
            //计算入库料单数量
            int num = 0;
            if (startNo.intValue() == 0) {
                num = endNo;
            } else {
                num = endNo - startNo + 1;
            }
            for (int i = startNo; i <= endNo; i++) {
                LineStore entity = new LineStore();
                //改为浅拷贝
                BeanUtils.copyProperties(lineStore, entity);
                if (isAutoMatchProd) {
                    //匹配生产订单
                    String orderNo = matchProd(entity.getMaterialNo(), entity.getNumber());
                    if (StrUtil.isNotBlank(orderNo)) {
                        HashMap<String, Integer> orderNumAndInNum = getOrderNumAndInNum(entity.getMaterialNo(), orderNo);
                        int surplusNum = orderNumAndInNum.get("orderNum") - orderNumAndInNum.get("inNum");
                        //当剩余数量小于当次入库数量提示入库数量大于订单数量
                        if (surplusNum < num) {
                            throw new GlobalException("录入量不能大于订单剩余量,订单: " + orderNo + " 的剩余数量为: " + surplusNum + " 您录入是数量为: " + num, ResultCode.FAILED);
                        }
                        entity.setProductionOrder(orderNo);
                    }
                }
                StringBuilder stringBuilder = new StringBuilder(strartSuffix);
                //毛坯编号中的数字补零操作
                String numstr = String.format("%0" + endNo.toString().length() + "d", i);
                //判断开始前缀有没有0，如果有0，则拼接到开始编号前，如果没有直接用startsuffix
                if (!StringUtils.isNullOrEmpty(strartSuffix)) {
                    stringBuilder.append(numstr);
                } else {
                    stringBuilder = new StringBuilder(numstr);
                }
                String workblankNo = oldWorkblankNo + "" + stringBuilder.toString();
//                String workblankNo = oldWorkblankNo + "" + i;
                if (!StringUtils.isNullOrEmpty(suffixNo)) {
                    workblankNo += suffixNo;
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
            bool = this.save(lineStore);
            //保存料单-附件关系
            storeAttachRelService.batchSaveStoreFile(lineStore.getId(), branchCode, lineStore.getFileIds());
        }
        return bool;
    }

    // 材料入库
    @Override
    public boolean addStoreNew(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo,
                               String isAutoMatchProd, Boolean isAutoMatchPur, String branchCode, String strartSuffix) throws Exception {

        lineStore.setUseNum(0);
        lineStore.setStatus(StoreItemStatusEnum.FINISH.getCode());
        lineStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        lineStore.setBranchCode(branchCode);
        lineStore.setCreateTime(new Date());
        lineStore.setInTime(new Date());

        boolean bool;

        if (startNo != null && startNo > 0) {
            List<LineStore> list = new ArrayList<>();
            String oldWorkblankNo = lineStore.getWorkblankNo();

            //计算入库料单数量
            int num = 0;
            if (startNo.intValue() == 0) {
                num = endNo;
            } else {
                num = endNo - startNo + 1;
            }
            String ordersSn = "";
            if (isAutoMatchProd.equals(2)) {
                ordersSn = createOrder(lineStore);
            }
            for (int i = startNo; i <= endNo; i++) {
                LineStore entity = new LineStore();
                //改为浅拷贝
                BeanUtils.copyProperties(lineStore, entity);
                if (isAutoMatchProd.equals(1)) {
                    //匹配生产订单
                    String orderNo = matchProd(entity.getMaterialNo(), entity.getNumber());
                    if (StrUtil.isNotBlank(orderNo)) {
                        HashMap<String, Integer> orderNumAndInNum = getOrderNumAndInNum(entity.getMaterialNo(), orderNo);
                        int surplusNum = orderNumAndInNum.get("orderNum") - orderNumAndInNum.get("inNum");
                        //当剩余数量小于当次入库数量提示入库数量大于订单数量
                        if (surplusNum < num) {
                            throw new GlobalException("录入量不能大于订单剩余量,订单: " + orderNo + " 的剩余数量为: " + surplusNum + " 您录入是数量为: " + num, ResultCode.FAILED);
                        }
                        entity.setProductionOrder(orderNo);
                    }
                } else if (isAutoMatchProd.equals(2)) {
                    entity.setProductionOrder(ordersSn);
                }
                StringBuilder stringBuilder = new StringBuilder(strartSuffix);
                //毛坯编号中的数字补零操作
                String numstr = String.format("%0" + endNo.toString().length() + "d", i);
                //判断开始前缀有没有0，如果有0，则拼接到开始编号前，如果没有直接用startsuffix
                if (!StringUtils.isNullOrEmpty(strartSuffix)) {
                    stringBuilder.append(numstr);
                } else {
                    stringBuilder = new StringBuilder(numstr);
                }
                String workblankNo = oldWorkblankNo + "" + stringBuilder.toString();
//                String workblankNo = oldWorkblankNo + "" + i;
                if (!StringUtils.isNullOrEmpty(suffixNo)) {
                    workblankNo += suffixNo;
                }
                entity.setWorkblankNo(workblankNo);
                entity.setProdNo(entity.getDrawingNo() + " " + entity.getWorkblankNo());
                list.add(entity);
            }
            for (LineStore s : list) {
                if (StrUtil.isBlank(s.getProductionOrder())) {
                    throw new Exception("没有订单！");
                }
            }
            bool = this.saveBatch(list);
            //保存料单-附件关系
            for (LineStore s : list) {
//                storeAttachRelService.batchSaveStoreFile(s.getId(), branchCode, lineStore.getFileIds());
                if (CollectionUtils.isNotEmpty(lineStore.getFileList())) {
                    storeAttachRelService.batchSaveStoreFileNew(s.getId(), branchCode, lineStore.getFileList());
                }
            }
            //erp生产投料
            if (bool) {
                for (LineStore l : list) {
                    l = erpServiceClient.storeSendFeeding(l).getData();
                    this.updateById(l);
                }
            }
        } else {
            if (isAutoMatchProd.equals("1")) {
                lineStore.setProductionOrder(matchProd(lineStore.getMaterialNo(), lineStore.getNumber()));
            }
            if (StrUtil.isBlank(lineStore.getProductionOrder())) {
                throw new Exception("没有订单！");
            }
            bool = this.save(lineStore);
            //保存料单-附件关系
//            storeAttachRelService.batchSaveStoreFile(lineStore.getId(), branchCode, lineStore.getFileIds());
            if (CollectionUtils.isNotEmpty(lineStore.getFileList())) {
                storeAttachRelService.batchSaveStoreFileNew(lineStore.getId(), branchCode, lineStore.getFileList());
            }
            //erp生产投料
            if (bool) {
                lineStore = erpServiceClient.storeSendFeeding(lineStore).getData();
                this.updateById(lineStore);
            }
        }
        return bool;
    }


    /**
     * 根据入库料单生成订单
     *
     * @param lineStore
     */
    private String createOrder(LineStore lineStore) {
        //生成订单
        Order order = new Order();
        String orderSn = UUID.randomUUID().toString().replaceAll("-", "");
        order.setOrderSn(orderSn);//订单号
        order.setOrderDate(new Date());//下单日期
        order.setMaterialCode(lineStore.getMaterialNo());//物料编码
        order.setOrderNum(lineStore.getNumber());//订单数量
        order.setDeliveryDate(new Date());//交货日期
        order.setPriority("低");///优先级
        order.setBranchCode(lineStore.getBranchCode());
        order.setStatus(0);
        order.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        order.setCreateTime(new Date());
        order.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        order.setMaterialDesc(lineStore.getMaterialName());
        order.setProduction(0);
//            order.setStartTime();
//            order.setEndTime();
//            order.setOrderType();
//            order.setInChargeOrg();
        orderService.save(order);
        return orderSn;
    }

    //校验编号是否存在
    @Override
    public boolean checkCodeExist(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo, String strartSuffix) {
        boolean codeExist = false;

        //循环编号
        if (startNo != null && startNo > 0) {
            for (int i = startNo; i <= endNo; i++) {
                String oldWorkblankNo = lineStore.getWorkblankNo();
                StringBuilder stringBuilder = new StringBuilder(strartSuffix);
                //毛坯编号中的数字补零操作
                String numstr = String.format("%0" + endNo.toString().length() + "d", i);
                //判断开始前缀有没有0，如果有0，则拼接到开始编号前，如果没有直接用startsuffix
                if (!StringUtils.isNullOrEmpty(strartSuffix)) {
                    stringBuilder.append(numstr);
                } else {
                    stringBuilder = new StringBuilder(numstr);
                }
                String workblankNo = oldWorkblankNo + "" + stringBuilder.toString();
                if (!StringUtils.isNullOrEmpty(suffixNo)) {
                    workblankNo += suffixNo;
                }

                QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
                DrawingNoUtil.queryEq(queryWrapper, "drawing_no", lineStore.getDrawingNo());
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
            DrawingNoUtil.queryEq(queryWrapper, "drawing_no", lineStore.getDrawingNo());
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
    private String matchProd(String materialNo, Integer number) throws Exception {
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
        if (StrUtil.isBlank(orderNo)) {
            throw new Exception("自动匹配没有找到订单！");
        }
        return orderNo;
    }

    /**
     * 根据订单号获取订单的物料数量和已入库数量
     *
     * @param materialNo 物料编码
     * @param orderNo    订单号
     * @return
     */
    @Override
    public HashMap<String, Integer> getOrderNumAndInNum(String materialNo, String orderNo) {
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //根据物料号和订单号获取订单
        orderWrapper.eq("tenant_id", currentUser.getTenantId());
        orderWrapper.eq("material_code", materialNo);
        orderWrapper.eq("order_sn", orderNo);
        orderWrapper.orderByAsc("delivery_date");
        List<Order> orderList = orderService.list(orderWrapper);

        HashMap<String, Integer> numMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            Order order = orderList.get(0);
            numMap.put("orderNum", order.getOrderNum());//订单物料数量

            //根据订单号计算入库数量
            QueryWrapper<LineStore> lWrapper = new QueryWrapper<>();
            lWrapper.select("sum(number) as number ");
            lWrapper.eq("production_order", orderNo);
            lWrapper.eq("branch_code", order.getBranchCode());
            LineStore lineStore = this.getOne(lWrapper);
            if (ObjectUtils.isNotEmpty(lineStore)) {
                numMap.put("inNum", lineStore.getNumber());//已入库数量
            } else {
                numMap.put("inNum", 0);//已入库数量为0
            }
        }
        return numMap;
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
    public void reSetCertNoByCertNo(String certificateNo) {
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
    @Transactional(rollbackFor = Exception.class)
    public String zpExpend(String trackHeadId, String flowId, String materialNo, int number, String branchCode, String tenantId) {
        //查询所有可绑定数量大于0的
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        queryWrapper.apply("number-use_num >" + "0");
        queryWrapper.eq("material_no", materialNo);
//        queryWrapper.eq("input_type", "3");
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.notIn("status", "3");
        queryWrapper.orderByAsc("create_time");
        List<LineStore> lineStoreList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(lineStoreList)) {
            throw new GlobalException("未查询到可绑定物料!", ResultCode.FAILED);
        }
        //绑定
        //剩余数量
        int surplus = number;
        List<String> bindingIdList = new ArrayList<>();
        try {
            for (LineStore lineStore : lineStoreList) {
                //判断还有需要绑定数量,没有结束循环
                if (surplus == 0) {
                    break;
                }
                //创建出库单
                TrackHeadRelation relation = new TrackHeadRelation();
                relation.setFlowId(flowId);
                relation.setThId(trackHeadId);
                relation.setLsId(lineStore.getId());
                relation.setType("1");
                //计算可绑定数量
                int num = lineStore.getNumber() - lineStore.getUseNum();
                //判断是剩余大于可绑定数量
                if (surplus > num) {
                    //大于可绑定数量,先绑定可绑定数量
                    lineStore.setUseNum(lineStore.getNumber());
                    lineStore.setStatus("3");
                    //绑定后剩余数量
                    surplus = surplus - num;
                    relation.setNumber(num);
                    //写出库记录
                    //记录表不返回Id 自行查询最大的ID
                    TrackHeadRelation id = trackHeadRelationService.getOne(new LambdaQueryWrapper<TrackHeadRelation>().orderByDesc(TrackHeadRelation::getId).last("limit 1"));
                    relation.setId(id.getId() + 1);
                    trackHeadRelationService.save(relation);
                    bindingIdList.add(String.valueOf(relation.getId()));
                    this.updateById(lineStore);
                    continue;
                }
                //判断剩余数量等于可绑定数量
                if (surplus == num) {
                    lineStore.setUseNum(lineStore.getNumber());
                    lineStore.setStatus("3");
                    //绑定后剩余数量
                    surplus = 0;
                    relation.setNumber(num);
                    //写出库记录
                    //记录表不返回Id 自行查询最大的ID
                    TrackHeadRelation id = trackHeadRelationService.getOne(new LambdaQueryWrapper<TrackHeadRelation>().orderByDesc(TrackHeadRelation::getId).last("limit 1"));
                    relation.setId(id.getId() + 1);
                    trackHeadRelationService.save(relation);
                    bindingIdList.add(String.valueOf(relation.getId()));
                    this.updateById(lineStore);
                    continue;
                }
                lineStore.setUseNum(lineStore.getUseNum() + surplus);
                //绑定后剩余数量
                surplus = 0;
                //创建出库单
                relation.setNumber(num);
                //写出库记录
                //记录表不返回Id 自行查询最大的ID
                TrackHeadRelation id = trackHeadRelationService.getOne(new LambdaQueryWrapper<TrackHeadRelation>().orderByDesc(TrackHeadRelation::getId).last("limit 1"));
                relation.setId(id.getId() + 1);
                trackHeadRelationService.save(relation);
                bindingIdList.add(String.valueOf(relation.getId()));
                this.updateById(lineStore);
            }
            if (surplus != 0) {
                throw new GlobalException("绑定失败,可绑定数量不足", ResultCode.FAILED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("绑定数量计算错误", ResultCode.FAILED);
        }
        return String.join(",", bindingIdList);
    }

    @Override
    public Map<String, String> partsBinding(String trackHeadId, String flowId, String materialNo, int number, String branchCode, String tenantId) {
        List<String> failureList = new ArrayList<>();
        //查询所有可绑定数量大于0的
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        queryWrapper.apply("number-use_num >" + "0");
        queryWrapper.eq("material_no", materialNo);
        queryWrapper.eq("input_type", "3");
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.notIn("status", "3");
        queryWrapper.orderByAsc("create_time");
        List<LineStore> lineStoreList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(lineStoreList)) {
            failureList.add(materialNo);
        }
        //绑定
        //剩余数量
        int surplus = number;
        List<String> bindingIdList = new ArrayList<>();
        try {
            for (LineStore lineStore : lineStoreList) {
                //判断还有需要绑定数量,没有结束循环
                if (surplus == 0) {
                    break;
                }
                //创建出库单
                TrackHeadRelation relation = new TrackHeadRelation();
                relation.setFlowId(flowId);
                relation.setThId(trackHeadId);
                relation.setLsId(lineStore.getId());
                relation.setType("1");
                //计算可绑定数量
                int num = lineStore.getNumber() - lineStore.getUseNum();
                //判断是剩余大于可绑定数量
                if (surplus > num) {
                    //大于可绑定数量,先绑定可绑定数量
                    lineStore.setUseNum(lineStore.getNumber());
                    lineStore.setStatus("3");
                    //绑定后剩余数量
                    surplus = surplus - num;
                    relation.setNumber(num);
                    //写出库记录
                    //记录表不返回Id 自行查询最大的ID
                    TrackHeadRelation id = trackHeadRelationService.getOne(new LambdaQueryWrapper<TrackHeadRelation>().orderByDesc(TrackHeadRelation::getId).last("limit 1"));
                    relation.setId(id.getId() + 1);
                    trackHeadRelationService.save(relation);
                    bindingIdList.add(String.valueOf(relation.getId()));
                    this.updateById(lineStore);
                    continue;
                }
                //判断剩余数量等于可绑定数量
                if (surplus == num) {
                    lineStore.setUseNum(lineStore.getNumber());
                    lineStore.setStatus("3");
                    //绑定后剩余数量
                    surplus = 0;
                    relation.setNumber(num);
                    //写出库记录
                    //记录表不返回Id 自行查询最大的ID
                    TrackHeadRelation id = trackHeadRelationService.getOne(new LambdaQueryWrapper<TrackHeadRelation>().orderByDesc(TrackHeadRelation::getId).last("limit 1"));
                    relation.setId(id.getId() + 1);
                    trackHeadRelationService.save(relation);
                    bindingIdList.add(String.valueOf(relation.getId()));
                    this.updateById(lineStore);
                    continue;
                }
                lineStore.setUseNum(lineStore.getUseNum() + surplus);
                //绑定后剩余数量
                surplus = 0;
                //创建出库单
                relation.setNumber(num);
                //写出库记录
                //记录表不返回Id 自行查询最大的ID
                TrackHeadRelation id = trackHeadRelationService.getOne(new LambdaQueryWrapper<TrackHeadRelation>().orderByDesc(TrackHeadRelation::getId).last("limit 1"));
                relation.setId(id.getId() + 1);
                trackHeadRelationService.save(relation);
                bindingIdList.add(String.valueOf(relation.getId()));
                this.updateById(lineStore);
            }
            if (surplus != 0) {
                failureList.add(materialNo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("绑定数量计算错误", ResultCode.FAILED);
        }
        Map<String, String> map = new HashMap<>();
        map.put("failureList", String.join(",", failureList));
        map.put("success", String.join(",", bindingIdList));
        return map;
    }


    @Override
    @Transactional
    public boolean unbundling(String idList) {
        try {
            List<String> list = new ArrayList<>();
            if (idList.contains(",")) {
                list = Arrays.asList(idList.split(","));
            } else {
                list.add(idList);
            }
            for (String id : list) {
                TrackHeadRelation trackHeadRelation = trackHeadRelationService.getById(id);
                LineStore lineStore = this.getById(trackHeadRelation.getLsId());
                //计算已使用数量
                int num = lineStore.getUseNum() - trackHeadRelation.getNumber();
                lineStore.setUseNum(num);
                if ("3".equals(lineStore.getStatus())) {
                    lineStore.setStatus("0");
                }
                this.updateById(lineStore);
                trackHeadRelationService.removeById(id);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("解绑失败", ResultCode.FAILED);
        }
        return true;
    }

    @Override
    public String loadFileToFolder(String id) throws Exception {

        //查询附件
        QueryWrapper<StoreAttachRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lineStoreId", id);

        List<StoreAttachRel> storeAttachRels = storeAttachRelService.list(queryWrapper);

        String path = FilesUtil.tempPath();

        for (StoreAttachRel storeAttachRel : storeAttachRels) {

            CommonResult<Attachment> atta = systemServiceClient.attachment(storeAttachRel.getId());
            CommonResult<byte[]> data = systemServiceClient.getAttachmentInputStream(storeAttachRel.getId());

            if (data.getStatus() == 200) {
                File file = new File(path + "/" +
                        (StringUtils.isNullOrEmpty(atta.getData().getAttachName()) ? atta.getData().getId() + "." +
                                atta.getData().getAttachType() : atta.getData().getAttachName()));
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                FileUtil.writeBytes(data.getData(), file);
            } else {
                throw new Exception("从文件服务器下载文件失败");
            }
        }

        ZipUtil.zip(path);
        return path + ".zip";
    }

    @Override
    public List<String> queryStoreFileIdList(String id) {
        //查询附件
        QueryWrapper<StoreAttachRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("line_store_id", id);

        List<StoreAttachRel> storeAttachRels = storeAttachRelService.list(queryWrapper);

        List<String> idList = new ArrayList<>();

        for (StoreAttachRel storeAttachRel : storeAttachRels) {
            idList.add(storeAttachRel.getAttachmentId());
        }

        return idList;
    }

    @Override
    public boolean addStoreByWmsSend(List<MaterialReceiveDetail> materialReceiveDetails, String branchCode) {

        //取一条中的的申请单号
        String aplyNum = materialReceiveDetails.get(0).getAplyNum();

        //根据申请单号，获取关联的跟单id
        QueryWrapper<RequestNote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("request_note_number", aplyNum);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        RequestNote requestNote = requestNoteService.getOne(queryWrapper);

        //根据跟单Id，获取跟单，拿到其中的订单号
        if (ObjectUtils.isNotNull(requestNote)) {
            TrackHead trackHead = trackHeadService.getById(requestNote.getTrackHeadId());
            String orderNo = trackHead.getProductionOrder();

            //把收料信息转换成料单信息，入库
            for (MaterialReceiveDetail materialReceiveDetail : materialReceiveDetails) {
                LineStore lineStore = new LineStore(materialReceiveDetail, branchCode);
                lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                lineStore.setProductionOrder(orderNo);
                this.save(lineStore);
            }
            return true;
        } else {
            return false;
        }


    }


    @Override
    public IPage<LineStoreSumZp> queryLineStoreSumZp(Page<LineStoreSumZp> page, Map parMap) {

        //图号查询处理
        if (!ObjectUtil.isEmpty(parMap.get("drawingNo"))) {
            parMap.put("drawingNoSql", DrawingNoUtil.queryLikeSql("drawing_no", String.valueOf(parMap.get("drawingNo"))));
        }
        //1 当前库存量
        IPage<LineStoreSumZp> storeList = this.lineStoreMapper.selectStoreNumForAssembly(page, parMap);
        log.debug("库存记录条数 [{}]", storeList.getRecords().size());
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
        for (LineStoreSumZp store : storeList.getRecords()) {

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

    @Override
    public Integer queryLineStoreSumZpNumber(Map parMap) {
        List<LineStoreSumZp> storeList = this.lineStoreMapper.selectStoreNumForAssembly(parMap);
        log.debug("库存记录条数 [{}]", storeList.size());
        return storeList.size();
    }


    @Override
    public LineStore addCpStoreByTrackHead(TrackHead trackHead, String productsNo, Integer number) {
        //新增一条半成品/成品信息
        QueryWrapper<LineStore> queryWrapperStore = new QueryWrapper<>();
        queryWrapperStore.eq("workblank_no", trackHead.getDrawingNo() + " " + productsNo);
        queryWrapperStore.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<LineStore> lineStores = this.list(queryWrapperStore);
        if (lineStores != null && lineStores.size() > 0) {
            //校验是否存在重复的产品编码
            throw new RuntimeException("产品编号已存在！");
        } else {
            List<Product> productList = baseServiceClient.listByMaterialNo(trackHead.getSelectedMaterialNo());
            if (productList == null || productList.size() == 0) {
                throw new GlobalException("没有匹配到选择的物料信息", ResultCode.FAILED);
            }
            Product product = productList.get(0);
            //新增一条半成品/成品信息
            LineStore lineStoreCp = getLineStore(trackHead, productsNo, number, product);
            lineStoreMapper.insert(lineStoreCp);
            return lineStoreCp;
        }
    }

    @Override
    public List<LineStore> listByFlowId(String flowId, String type) {
        //查询跟单物料关联表
        List<LineStore> lineStoreList = new ArrayList<>();
        QueryWrapper<TrackHeadRelation> queryWrapperTrackHeadRelation = new QueryWrapper<>();
        queryWrapperTrackHeadRelation.eq("flow_id", flowId);
        queryWrapperTrackHeadRelation.eq(!StrUtil.isBlank(type), "type", type);
        List<TrackHeadRelation> trackHeadRelationList = trackHeadRelationMapper.selectList(queryWrapperTrackHeadRelation);
        //封装料单信息
        for (TrackHeadRelation thr : trackHeadRelationList) {
            lineStoreList.add(this.getById(thr.getLsId()));
        }
        return lineStoreList;
    }

    private static LineStore getLineStore(TrackHead trackHead, String productsNo, Integer number, Product product) {
        LineStore lineStoreCp = new LineStore();
        lineStoreCp.setId(UUID.randomUUID().toString().replace("-", ""));
        lineStoreCp.setTenantId(trackHead.getTenantId());
        lineStoreCp.setDrawingNo(trackHead.getDrawingNo());
        lineStoreCp.setMaterialNo(trackHead.getMaterialNo());
        lineStoreCp.setMaterialName(trackHead.getMaterialName());
        lineStoreCp.setWorkblankNo(trackHead.getDrawingNo() + " " + productsNo);
        lineStoreCp.setWorkNo(trackHead.getWorkNo());
        lineStoreCp.setProdNo(trackHead.getProductNo());
        lineStoreCp.setProductName(trackHead.getProductName());
        lineStoreCp.setBatchNo(trackHead.getBatchNo());
        lineStoreCp.setWeight(trackHead.getWeight());
        lineStoreCp.setTexture(trackHead.getTexture());
        //添加订单信息
        lineStoreCp.setProductionOrder(trackHead.getProductionOrder());
        lineStoreCp.setProductionOrderId(trackHead.getProductionOrderId());
        //添加单件多个产品
        lineStoreCp.setNumber(number);
        lineStoreCp.setUseNum(0);
        //在制状态
        lineStoreCp.setStatus("1");
        lineStoreCp.setTrackNo(trackHead.getTrackNo());
        lineStoreCp.setMaterialType("1");
        lineStoreCp.setTrackType(trackHead.getTrackType());
        lineStoreCp.setInTime(new Date());
        lineStoreCp.setBranchCode(trackHead.getBranchCode());
        lineStoreCp.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        //录入类型 系统自动生成
        lineStoreCp.setInputType("2");
        lineStoreCp.setStockType("1");
        return lineStoreCp;
    }
}
