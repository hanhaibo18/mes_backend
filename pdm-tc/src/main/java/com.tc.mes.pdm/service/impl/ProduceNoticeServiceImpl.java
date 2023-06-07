package com.tc.mes.pdm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tc.mes.pdm.entity.SaleProduceNoticeDto;
import com.tc.mes.pdm.entity.SaleProductionSchedulingDto;
import com.tc.mes.pdm.entity.domain.ProduceNotice;
import com.tc.mes.pdm.mapper.ProduceNoticeMapper;
import com.tc.mes.pdm.service.ProduceNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProduceNoticeServiceImpl extends ServiceImpl<ProduceNoticeMapper, ProduceNotice>
        implements ProduceNoticeService {

    @Autowired
    private ProduceNoticeService produceNoticeService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatchNotice(List<SaleProductionSchedulingDto> schedulingDtoList) {
        List<SaleProduceNoticeDto> dtoList = convert(schedulingDtoList);
        List<ProduceNotice> produceNoticeList = new ArrayList<>();
        dtoList.forEach(e -> {
            ProduceNotice produceNotice = new ProduceNotice();
            BeanUtils.copyProperties(e, produceNotice);
            produceNoticeList.add(produceNotice);
        });
        boolean flag = produceNoticeService.saveBatch(produceNoticeList);
        return flag ? true: false;
    }


    private List<SaleProduceNoticeDto> convert(List<SaleProductionSchedulingDto> schedulingDtoList) {
        List<SaleProduceNoticeDto> noticeDtoList = new ArrayList<>();
        for (SaleProductionSchedulingDto schedulingDto: schedulingDtoList) {
            SaleProduceNoticeDto noticeDto = new SaleProduceNoticeDto();
            noticeDto.setProductionOrder(schedulingDto.getCode());
            noticeDto.setUserUnit(schedulingDto.getName());
            noticeDto.setWorkNo(schedulingDto.getTrackNo());
            noticeDto.setProduceName(schedulingDto.getProductName());
            noticeDto.setQuantity(schedulingDto.getNumber());
            noticeDto.setDeliveryDate(schedulingDto.getDeliveryTime());
            noticeDto.setPreviewUrl(schedulingDto.getRelationId());
            noticeDto.setSalesSchedulingDate(schedulingDto.getCreateTime());
            noticeDto.setMaterialNo(schedulingDto.getMaterialCode());
            noticeDto.setMaterialName(schedulingDto.getErpName());
            noticeDto.setDrawingNo(schedulingDto.getDrawNo());
            noticeDtoList.add(noticeDto);
        }
        return noticeDtoList;
    }

}
