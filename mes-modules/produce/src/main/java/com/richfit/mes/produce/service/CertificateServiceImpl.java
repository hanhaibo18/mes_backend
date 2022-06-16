package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.code.CertTypeEnum;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CertificateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        certificate.setTenantId(SecurityUtils.getCurrentUser().getTenantId());

        //1 保存合格证
        boolean bool = this.save(certificate);

        if (bool) {

            //2 更新跟单或工序对应的合格证编号
            certificate.getTrackCertificates().stream().forEach(track -> {
                //工序合格证
                if (certificate.getType().equals(CertTypeEnum.ITEM_CERT.getCode())) {
                    TrackItem trackItem = new TrackItem();
                    trackItem.setId(track.getTiId());
                    trackItem.setCertificateNo(certificate.getCertificateNo());
                    trackItemService.updateById(trackItem);

                    //完工合格证
                } else if (certificate.getType().equals(CertTypeEnum.FINISH_CERT.getCode())) {
                    TrackHead trackHead = new TrackHead();
                    trackHead.setId(track.getThId());
                    trackHead.setCertificateNo(certificate.getCertificateNo());
                    trackHeadService.updateById(trackHead);

                    TrackHead th = trackHeadService.getById(track.getThId());

                    //TODO  线边库成品入库
                    QueryWrapper<LineStore> wrapper = new QueryWrapper<>();
                    wrapper.eq("workblank_no", th.getProductNo());

                }
                track.setCertificateType(certificate.getType());
                track.setCertificateId(certificate.getId());
            });
            trackCertificateService.saveBatch(certificate.getTrackCertificates());
            return true;
        } else {
            return false;
        }
    }


}
