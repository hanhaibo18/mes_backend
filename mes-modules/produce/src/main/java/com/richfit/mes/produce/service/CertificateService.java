package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.CertQueryDto;

import java.util.List;

public interface CertificateService extends IService<Certificate> {

    IPage<Certificate> selectCertificate(Page<Certificate> page, QueryWrapper<Certificate> query);

    List<TrackHead> selectItemTrack(TrackHead trackHead);

    public boolean autoCertificate(TrackHead trackHead) throws Exception;

    public boolean saveCertificate(Certificate certificate) throws Exception;

    public boolean savePushCert(Certificate certificate) throws Exception;

    public void updateCertificate(Certificate certificate, boolean changeTrack) throws Exception;

    public void delCertificate(List<String> ids) throws Exception;

    public boolean certNoExits(String certificateNo, String branchCode);

    /**
     * 查询待接收的合格证列表
     *
     * @param queryDto
     * @return
     */
    IPage<Certificate> selectNeedTransferCert(CertQueryDto queryDto);

    /**
     * 合格证转车间的或完工交库的，对方车间接收后，修改合格证状态
     *
     * @param certificate
     * @return
     */
    public boolean certPushComplete(Certificate certificate);

    /**
     * 合格证完工的，推送工时给ERP后，改状态字段为已推送工时
     *
     * @param certificate
     * @return
     */
    void setPushHourComplete(Certificate certificate);
}
