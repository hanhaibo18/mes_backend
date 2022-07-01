package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackCheckDetail;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.produce.dao.TrackCheckAttachmentMapper;
import com.richfit.mes.produce.dao.TrackCheckDetailMapper;
import com.richfit.mes.produce.entity.QueryQualityTestingResultVo;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public QueryQualityTestingResultVo queryQualityTestingResult(String tiId) {
        QueryQualityTestingResultVo qualityTestingResult = new QueryQualityTestingResultVo();
        QueryWrapper<TrackCheckDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", tiId);
        List<TrackCheckDetail> trackCheckDetails = this.list(queryWrapper);
        qualityTestingResult.setTrackCheckDetailList(trackCheckDetails);
        List<String> fileIdList = trackCheckAttachmentMapper.queryFileIdList(tiId);
        if (!fileIdList.isEmpty()) {
            List<Attachment> attachmentList = systemServiceClient.selectAttachmentsList(fileIdList);
            if (!attachmentList.isEmpty()) {
                qualityTestingResult.setAttachmentList(attachmentList);
            }
        }
        return qualityTestingResult;
    }
}
