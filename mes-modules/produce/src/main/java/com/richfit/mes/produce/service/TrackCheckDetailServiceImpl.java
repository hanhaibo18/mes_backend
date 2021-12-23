package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackCheckDetail;
import com.richfit.mes.produce.dao.TrackCheckDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mafeng
 * @Description 质检明细
 */
@Service
public class TrackCheckDetailServiceImpl extends ServiceImpl<TrackCheckDetailMapper, TrackCheckDetail> implements TrackCheckDetailService{

    @Autowired
    private TrackCheckDetailMapper trackCheckMapper;

}
