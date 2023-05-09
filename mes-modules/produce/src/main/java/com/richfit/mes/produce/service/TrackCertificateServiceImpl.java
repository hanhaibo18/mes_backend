package com.richfit.mes.produce.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.code.CertTypeEnum;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.dao.TrackCertificateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 王瑞
 * @Date: 2020/8/11
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TrackCertificateServiceImpl extends ServiceImpl<TrackCertificateMapper, TrackCertificate> implements TrackCertificateService {

    @Autowired
    TrackCertificateMapper trackCertificateMapper;

    @Autowired
    public TrackItemService trackItemService;

    @Override
    public void save(Certificate certificate) {
        List<TrackCertificate> trackCertificateList = new ArrayList<>();
        for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {
            if (certificate.getType().equals(CertTypeEnum.ITEM_CERT.getCode())) {
                //工序信息
                List<TrackItem> trackItemList = trackItemService.queryItemByThId(trackCertificate.getThId());
                for (TrackItem trackItem : trackItemList) {
                    if (certificate.getOptNo().equals(trackItem.getOptNo())) {
                        TrackCertificate tc = new TrackCertificate();
                        tc.setCertificateId(certificate.getId());
                        tc.setCertificateType(certificate.getType());
                        tc.setThId(trackCertificate.getThId());
                        tc.setTiId(trackItem.getId());
                        trackCertificateList.add(tc);
                    }
                }
            } else if (certificate.getType().equals(CertTypeEnum.FINISH_CERT.getCode())) {
                TrackCertificate tc = new TrackCertificate();
                tc.setCertificateId(certificate.getId());
                tc.setCertificateType(certificate.getType());
                tc.setThId(trackCertificate.getThId());
                trackCertificateList.add(tc);
            }
        }
        //批量保存
        this.saveBatch(trackCertificateList);
    }
}
