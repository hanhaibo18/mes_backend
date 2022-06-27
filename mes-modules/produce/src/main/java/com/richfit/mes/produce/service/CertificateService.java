package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Certificate;

import java.util.List;

public interface CertificateService extends IService<Certificate> {

    IPage<Certificate> selectCertificate(Page<Certificate> page, QueryWrapper<Certificate> query);

    public boolean saveCertificate(Certificate certificate) throws Exception;

    public void updateCertificate(Certificate certificate, boolean changeTrack) throws Exception;

    public void delCertificate(List<String> ids) throws Exception;

    boolean certNoExits(String certificateNo, String branchCode);
}
