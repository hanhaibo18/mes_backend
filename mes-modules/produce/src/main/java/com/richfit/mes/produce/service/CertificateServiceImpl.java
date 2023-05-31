package com.richfit.mes.produce.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.code.CertTypeEnum;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.entity.CertQueryDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private StockRecordService stockRecordService;

    @Autowired
    private TrackCertificateService trackCertificateService;

    @Autowired
    private CertAdditionalBsns certAdditionalBsns;

    @Autowired
    private CodeRuleService codeRuleService;

    @Autowired
    private SystemServiceClient systemServiceClient;

    @Override
    public IPage<Certificate> selectCertificate(Page<Certificate> page, QueryWrapper<Certificate> query) {
        return fillBranchName(certificateMapper.selectCertificate(page, query));
    }

    @Override
    public List<TrackHead> selectItemTrack(TrackHead trackHead) {
        return certificateMapper.selectItemTrack(trackHead);
    }

    @Override
    public boolean autoCertificate(TrackHead trackHead) throws Exception {
        //装配车间
        if (!"2".equals(trackHead.getClasses()) && !"BOMCO_BY_ZPG1".equals(trackHead.getTemplateCode())) {
            return true;
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
        return saveCertificate(certificate);
    }

    /**
     * 热工根据跟单开工序合格证
     * @param thId
     * @param nextOptWork 下车间编码
     * @param flowId
     * @return
     * @throws Exception
     */
    @Override
    public boolean heatAutoCertificate(String thId,String nextOptWork,String flowId) throws Exception {
        //开具合格证的跟单
        TrackHead trackHead = trackHeadService.getById(thId);
        //登陆人信息
        TenantUserVo tenantUser = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId()).getData();
        Certificate certificate = new Certificate();
        //跟单属性赋值
        BeanUtil.copyProperties(trackHead, certificate, new String[]{"id", "createTime", "modifyTime", "modifyBy"});
        //根据分流id查询当前工序
        QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
        trackItemQueryWrapper.eq("flow_id",flowId)
                .eq("is_current",1)
                .orderByAsc("opt_sequence");
        List<TrackItem> trackItems = trackItemService.list(trackItemQueryWrapper);
        if(!CollectionUtil.isEmpty(trackItems)){
            //本工序（开合格证的工序）
            TrackItem cretificateItem = trackItems.get(0);
            //根据工序顺序查询下工序
            QueryWrapper<TrackItem> trackItemQw = new QueryWrapper<>();
            trackItemQw.eq("opt_sequence",cretificateItem.getNextOptSequence())
                    .eq("flow_id",cretificateItem.getFlowId());
            List<TrackItem> nextItems = trackItemService.list(trackItemQw);
            certificate.setOptSequence(cretificateItem.getOptSequence());
            certificate.setOptNo(cretificateItem.getOptNo());
            certificate.setOptName(cretificateItem.getOptName());
            certificate.setCertOrigin("0");
            certificate.setCertificateNo(Code.valueOnUpdate("hege_no", trackHead.getTenantId(), trackHead.getBranchCode(), codeRuleService));
            certificate.setCheckName(tenantUser.getEmplName());
            certificate.setCheckTime(new Date());
            certificate.setNextOpt(CollectionUtil.isEmpty(nextItems)?"/":nextItems.get(0).getOptName());
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

            return saveCertificate(certificate);
        }
        return true;
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
    public boolean saveCertificate(Certificate certificate) throws Exception {
        //1 合格证开具校验
        this.certificateCheck(certificate);
        //重写拼接产品编号
        certificate.setProductNoContinuous(Utils.productNoContinuous(certificate.getProductNo()));
        certificate.setTenantId(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getTenantId());
        certificate.setIsPush("0");
        //合格证来源 0：开出合格证 1：接收合格证
        certificate.setCertOrigin("0");

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
        this.save(certificate);
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
