package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackHeadTemplateForm;
import com.richfit.mes.produce.dao.TrackHeadTemplateFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 跟单表单模板服务
 */
@Service
public class TrackHeadTemplateFormServiceImpl extends ServiceImpl<TrackHeadTemplateFormMapper, TrackHeadTemplateForm> implements TrackHeadTemplateFormService{

    @Autowired
    private TrackHeadTemplateFormMapper trackHeadTemplateFormMapper;

}
