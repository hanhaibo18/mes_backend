package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 王瑞
 * @Description 合格证Mapper
 */
@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {
    IPage<Certificate> selectCertificate(IPage<Certificate> page, @Param(Constants.WRAPPER) Wrapper<Certificate> query);

    List<TrackHead> selectItemTrack(@Param("trackHead") TrackHead trackHead);
}
