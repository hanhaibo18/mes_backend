package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackHeadTemplate;
import com.richfit.mes.produce.dao.TrackHeadTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 跟单模板服务
 */
@Service
public class TrackHeadTemplateServiceImpl extends ServiceImpl<TrackHeadTemplateMapper, TrackHeadTemplate> implements TrackHeadTemplateService {

    @Autowired
    private TrackHeadTemplateMapper trackHeadTemplateMapper;

}
