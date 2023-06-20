package com.pdm.mes.schedule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdm.mes.schedule.entity.domain.ProduceMesPdmAttachment;
import com.pdm.mes.schedule.entity.domain.ProduceNotice;
import com.pdm.mes.schedule.entity.dto.SaleProduceAttachmentDto;
import com.pdm.mes.schedule.entity.dto.SaleProductionSchedulingDto;
import com.pdm.mes.schedule.entity.request.SaleProductionSchedulingRequest;
import com.pdm.mes.schedule.entity.vo.SaleProduceNoticeVo;
import com.pdm.mes.schedule.mapper.ProduceNoticeMapper;
import com.pdm.mes.schedule.service.ProduceMesPdmAttachmentService;
import com.pdm.mes.schedule.service.ProduceNoticeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
* @author llh
* @description 针对表【produce_notice】的数据库操作Service实现
* @createDate 2023-06-12 14:01:16
*/
@Service
public class ProduceNoticeServiceImpl extends ServiceImpl<ProduceNoticeMapper, ProduceNotice>
    implements ProduceNoticeService {

    @Autowired
    private ProduceNoticeService produceNoticeService;

    @Autowired
    private ProduceMesPdmAttachmentService produceMesPdmAttachmentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatchNotice(List<SaleProductionSchedulingRequest> schedulingList) {
        if (CollectionUtils.isEmpty(schedulingList)) {
            return false;
        }
        SaleProduceAttachmentDto convert = convert(schedulingList);
        List<ProduceMesPdmAttachment> attachmentList = convert.getAttachmentList();
        if (!CollectionUtils.isEmpty(attachmentList)) {
            produceMesPdmAttachmentService.saveBatch(attachmentList);
        }

        List<SaleProductionSchedulingDto> dtoList = convert.getSchedulingDtoList();
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

    private SaleProduceAttachmentDto convert(List<SaleProductionSchedulingRequest> requestList) {
        List<SaleProductionSchedulingDto> schedulingDtoList = new ArrayList<>(requestList.size());
        List<ProduceMesPdmAttachment> attachmentList = new ArrayList<>();
        requestList.forEach(e -> {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            SaleProductionSchedulingDto schedulingDto = new SaleProductionSchedulingDto();
            schedulingDto.setId(uuid);
            schedulingDto.setCode(e.getCode());
            schedulingDto.setCustomer(e.getCustomer());
            schedulingDto.setTrackNo(e.getTrack_no());
            schedulingDto.setName(e.getName());
            schedulingDto.setNumber(e.getNumber());
            schedulingDto.setDeliveryTime(e.getDelivery_time());
            schedulingDto.setCreateTime(e.getCreate_time());
            schedulingDto.setMaterialCode(e.getMaterial_code());
            schedulingDto.setErpName(e.getErp_name());
            schedulingDto.setDrawNo(e.getDraw_no());
            schedulingDtoList.add(schedulingDto);
            e.getRelation_id().forEach(f -> {
                ProduceMesPdmAttachment attachment = new ProduceMesPdmAttachment();
                attachment.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                attachment.setTableId(uuid);
                attachment.setFileUrl(f);
                attachmentList.add(attachment);
            });
        });
        SaleProduceAttachmentDto saleProduceAttachmentDto = new SaleProduceAttachmentDto();
        saleProduceAttachmentDto.setAttachmentList(attachmentList);
        saleProduceAttachmentDto.setSchedulingDtoList(schedulingDtoList);
        return saleProduceAttachmentDto;
    }

    private List<SaleProduceNoticeVo> convertVo(List<SaleProductionSchedulingDto> schedulingDtoList) {
        List<SaleProduceNoticeVo> noticeDtoList = new ArrayList<>();
        for (SaleProductionSchedulingDto schedulingDto: schedulingDtoList) {
            SaleProduceNoticeVo noticeVo = new SaleProduceNoticeVo();
            noticeVo.setId(schedulingDto.getId());
            noticeVo.setProductionOrder(schedulingDto.getCode());
            noticeVo.setUserUnit(schedulingDto.getCustomer());
            noticeVo.setWorkNo(schedulingDto.getTrackNo());
            noticeVo.setProduceName(schedulingDto.getName());
            noticeVo.setQuantity(schedulingDto.getNumber());
            noticeVo.setDeliveryDate(schedulingDto.getDeliveryTime());
            noticeVo.setSalesSchedulingDate(schedulingDto.getCreateTime());
            noticeVo.setMaterialNo(schedulingDto.getMaterialCode());
            noticeVo.setMaterialName(schedulingDto.getErpName());
            noticeVo.setDrawingNo(schedulingDto.getDrawNo());
            noticeDtoList.add(noticeVo);
        }
        return noticeDtoList;
    }
}




