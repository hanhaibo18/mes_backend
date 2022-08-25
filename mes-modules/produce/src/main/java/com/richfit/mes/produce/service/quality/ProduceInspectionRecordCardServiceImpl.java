package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCardContent;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardContentMapper;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardMapper;
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

    @Override
    public void saveProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        this.save(produceInspectionRecordCard);
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = produceInspectionRecordCard.getProduceInspectionRecordCardContentList();
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
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = produceInspectionRecordCard.getProduceInspectionRecordCardContentList();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("flow_id", produceInspectionRecordCard.getId());
        produceInspectionRecordCardContentMapper.delete(queryWrapper);
        for (ProduceInspectionRecordCardContent produceInspectionRecordCardContent : produceInspectionRecordCardContentList) {
            produceInspectionRecordCardContent.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            produceInspectionRecordCardContent.setFlowId(produceInspectionRecordCard.getId());
            produceInspectionRecordCardContent.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            produceInspectionRecordCardContentMapper.insert(produceInspectionRecordCardContent);
        }
    }
}
