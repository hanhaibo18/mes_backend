package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.DrawingApplyMapper;
import com.richfit.mes.common.model.base.DrawingApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 王瑞
 * @Description 图纸申请服务
 */
@Service
public class DrawingApplyServiceImpl extends ServiceImpl<DrawingApplyMapper, DrawingApply> implements DrawingApplyService{

    @Autowired
    private DrawingApplyMapper drawingApplyMapper;

}
