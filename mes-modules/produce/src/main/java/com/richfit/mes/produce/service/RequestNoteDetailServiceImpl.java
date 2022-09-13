package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.RequestNoteDetail;
import com.richfit.mes.produce.dao.RequestNoteDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RequestNoteDetailServiceImpl extends ServiceImpl<RequestNoteDetailMapper, RequestNoteDetail> implements RequestNoteDetailService {

    @Autowired
    RequestNoteDetailMapper requestNoteDetailMapper;

    @Override
    public List<RequestNoteDetail> getDeliveryInformation(String noteId) {
        return requestNoteDetailMapper.getDeliveryInformation(noteId);
    }
}
