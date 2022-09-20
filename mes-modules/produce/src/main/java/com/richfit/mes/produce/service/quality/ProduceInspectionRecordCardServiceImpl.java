package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.RouterCheck;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardContentMapper;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022.8.25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProduceInspectionRecordCardServiceImpl extends ServiceImpl<ProduceInspectionRecordCardMapper, ProduceInspectionRecordCard> implements ProduceInspectionRecordCardService {

    @Autowired
    public ProduceInspectionRecordCardContentMapper produceInspectionRecordCardContentMapper;

    @Autowired
    public TrackHeadService trackHeadService;

    @Autowired
    public TrackHeadFlowService trackHeadFlowService;

    @Autowired
    public TrackCheckService trackCheckService;

    @Autowired
    public TrackCheckDetailService trackCheckDetailService;

    @Autowired
    public TrackItemService trackItemService;


    @Resource
    private BaseServiceClient baseServiceClient;

    @Override
    public void saveProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        this.saveOrUpdate(produceInspectionRecordCard);
    }

    @Override
    public void updateTrackCheckDetail(ProduceInspectionRecordCardContent produceInspectionRecordCardContent) {
        TrackFlow trackFlow = trackHeadFlowService.getById(produceInspectionRecordCardContent.getFlowId());
        trackFlow.setIsExamineCardData(TrackFlow.EXAMINE_CARD_DATA_XG);
        trackHeadFlowService.updateById(trackFlow);

        TrackCheckDetail trackCheckDetail = trackCheckDetailService.getById(produceInspectionRecordCardContent.getId());
        trackCheckDetail.setValue(produceInspectionRecordCardContent.getInspectionResult());
        trackCheckDetail.setResult(Integer.parseInt(produceInspectionRecordCardContent.getInspectionQualified()));
        trackCheckDetailService.updateById(trackCheckDetail);
    }

    @Override
    public void updateProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        this.updateById(produceInspectionRecordCard);
    }

    @Override
    public ProduceInspectionRecordCard selectProduceInspectionRecordCard(String flowId) {
        ProduceInspectionRecordCard produceInspectionRecordCard = this.getById(flowId);
        if (produceInspectionRecordCard == null) {
            produceInspectionRecordCard.setIsSave(ProduceInspectionRecordCard.PRODUCE_INSPECTION_RECORD_CARD_SAVE_N);
        }

        //质量检测卡基本信息查询
        TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
        TrackHead trackHead = trackHeadService.getById(trackFlow.getTrackHeadId());
        trackHead.setFlowId(flowId);
        trackHead.setIsCardData(trackFlow.getIsCardData());
        trackHead.setProductNo(trackFlow.getProductNo());
        produceInspectionRecordCard = new ProduceInspectionRecordCard(trackHead);

        //记录检验卡明细
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = new ArrayList<>();
        //获取工序质检信息
        QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
        queryWrapperTrackItem.eq("flow_id", flowId);
        queryWrapperTrackItem.orderByAsc("opt_sequence");
        List<TrackItem> trackItemList = trackItemService.list(queryWrapperTrackItem);
        //质检信息
        QueryWrapper<TrackCheck> queryWrapperTrackCheck = new QueryWrapper<>();
        queryWrapperTrackCheck.eq("flow_id", flowId);
        List<TrackCheck> trackCheckList = trackCheckService.list(queryWrapperTrackCheck);
        //质检明细
        QueryWrapper<TrackCheckDetail> queryWrapperTrackCheckDetail = new QueryWrapper<>();
        queryWrapperTrackCheckDetail.eq("flow_id", flowId);
        List<TrackCheckDetail> trackCheckDetailList = trackCheckDetailService.list(queryWrapperTrackCheckDetail);
        //质检信息数据重组
        for (TrackCheck trackCheck : trackCheckList) {
            List<TrackCheckDetail> list = new ArrayList<>();
            for (TrackCheckDetail trackCheckDetail : trackCheckDetailList) {
                if (trackCheck.getId().equals(trackCheckDetail.getTrackCheckId())) {
                    CommonResult<RouterCheck> result = baseServiceClient.routerCheckSelectById(trackCheckDetail.getCheckId());
                    trackCheckDetail.setRouterCheck(result.getData());
                    list.add(trackCheckDetail);
                }
            }
            trackCheck.setCheckDetailsList(list);
        }
        //获取质检信息与其他信息（质检信息、工序合格证、探伤记录）
        for (TrackItem trackItem : trackItemList) {
            produceInspectionRecordCardContentList.addAll(ProduceInspectionRecordCardContent.listByTrackItem(trackItem, trackCheckList));
        }

        //材料追溯（炉号）
        produceInspectionRecordCardContentList.addAll(ProduceInspectionRecordCardContent.listByTrackHead(produceInspectionRecordCard));
        int i = 1;
        for (ProduceInspectionRecordCardContent p : produceInspectionRecordCardContentList) {
            p.setInspectionNo(i++ + "");
        }

        //数据整合
        produceInspectionRecordCard.setProduceInspectionRecordCardContentList(produceInspectionRecordCardContentList);
        return produceInspectionRecordCard;
    }
}
