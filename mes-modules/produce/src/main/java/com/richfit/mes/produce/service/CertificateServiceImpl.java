package com.richfit.mes.produce.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.code.CertTypeEnum;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.enmus.OptTypeEnum;
import com.richfit.mes.produce.entity.CertQueryDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: 王瑞
 * @Date: 2020/8/11
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements CertificateService {

    @Autowired
    CertificateMapper certificateMapper;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    private LineStoreService lineStoreService;

    @Autowired
    private TrackCertificateService trackCertificateService;

    @Autowired
    private CertAdditionalBsns certAdditionalBsns;

    @Autowired
    private CodeRuleService codeRuleService;

    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;
    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    private TrackHeadCastService trackHeadCastService;

    @Override
    public IPage<Certificate> selectCertificate(Page<Certificate> page, QueryWrapper<Certificate> query) {
        return fillBranchName(certificateMapper.selectCertificate(page, query));
    }

    @Override
    public List<TrackHead> selectItemTrack(TrackHead trackHead) {
        return certificateMapper.selectItemTrack(trackHead);
    }

    @Override
    public void autoCertificate(TrackHead trackHead) throws Exception {
        //装配车间
        if (!"2".equals(trackHead.getClasses()) && !"BOMCO_BY_ZPG1".equals(trackHead.getTemplateCode())) {
            return;
        }
        //开具合格证校验
        if (StrUtil.isBlank(trackHead.getProductNo())) {
            throw new Exception("当前跟单没有产品编码，请先补齐信息");
        }
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        Certificate certificate = new Certificate();
        certificate.setBranchCode(trackHead.getBranchCode());
        certificate.setCertOrigin("0");
        certificate.setCertificateNo(Code.valueOnUpdate("hege_no", trackHead.getTenantId(), trackHead.getBranchCode(), codeRuleService));
        certificate.setCheckName("System");
        certificate.setCheckTime(new Date());
        certificate.setDrawingNo(trackHead.getDrawingNo());
        certificate.setMaterialNo(trackHead.getMaterialNo());
        certificate.setNextOpt("/");
        //裝配开具并生产入库
        if ("2".equals(trackHead.getClasses()) && "BOMCO_BY_ZPG1".equals(trackHead.getTemplateCode())) {
            certificate.setNextOptWork("BOMCO_SC");
        }
        certificate.setNumber(trackHead.getNumber());
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        queryWrapper.eq("track_head_id", trackHead.getId());
        queryWrapper.orderByDesc("opt_sequence");
        List<TrackItem> list = trackItemService.list(queryWrapper);
        //去重操作(工序号+工序名 都一样认为重复)
        ArrayList<TrackItem> collect = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(trackItem -> trackItem.getOptName() + "-" + trackItem.getOptNo()))), ArrayList::new));
        //添加工序列表排序
        Collections.sort(collect, new Comparator<TrackItem>() {
            @Override
            public int compare(TrackItem o1, TrackItem o2) {
                return o1.getOptSequence() - o2.getOptSequence();
            }
        });
        TrackItem trackItem = collect.get(collect.size() - 1);
        certificate.setOptSequence(trackItem.getOptSequence());
        certificate.setOptNo(trackItem.getOptNo());
        certificate.setOptName(trackItem.getOptName());
        certificate.setProductName(trackHead.getProductName());
        certificate.setProductNo(trackHead.getProductNo());
        certificate.setProductNoContinuous(trackHead.getProductNoContinuous());
        certificate.setProductNoDesc(trackHead.getProductNoDesc());
        certificate.setProductionOrder(trackHead.getProductionOrder());
        certificate.setReplaceMaterial(trackHead.getReplaceMaterial());
        certificate.setTenantId(trackHead.getTenantId());
        certificate.setTestBarNumber(trackHead.getTestBarNumber());
        certificate.setTestBarType(trackHead.getTestBarType());
        certificate.setTexture(trackHead.getTexture());
        certificate.setBatchNo(trackHead.getBatchNo());
        List<TrackCertificate> trackCertificates = new ArrayList<>();
        TrackCertificate trackCertificate = new TrackCertificate();
        trackCertificate.setThId(trackHead.getId());
        trackCertificates.add(trackCertificate);
        certificate.setTrackCertificates(trackCertificates);
        certificate.setType("1");
        certificate.setWeight(trackHead.getWeight());
        certificate.setWorkNo(trackHead.getWorkNo());
        saveCertificate(certificate);
    }

    /**
     * 跟单流转方法
     */
    @Override
    public void headMoveToNextBranch(String thId, String nextOptWork, TrackItem trackItem) {
        try {
            //开出工序合格证
            Certificate certificate = heatAutoCertificate(thId, nextOptWork, trackItem);
            //根据合格证在下车间开跟单
            autoCreateTrackHeadByCertificate(certificate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 热工根据跟单开工序合格证
     *
     * @param thId
     * @param nextOptWork 下车间编码
     * @param trackItem
     * @return
     * @throws Exception
     */
    public Certificate heatAutoCertificate(String thId, String nextOptWork, TrackItem trackItem) throws Exception {
        //开具合格证的跟单
        TrackHead trackHead = trackHeadService.getById(thId);
        //登陆人信息
        TenantUserVo tenantUser = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId()).getData();
        Certificate certificate = new Certificate();
        //跟单属性赋值
        BeanUtil.copyProperties(trackHead, certificate, new String[]{"id", "createTime", "modifyTime", "modifyBy"});
        //根据分流id查询当前工序
        if (!ObjectUtil.isEmpty(trackItem)) {
            //本工序（开合格证的工序）
            TrackItem cretificateItem = trackItem;
            //根据工序顺序查询下工序
            QueryWrapper<TrackItem> trackItemQw = new QueryWrapper<>();
            trackItemQw.eq("opt_sequence", cretificateItem.getNextOptSequence())
                    .eq("flow_id", cretificateItem.getFlowId());
            List<TrackItem> nextItems = trackItemService.list(trackItemQw);
            certificate.setOptSequence(cretificateItem.getOptSequence());
            certificate.setOptNo(cretificateItem.getOptNo());
            certificate.setOptName(cretificateItem.getOptName());
            certificate.setCertOrigin("0");
            certificate.setCertificateNo(Code.valueOnUpdate("hege_no", trackHead.getTenantId(), trackHead.getBranchCode(), codeRuleService));
            certificate.setCheckName(tenantUser.getEmplName());
            certificate.setCheckTime(new Date());
            certificate.setNextOpt(CollectionUtil.isEmpty(nextItems) ? "/" : nextItems.get(0).getOptName());
            //未接收
            certificate.setIsPush("0");
            //下车间 车间编码
            certificate.setNextOptWork(nextOptWork);
            List<TrackCertificate> trackCertificates = new ArrayList<>();
            TrackCertificate trackCertificate = new TrackCertificate();
            trackCertificate.setThId(trackHead.getId());
            trackCertificates.add(trackCertificate);
            certificate.setTrackCertificates(trackCertificates);
            certificate.setType("0");
            saveCertificate(certificate);
        }
        return certificate;
    }

    //合格证前面的工序是否完工校验
    private void checkBefore(Certificate certificate) throws Exception {
        //判断当前工序之前有没有未完成的工序
        List<String> ids = certificate.getTrackCertificates().stream().map(TrackCertificate::getThId).collect(Collectors.toList());
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        queryWrapper.in("track_head_id", ids);
        List<TrackItem> list = trackItemService.list(queryWrapper);
        //去重操作(工序号+工序名 都一样认为重复)
        ArrayList<TrackItem> collect = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(trackItem -> trackItem.getOptName() + "-" + trackItem.getOptNo()))), ArrayList::new));
        List<TrackItem> current = collect.stream().filter(x -> x.getIsCurrent() == 1).collect(Collectors.toList());
        //当前工序的前工序 并且最终完成状态 is_final_complete 不为1 的
        List<TrackItem> before = collect.stream().filter(x -> x.getOptSequence() < current.get(0).getOptSequence() & !"1".equals(x.getIsFinalComplete())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(before)) {
            throw new Exception(before.get(0).getOptName() + " " + Certificate.FAILED_ON_COMPLETE);
        }
    }

    @Override
    public void certificateCheck(Certificate certificate) throws Exception {
        if (StringUtils.isNullOrEmpty(certificate.getCertificateNo())) {
            throw new Exception(Certificate.CERTIFICATE_NO_NULL_MESSAGE);
        }
        if (certificate.getTrackCertificates() == null || certificate.getTrackCertificates().size() == 0) {
            throw new Exception(Certificate.TRACK_NO_NULL_MESSAGE);
        }
        //合格证号码重复校验
        String certificateNo = Code.valueOnUpdate("hege_no", certificate.getTenantId(), certificate.getBranchCode(), codeRuleService);
        certificate.setCertificateNo(certificateNo);
        if (this.certNoExits(certificate.getCertificateNo(), certificate.getBranchCode())) {
            throw new Exception(Certificate.CERTIFICATE_NO_EXIST_MESSAGE);
        }
        List<TrackCertificate> trackCertificates = certificate.getTrackCertificates();
        if (certificate.getType().equals(CertTypeEnum.ITEM_CERT.getCode())) {
            //检查当前工序之前有没有未完成的工序
            this.checkBefore(certificate);
            //合格证是否已经开具过
            for (TrackCertificate trackCertificate : trackCertificates) {
                Boolean isCertRepeat = trackItemService.checkIsCertRepeat(certificate);
                if (isCertRepeat) {
                    throw new Exception(Certificate.CERTIFICATE_HAS_BEEN_ISSUED);
                }
            }
        } else if (certificate.getType().equals(CertTypeEnum.FINISH_CERT.getCode())) {
            //完工合格证
            //合格证是否已经开具过
            for (TrackCertificate trackCertificate : trackCertificates) {
                TrackHead trackHead = trackHeadService.getById(trackCertificate.getThId());
                if (StrUtil.isNotBlank(trackHead.getCertificateNo())) {
                    throw new Exception(Certificate.CERTIFICATE_HAS_BEEN_ISSUED);
                }
            }
        }
    }

    @Override
    public void saveCertificate(Certificate certificate) throws Exception {
        //1 合格证开具校验
        this.certificateCheck(certificate);
        //重写拼接产品编号
        certificate.setProductNoContinuous(Utils.productNoContinuous(certificate.getProductNo()));
        certificate.setTenantId(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getTenantId());
        certificate.setIsPush("0");
        //合格证来源 0：开出合格证 1：接收合格证
        certificate.setCertOrigin("0");
        certificate.setId(UUID.randomUUID().toString().replaceAll("-", ""));

        //2 更新跟单或工序对应的合格证编号
        certificate.getTrackCertificates().stream().forEach(track -> {
            if (certificate.getType().equals(CertTypeEnum.ITEM_CERT.getCode())) {
                //工序合格证
                trackItemService.linkToCertNew(track.getThId(), certificate);
            } else if (certificate.getType().equals(CertTypeEnum.FINISH_CERT.getCode())) {
                //完工合格证
                trackHeadService.linkToCert(track.getThId(), certificate.getCertificateNo());
                //更新跟单状态到 9 "已交"
                trackHeadService.trackHeadDelivery(track.getThId());
                //半成品 成品更新状态及合格证号
                TrackHead th = trackHeadService.getById(track.getThId());
                lineStoreService.updateCertNoByCertTrack(th);
            }
        });
        //3 保存关联关系
        if (certificate.getTrackCertificates().size() > 0) {
            trackCertificateService.save(certificate);
        }
        //4 根据合格证类型 执行交库、ERP工时推送、合格证交互池处理(不增加交互池了，都从合格证表查询即可)
        additionalBsns(certificate);
        //5 保存合格证
        this.saveOrUpdate(certificate);
    }


    /**
     * 根据工序合格证在对应车间开具跟单
     *
     * @param certificate ,trackItem
     */
    @Transactional(rollbackFor = Exception.class)
    public void autoCreateTrackHeadByCertificate(Certificate certificate) throws Exception {
        //查询合格证
        QueryWrapper<TrackCertificate> trackCertificateQueryWrapper = new QueryWrapper<>();
        trackCertificateQueryWrapper.eq("certificate_id", certificate.getId());
        List<TrackCertificate> list = trackCertificateService.list(trackCertificateQueryWrapper);
        //对应的跟单id
        String thId = list.get(0).getThId();
        //对应的跟单工序id
        String tiId = list.get(0).getTiId();
        //下车间branchCode
        String branchCode = certificate.getNextOptWork();
        //下车间tenantId
        String tenantId = baseServiceClient.queryTenantIdByBranchCode(branchCode).getTenantId();
        //创建跟单信息
        TrackHead trackHead = trackHeadService.getById(thId);
        TrackHead newTrackHead = new TrackHead();
        BeanUtil.copyProperties(trackHead, newTrackHead, new String[]{"id", "modifyTime", "modifyBy", "routerId", "classes", "status","trackNo"});
        //下车间跟单号
        String trackNo = Code.valueOnUpdate("track_no", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
        newTrackHead.setTrackNo(trackNo);
        newTrackHead.setBranchCode(branchCode);
        newTrackHead.setTenantId(tenantId);
        newTrackHead.setClasses("7"); //冶炼车间
        newTrackHead.setStatus("0"); //跟单状态设置为初始
        trackHeadService.save(newTrackHead);
        //保存到冶炼车间 铸件跟单的工艺信息（因为只匹配了冶炼车间的工艺的工艺路线，但是工艺的信息还是沿用之前铸钢车间的）
        if ("7".equals(newTrackHead.getClasses())) {
            String routerId = trackHead.getRouterId();
            List<Router> routers = baseServiceClient.getRouterByIdAndBranchCode(Arrays.asList(routerId + "_" + trackHead.getBranchCode())).getData();
            if (!CollectionUtil.isEmpty(routers)) {
                Router router = routers.get(0);
                TrackHeadCast trackHeadCast = new TrackHeadCast();
                BeanUtil.copyProperties(router, trackHeadCast, new String[]{"id", "branchCode", "tenantId", "remark"});
                trackHeadCast.setBranchCode(branchCode);
                trackHeadCast.setHeadId(newTrackHead.getId());
                trackHeadCast.setWeightMolten(new BigDecimal(router.getWeightMolten()));
                trackHeadCastService.save(trackHeadCast);
            }
        }
        //创建跟单产品信息flow
        List<TrackFlow> trackFlows = trackHeadFlowService.list(new QueryWrapper<TrackFlow>().eq("track_head_id", trackHead.getId()));
        for (TrackFlow trackFlow : trackFlows) {
            trackFlow.setId(null);
            trackFlow.setBranchCode(branchCode);
            trackFlow.setTenantId(tenantId);
            trackFlow.setTrackHeadId(newTrackHead.getId());
            trackFlow.setStatus("0");
        }
        trackHeadFlowService.saveBatch(trackFlows);
        //开工序合格证的工序信息
        TrackItem trackItem = trackItemService.getById(tiId);
        //根据开合格证的工序  判断新跟单要绑定的工艺
        Router router = getRouterByLastOptType(certificate, trackItem);
        //构造绑定工艺的跟单工序
        List<TrackItem> newTrackItems = new ArrayList<>();
        List<Sequence> sequences = baseServiceClient.listByBranchCodeAndRouterId(router.getId(), branchCode);
        for (int i = 0; i < sequences.size(); i++) {
            Sequence sequence = sequences.get(i);
            TrackItem newTrackItem = new TrackItem();
            newTrackItem.setOperatiponId(sequence.getOptId());
            newTrackItem.setOptId(sequence.getId());
            newTrackItem.setOptNo(sequence.getOpNo());
            newTrackItem.setOptSequence(sequence.getOptOrder());
            newTrackItem.setSequenceOrderBy(i + 1);
            newTrackItem.setOptVer(sequence.getVersionCode());
            newTrackItem.setOptName(sequence.getOptName());
            newTrackItem.setOptType(sequence.getOptType());
            newTrackItem.setIsAutoSchedule(Integer.parseInt(sequence.getIsAutoAssign()));
            newTrackItem.setOptParallelType(Integer.parseInt(sequence.getIsParallel()));
            newTrackItem.setIsExistQualityCheck(Integer.parseInt(sequence.getIsQualityCheck()));
            newTrackItem.setIsExistScheduleCheck(Integer.parseInt(sequence.getIsScheduleCheck()));
            newTrackItem.setIsDoing(0);
            newTrackItem.setIsCurrent(i == 0 ? 1 : 0);
            newTrackItem.setIsSchedule(0);
            newTrackItem.setBranchCode(branchCode);
            newTrackItem.setTenantId(tenantId);
            newTrackItem.setPriority(trackHead.getPriority());
            newTrackItems.add(newTrackItem);
        }
        //绑定工艺
        this.bindRouterInfo(newTrackHead, trackFlows, newTrackItems, router.getId(), router.getVersion());

        //合格证关联表下车间跟单赋值
        UpdateWrapper<TrackCertificate> trackCertificateUpdate = new UpdateWrapper<>();
        trackCertificateUpdate.eq("certificate_id", certificate.getId())
                .set("next_th_id", newTrackHead.getId());
        trackCertificateService.update(trackCertificateUpdate);

    }

    /**
     * 根据开完工合格证的工序的工序类型，来选择新开出的跟单需要绑定的工艺
     *
     * @param certificate
     * @param trackItem
     * @return
     */
    private Router getRouterByLastOptType(Certificate certificate, TrackItem trackItem) {
        if (OptTypeEnum.KX_OPERATION.getStateId().equals(trackItem.getOptType())) {
            List<Router> routers = baseServiceClient.find(null, null, null, "01", certificate.getNextOptWork(), null, null, null, null, Router.COMMON_ROUTER_TYPE).getData();
            if (ObjectUtil.isEmpty(routers)) {
                throw new GlobalException("冶炼车间没有铸件工艺，无法通过合格证进行推送！", ResultCode.FAILED);
            }
            return routers.get(0);
        }
        return null;
    }

    /**
     * 描述: 跟单绑定工艺
     *
     * @Author: renzewen
     * @Date: 2023/5/31 10:25
     **/
    public boolean bindRouterInfo(TrackHead trackHead, List<TrackFlow> flows, List<TrackItem> trackItems, String routerId, String routerVer) {
        //对工序数据处理
        trackHeadService.beforeSaveItemDeal(trackItems);
        //绑定
        if (!CollectionUtil.isEmpty(flows)) {
            for (TrackFlow flow : flows) {
                if (trackItems != null && trackItems.size() > 0) {
                    for (TrackItem item : trackItems) {
                        item.setId(UUID.randomUUID().toString().replace("-", ""));
                        item.setTrackHeadId(trackHead.getId());
                        item.setDrawingNo(trackHead.getDrawingNo());
                        item.setFlowId(flow.getId());
                        item.setProductNo(flow.getProductNo());
                        //可分配数量
                        item.setAssignableQty(flow.getNumber());
                        item.setNumber(flow.getNumber());
                        item.setIsSchedule(0);
                        item.setIsPrepare(0);
                        item.setIsNotarize(0);
                        //需要调度审核时展示
                        if (1 == item.getIsExistScheduleCheck()) {
                            item.setIsScheduleCompleteShow(1);
                        } else {
                            item.setIsScheduleCompleteShow(0);
                        }
                        if (trackHead.getStatus().equals("4")) {
                            item.setIsCurrent(0);
                        }
                    }
                    trackItemService.saveOrUpdateBatch(trackItems);
                }
            }
        }
        //跟单工艺属性赋值
        trackHead.setRouterId(routerId);
        trackHead.setRouterVer(routerVer);
        trackHeadService.updateById(trackHead);
        return true;
    }

    @Override
    public boolean savePushCert(Certificate certificate) {
        certificate.setTenantId(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getTenantId());
        certificate.setIsPush("0");
        //1 保存合格证
        if (!this.certNoExits(certificate.getCertificateNo(), certificate.getBranchCode())) {
            return this.save(certificate);
        } else {
            return false;
        }

    }

    private void additionalBsns(Certificate certificate) throws Exception {
        //完工合格证情况  交库    报工时
        if (certificate.getType().equals(CertTypeEnum.FINISH_CERT.getCode())) {
            certAdditionalBsns.doAdditionalBsns(certificate);
        }
    }

    /**
     * 更新合格证时，可能关联的跟单记录有变化：又有新增的跟单，之前关联的可能去掉，故需要一个较复杂的对比逻辑
     *
     * @param certificate
     * @param changeTrack 关联的跟单是否有变化
     */
    @Override
    public void updateCertificate(Certificate certificate, boolean changeTrack) throws Exception {
        if (certificate.getType().equals(CertTypeEnum.ITEM_CERT.getCode())) {
            //检查当前工序之前有没有未完成的工序
            this.checkBefore(certificate);
        }
        //重写拼接产品编号
        certificate.setProductNoContinuous(Utils.productNoContinuous(certificate.getProductNo()));
        //1、保存合格证
        this.updateById(certificate);
        if (changeTrack) {
            QueryWrapper<TrackCertificate> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("certificate_id", certificate.getId());
            List<TrackCertificate> result = trackCertificateService.list(queryWrapper);

            //2、找出修改合格证时新选择的跟单
            List<TrackCertificate> insert = certificate.getTrackCertificates().stream().filter(track -> {
                track.setCertificateType(certificate.getType());
                track.setCertificateId(certificate.getId());
                boolean isNotHave = true;
                for (TrackCertificate trackCertificate : result) {
                    if (trackCertificate.getThId().equals(track.getThId())) {
                        isNotHave = false;
                        break;
                    }
                }
                if (isNotHave) {
                    if (CertTypeEnum.ITEM_CERT.getCode().equals(certificate.getType())) {
                        trackItemService.linkToCertNew(track.getTiId(), certificate);
                    } else if (CertTypeEnum.FINISH_CERT.getCode().equals(certificate.getType())) {
                        trackHeadService.linkToCert(track.getThId(), certificate.getCertificateNo());
                        //半成品 成品入库
                        TrackHead th = trackHeadService.getById(track.getThId());
                        lineStoreService.updateCertNoByCertTrack(th);
                    }
                }
                return isNotHave;
            }).collect(Collectors.toList());
            trackCertificateService.saveBatch(insert);

            //3、找出修改合格证时取消选择的跟单
            List<String> delete = result.stream().filter(track -> {
                boolean isHave = false;
                for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {
                    if (trackCertificate.getThId().equals(track.getThId())) {
                        isHave = true;
                        break;
                    }
                }
                if (!isHave) {
                    if (CertTypeEnum.ITEM_CERT.getCode().equals(certificate.getType())) {
                        trackItemService.unLinkFromCert(track.getTiId());
                    } else if (CertTypeEnum.FINISH_CERT.getCode().equals(certificate.getType())) {
                        trackHeadService.unLinkFromCert(track.getThId());

                        //删除线边库对应半成品 对应合格证号
                        TrackHead th = trackHeadService.getById(track.getThId());
                        lineStoreService.reSetCertNoByTrackHead(th);
                    }
                }
                return !isHave;
            }).map(track -> track.getId()).collect(Collectors.toList());
            trackCertificateService.removeByIds(delete);
        }
    }

    @Override
    public void delCertificate(List<String> ids) throws Exception {

        //校验：接收的合格证，不能删除（没有必要删除）
        for (String id : ids) {
            Certificate cert = this.getById(id);
            if ("1".equals(cert.getCertOrigin())) {
                throw new GlobalException("接收合格证不能删除", ResultCode.FAILED);
            }
        }

        //处理合格证关联跟单
        QueryWrapper<TrackCertificate> queryWrapper = new QueryWrapper<TrackCertificate>();
        queryWrapper.in("certificate_id", ids);
        List<TrackCertificate> list = trackCertificateService.list(queryWrapper);
        list.stream().forEach(track -> {
            //对应跟单工序-合格证字段置空
            if (CertTypeEnum.ITEM_CERT.getCode().equals(track.getCertificateType())) {
                trackItemService.unLinkFromCert(track.getTiId());
                //对应跟单-合格证字段置空
            } else if (CertTypeEnum.FINISH_CERT.getCode().equals(track.getCertificateType())) {
                trackHeadService.unLinkFromCert(track.getThId());
                //清空所有该合格证号对应的成品入库信息中的合格证号
                lineStoreService.reSetCertNoByCertNo(this.getById(track.getCertificateId()).getCertificateNo());
            }
            //删除关系表
            Map map = new HashMap(16);
            map.put("certificate_id", track.getCertificateId());
            trackCertificateService.removeByMap(map);
        });

        //删除合格证
        this.removeByIds(ids);
    }

    @Override
    public boolean certNoExits(String certNo, String branchCode) throws NullPointerException {

        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("certificate_no", certNo);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_Id", Objects.requireNonNull(SecurityUtils.getCurrentUser()).getTenantId());

        int count = this.certificateMapper.selectCount(queryWrapper);

        return count > 0;
    }

    @Override
    public IPage<Certificate> selectNeedTransferCert(CertQueryDto queryDto) {

        //查询 未推送的（is_push =0 ） 接收单位是本单位，生产单位不是本单位的合格证
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pc.is_push", "0");
        queryWrapper.eq("pc.next_opt_work", queryDto.getBranchCode());
        queryWrapper.ne("pc.branch_code", queryDto.getBranchCode());
        queryWrapper.eq("pc.tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        if (!StringUtils.isNullOrEmpty(queryDto.getCertificateNo())) {
            queryWrapper.like("certificate_no", queryDto.getCertificateNo());
        }

        if (!StringUtils.isNullOrEmpty(queryDto.getDrawingNo())) {
            queryWrapper.like("drawing_no", queryDto.getDrawingNo());
        }

        return this.selectCertificate(new Page<>(queryDto.getPage(), queryDto.getLimit()), queryWrapper);

    }

    @Override
    public boolean certPushComplete(Certificate certificate) {

        Certificate cert = new Certificate();
        cert.setId(certificate.getId());
        cert.setIsPush("1");

        return this.updateById(cert);
    }

    @Override
    public void setPushHourComplete(Certificate certificate) {
        Certificate cert = new Certificate();
        cert.setId(certificate.getId());
        cert.setIsSendWorkHour("1");
        this.updateById(cert);
    }


    /**
     * 查询字典表：stockFrom字典项，把生产单位、下工序单位对应的中文名称填充进行数据中
     *
     * @param certificateIPage
     * @return
     */
    private IPage<Certificate> fillBranchName(IPage<Certificate> certificateIPage) {

        List<ItemParam> itemParamList = systemServiceClient.selectItemClass("stockFrom", "").getData();
        for (Certificate cert : certificateIPage.getRecords()) {
            for (ItemParam b : itemParamList) {
                if (b.getCode().equals(cert.getBranchCode())) {
                    cert.setBranchCodeName(b.getLabel());
                }
                if (b.getCode().equals(cert.getNextOptWork())) {
                    cert.setNextOptWorkName(b.getLabel());
                }
            }
        }
        return certificateIPage;
    }

}
