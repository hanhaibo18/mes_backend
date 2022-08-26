package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCardContent;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardContentMapper;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardMapper;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import com.richfit.mes.produce.service.TrackHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author zhiqiang.lu
 * @date 2022.8.25
 */
@Service
@Transactional
public class ProduceInspectionRecordCardServiceImpl extends ServiceImpl<ProduceInspectionRecordCardMapper, ProduceInspectionRecordCard> implements ProduceInspectionRecordCardService {

    @Autowired
    public ProduceInspectionRecordCardContentMapper produceInspectionRecordCardContentMapper;

    @Autowired
    public TrackHeadService trackHeadService;

    @Autowired
    public TrackHeadFlowService trackHeadFlowService;

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
        if (produceInspectionRecordCard != null) {
            return produceInspectionRecordCard;
        } else {
            //从跟单中过去数据
            TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
            TrackHead trackHead = trackHeadService.getById(trackFlow.getTrackHeadId());
        }
        return null;
    }
}
