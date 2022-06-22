package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.code.CertTypeEnum;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CertificateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: 王瑞
 * @Date: 2020/8/11
 */
@Slf4j
@Service
@Transactional
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

    @Override
    public IPage<Certificate> selectCertificate(Page<Certificate> page, QueryWrapper<Certificate> query) {
        return certificateMapper.selectCertificate(page, query);
    }

    @Override
    public boolean saveCertificate(Certificate certificate) throws Exception {
        certificate.setTenantId(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getTenantId());

        //1 保存合格证
        boolean bool = this.save(certificate);

        if (bool) {

            //2 更新跟单或工序对应的合格证编号
            certificate.getTrackCertificates().stream().forEach(track -> {
                //工序合格证
                if (certificate.getType().equals(CertTypeEnum.ITEM_CERT.getCode())) {
                    trackItemService.linkToCert(track.getTiId(), certificate.getCertificateNo());
                    //完工合格证
                } else if (certificate.getType().equals(CertTypeEnum.FINISH_CERT.getCode())) {
                    trackHeadService.linkToCert(track.getThId(), certificate.getCertificateNo());

                    //半成品 成品更新状态及合格证号
                    TrackHead th = trackHeadService.getById(track.getThId());
                    lineStoreService.updateCertNoByCertTrack(th);
                }
                track.setCertificateType(certificate.getType());
                track.setCertificateId(certificate.getId());
            });
            //3 保存关联关系
            trackCertificateService.saveBatch(certificate.getTrackCertificates());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateCertificate(Certificate certificate, boolean changeTrack) throws Exception {
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
                    if (trackCertificate.getTiId().equals(track.getTiId())
                            && trackCertificate.getThId().equals(track.getThId())) {
                        isNotHave = false;
                        break;
                    }
                }
                if (isNotHave) {
                    if (CertTypeEnum.ITEM_CERT.getCode().equals(certificate.getType())) {
                        trackItemService.linkToCert(track.getTiId(), certificate.getCertificateNo());
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
                    if (trackCertificate.getTiId().equals(track.getTiId())
                            && trackCertificate.getThId().equals(track.getThId())) {
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
                lineStoreService.reSetCertNoByTrackHead(this.getById(track.getCertificateId()).getCertificateNo());
            }

            //删除关系表
            Map map = new HashMap();
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

        Certificate cert = this.certificateMapper.selectOne(queryWrapper);

        return cert != null;
    }

}
