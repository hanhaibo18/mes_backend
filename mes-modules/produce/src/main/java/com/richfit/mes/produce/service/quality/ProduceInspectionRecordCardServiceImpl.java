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
import java.util.UUID;

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
        this.save(produceInspectionRecordCard);
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = produceInspectionRecordCard.getProduceInspectionRecordCardContentList();
        //保存检测明细信息
        for (ProduceInspectionRecordCardContent produceInspectionRecordCardContent : produceInspectionRecordCardContentList) {
            produceInspectionRecordCardContent.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            produceInspectionRecordCardContent.setFlowId(produceInspectionRecordCard.getId());
            produceInspectionRecordCardContent.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            produceInspectionRecordCardContentMapper.insert(produceInspectionRecordCardContent);
        }
    }

    @Override
    public void updateProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        this.updateById(produceInspectionRecordCard);
        //删除原明细
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = produceInspectionRecordCard.getProduceInspectionRecordCardContentList();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("flow_id", produceInspectionRecordCard.getId());
        produceInspectionRecordCardContentMapper.delete(queryWrapper);
        //保存新检测明细信息
        for (ProduceInspectionRecordCardContent produceInspectionRecordCardContent : produceInspectionRecordCardContentList) {
            produceInspectionRecordCardContent.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            produceInspectionRecordCardContent.setFlowId(produceInspectionRecordCard.getId());
            produceInspectionRecordCardContent.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            produceInspectionRecordCardContentMapper.insert(produceInspectionRecordCardContent);
        }
    }

    @Override
    public ProduceInspectionRecordCard selectProduceInspectionRecordCard(String flowId) {
        ProduceInspectionRecordCard produceInspectionRecordCard = this.getById(flowId);
        //如果查到说明已保存过，直接返回，否则需要从原工厂进行查询信息
        if (produceInspectionRecordCard == null) {
            TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
            TrackHead trackHead = trackHeadService.getById(trackFlow.getTrackHeadId());
            trackHead.setFlowId(flowId);
            trackHead.setIsCardData(trackFlow.getIsCardData());
            produceInspectionRecordCard = new ProduceInspectionRecordCard(trackHead);
        }
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
        //获取质检信息与其他信息（工序合格证、探伤记录）
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
