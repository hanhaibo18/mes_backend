package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.RequestNoteDetail;

import java.util.List;

public interface RequestNoteDetailService extends IService<RequestNoteDetail> {


    /**
     * 功能描述: 通过配送主表id查询配送零件详细信息
     *
     * @param noteId 配送主表id
     * @Author: zhiqiang.lu
     * @Date: 2022/9/8 14:32
     **/
    List<RequestNoteDetail> getDeliveryInformation(String noteId);
}
