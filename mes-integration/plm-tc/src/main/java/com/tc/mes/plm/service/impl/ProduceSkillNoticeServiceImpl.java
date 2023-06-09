package com.tc.mes.plm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tc.mes.plm.entity.domain.ProduceSkillNotice;
import com.tc.mes.plm.entity.domain.ProduceSkillNoticeAttachment;
import com.tc.mes.plm.entity.dto.TechnicalNoticeDto;
import com.tc.mes.plm.entity.request.TechnicalNoticeRequest;
import com.tc.mes.plm.entity.vo.ProduceSkillNoticeAttachmentVo;
import com.tc.mes.plm.entity.vo.ProduceSkillNoticeVo;
import com.tc.mes.plm.mapper.ProduceSkillNoticeMapper;
import com.tc.mes.plm.service.ProduceSkillNoticeAttachmentService;
import com.tc.mes.plm.service.ProduceSkillNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
* @author llh
* @description 针对表【produce_skill_notice(技术通知单)】的数据库操作Service实现
* @createDate 2023-06-09 13:59:31
*/
@Service
public class ProduceSkillNoticeServiceImpl extends ServiceImpl<ProduceSkillNoticeMapper, ProduceSkillNotice>
    implements ProduceSkillNoticeService {

    @Autowired
    private ProduceSkillNoticeService produceSkillNoticeService;

    @Autowired
    private ProduceSkillNoticeAttachmentService produceSkillNoticeAttachmentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatchNotice(List<TechnicalNoticeRequest> noticeRequestList) {
        List<ProduceSkillNoticeAttachment> attachmentList = convert(noticeRequestList).getAttachmentList();
        List<TechnicalNoticeDto> technicalNoticeDtoList = convert(noticeRequestList).getTechnicalNoticeDtoList();
        List<ProduceSkillNoticeVo> skillNoticeVoList = convertVo(technicalNoticeDtoList);
        if (!CollectionUtils.isEmpty(attachmentList)) {
            produceSkillNoticeAttachmentService.saveBatch(attachmentList);
        }
        List<ProduceSkillNotice> produceSkillNoticeList = new ArrayList<>(skillNoticeVoList.size());
        boolean flag = false;
        if (!CollectionUtils.isEmpty(skillNoticeVoList)) {
            skillNoticeVoList.forEach(e -> {
                ProduceSkillNotice produceSkillNotice = new ProduceSkillNotice();
                BeanUtils.copyProperties(e, produceSkillNotice);
                produceSkillNoticeList.add(produceSkillNotice);
            });
            flag = produceSkillNoticeService.saveBatch(produceSkillNoticeList);
        }
        return flag ? true: false;
    }

    private ProduceSkillNoticeAttachmentVo convert(List<TechnicalNoticeRequest> noticeRequestList) {
        List<TechnicalNoticeDto> technicalNoticeDtoList = new ArrayList<>(noticeRequestList.size());
        List<ProduceSkillNoticeAttachment> attachmentList = new ArrayList<>();
        noticeRequestList.forEach(e -> {
            TechnicalNoticeDto technicalNoticeDto = new TechnicalNoticeDto();
            technicalNoticeDto.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            technicalNoticeDto.setTechNoticeNo(e.getTech_notice_no());
            technicalNoticeDto.setTechNoticeRevId(e.getTech_notice_rev_id());
            technicalNoticeDto.setTechNoticeName(e.getTech_notice_name());
            technicalNoticeDto.setWorkNo(e.getWork_no());
            technicalNoticeDtoList.add(technicalNoticeDto);
            e.getPreview_url().forEach(f -> {
                ProduceSkillNoticeAttachment attachment = new ProduceSkillNoticeAttachment();
                attachment.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                attachment.setSkillNoticeId(technicalNoticeDto.getId());
                attachment.setUrl(f);
                attachmentList.add(attachment);
            });
        });
        ProduceSkillNoticeAttachmentVo attachmentVo = new ProduceSkillNoticeAttachmentVo();
        attachmentVo.setTechnicalNoticeDtoList(technicalNoticeDtoList);
        attachmentVo.setAttachmentList(attachmentList);
        return attachmentVo;
    }

    private List<ProduceSkillNoticeVo> convertVo(List<TechnicalNoticeDto> technicalNoticeDtoList) {
        List<ProduceSkillNoticeVo> noticeVoList = new ArrayList<>(technicalNoticeDtoList.size());
        technicalNoticeDtoList.forEach(e -> {
            ProduceSkillNoticeVo noticeVo = new ProduceSkillNoticeVo();
            noticeVo.setId(e.getId());
            noticeVo.setSkillNoticeNumber(e.getTechNoticeNo());
            noticeVo.setSkillNoticeName(e.getTechNoticeName());
            noticeVo.setSkillNoticeRevId(e.getTechNoticeRevId());
            noticeVo.setWorkNo(e.getWorkNo());
            noticeVoList.add(noticeVo);
        });
        return noticeVoList;
    }

}




