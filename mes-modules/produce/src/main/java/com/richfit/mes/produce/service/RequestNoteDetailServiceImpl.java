package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.RequestNoteDetail;
import com.richfit.mes.produce.dao.RequestNoteDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author hou
 */
@Service
public class RequestNoteDetailServiceImpl extends ServiceImpl<RequestNoteDetailMapper, RequestNoteDetail> implements RequestNoteDetailService {

    @Autowired
    RequestNoteDetailMapper requestNoteDetailMapper;

    @Override
    public List<RequestNoteDetail> getDeliveryInformation(String noteId) {
        return requestNoteDetailMapper.getDeliveryInformation(noteId);
    }

    @Override
    public List<RequestNoteDetail> queryRequestNoteDetailDetails(String materialNo, String requestNoteNo) {
        QueryWrapper<RequestNoteDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_no", materialNo);
        queryWrapper.eq("request_note_number", requestNoteNo);
        return this.list(queryWrapper);
    }

    @Override
    public List<RequestNoteDetail> queryRequestNoteDetailDetails(List<String> materialNo, List<String> requestNoteNo) {
        QueryWrapper<RequestNoteDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("material_no", materialNo);
        queryWrapper.in("request_note_number", requestNoteNo);
        return this.list(queryWrapper);
    }
}
