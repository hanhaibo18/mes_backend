package com.kld.mes.plm.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kld.mes.plm.entity.domain.ProduceNotice;
import com.kld.mes.plm.entity.dto.SaleProductionSchedulingDto;
import com.kld.mes.plm.entity.request.SaleProductionSchedulingRequest;
import com.kld.mes.plm.entity.vo.SaleProduceNoticeVo;
import com.kld.mes.plm.mapper.ProduceNoticeMapper;
import com.kld.mes.plm.service.ProduceNoticeService;
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
    public boolean saveBatchNotice(List<SaleProductionSchedulingRequest> schedulingList) {
        List<SaleProductionSchedulingDto> dtoList = convertDto(schedulingList);
        List<SaleProduceNoticeVo> voList = convertVo(dtoList);
        List<ProduceNotice> produceNoticeList = new ArrayList<>();
        voList.forEach(e -> {
            ProduceNotice produceNotice = new ProduceNotice();
            BeanUtils.copyProperties(e, produceNotice);
            produceNoticeList.add(produceNotice);
        });
        boolean flag = produceNoticeService.saveBatch(produceNoticeList);
        return flag ? true: false;
    }

    private List<SaleProductionSchedulingDto> convertDto(List<SaleProductionSchedulingRequest> requestList) {
        List<SaleProductionSchedulingDto> schedulingDtoList = new ArrayList<>();
        requestList.forEach(e -> {
            SaleProductionSchedulingDto schedulingDto = new SaleProductionSchedulingDto();
            schedulingDto.setCode(e.getCode());
            schedulingDto.setCustomer(e.getCustomer());
            schedulingDto.setTrackNo(e.getTrack_no());
            schedulingDto.setName(e.getName());
            schedulingDto.setNumber(e.getNumber());
            schedulingDto.setDeliveryTime(e.getDelivery_time());
            schedulingDto.setRelationId(e.getRelation_id());
            schedulingDto.setCreateTime(e.getCreate_time());
            schedulingDto.setMaterialCode(e.getMaterial_code());
            schedulingDto.setErpName(e.getErp_name());
            schedulingDto.setDrawNo(e.getDraw_no());
            schedulingDtoList.add(schedulingDto);
        });
        return schedulingDtoList;
    }

    private List<SaleProduceNoticeVo> convertVo(List<SaleProductionSchedulingDto> schedulingDtoList) {
        List<SaleProduceNoticeVo> noticeDtoList = new ArrayList<>();
        for (SaleProductionSchedulingDto schedulingDto: schedulingDtoList) {
            SaleProduceNoticeVo noticeVo = new SaleProduceNoticeVo();
            noticeVo.setProductionOrder(schedulingDto.getCode());
            noticeVo.setUserUnit(schedulingDto.getCustomer());
            noticeVo.setWorkNo(schedulingDto.getTrackNo());
            noticeVo.setProduceName(schedulingDto.getName());
            noticeVo.setQuantity(schedulingDto.getNumber());
            noticeVo.setDeliveryDate(schedulingDto.getDeliveryTime());
            noticeVo.setPreviewUrl(schedulingDto.getRelationId());
            noticeVo.setSalesSchedulingDate(schedulingDto.getCreateTime());
            noticeVo.setMaterialNo(schedulingDto.getMaterialCode());
            noticeVo.setMaterialName(schedulingDto.getErpName());
            noticeVo.setDrawingNo(schedulingDto.getDrawNo());
            noticeDtoList.add(noticeVo);
        }
        return noticeDtoList;
    }

}
