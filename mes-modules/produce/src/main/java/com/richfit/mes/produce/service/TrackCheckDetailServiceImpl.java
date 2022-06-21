package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackCheckDetail;
import com.richfit.mes.produce.dao.TrackCheckDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mafeng
 * @Description 质检明细
 */
@Service
public class TrackCheckDetailServiceImpl extends ServiceImpl<TrackCheckDetailMapper, TrackCheckDetail> implements TrackCheckDetailService {

    @Autowired
    private TrackCheckDetailMapper trackCheckMapper;

    /**
     * 描述: 根据工序id查询质检的工序项目列表
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Override
    public List<TrackCheckDetail> selectByTiId(String tiId) {
        QueryWrapper<TrackCheckDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", tiId);
        return trackCheckMapper.selectList(queryWrapper);
    }
}
