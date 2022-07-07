package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.common.model.produce.TrackCheckDetail;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.produce.dao.TrackCheckAttachmentMapper;
import com.richfit.mes.produce.dao.TrackCheckDetailMapper;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author mafeng
 * @Description 质检明细
 */
@Service
public class TrackCheckDetailServiceImpl extends ServiceImpl<TrackCheckDetailMapper, TrackCheckDetail> implements TrackCheckDetailService {

    @Autowired
    private TrackCheckDetailMapper trackCheckMapper;

    @Resource
    private TrackCheckAttachmentMapper trackCheckAttachmentMapper;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private TrackCheckService trackCheckService;

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

    @Override
    public TrackCheck queryQualityTestingResult(String tiId) {
        TrackCheck trackCheck = new TrackCheck();
        QueryWrapper<TrackCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", tiId);
        queryWrapper.orderByDesc("modify_time");
        trackCheck = trackCheckService.getOne(queryWrapper);
        QueryWrapper<TrackCheckDetail> queryWrapperDetail = new QueryWrapper<>();
        queryWrapperDetail.eq("ti_id", tiId);
        queryWrapper.orderByDesc("modify_time");
        List<TrackCheckDetail> trackCheckDetails = this.list(queryWrapperDetail);
        trackCheck.setCheckDetailsList(trackCheckDetails);
        return trackCheck;
    }

    @Override
    public List<Attachment> getAttachmentListByTiId(String tiId) {
        //查询文件
        List<String> fileIdList = trackCheckAttachmentMapper.queryFileIdList(tiId);
        if (fileIdList.isEmpty()) {
            return Collections.emptyList();
        }
        return systemServiceClient.selectAttachmentsList(fileIdList);
    }
}
