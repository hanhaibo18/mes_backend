package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.produce.dao.ProduceTrackHeadTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gwb
 * @since 2022-01-10
 */
@Service
public class ProduceTrackHeadTemplateServiceImpl extends ServiceImpl<ProduceTrackHeadTemplateMapper, ProduceTrackHeadTemplate> implements ProduceTrackHeadTemplateService {
    @Autowired
    public ProduceTrackHeadTemplateMapper produceTrackHeadTemplateMapper;


    public IPage<ProduceTrackHeadTemplate> selectPage(Page page, QueryWrapper<ProduceTrackHeadTemplate> qw) {
        return produceTrackHeadTemplateMapper.selectPage(page, qw);
    }


}
