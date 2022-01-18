package com.richfit.mes.produce.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gwb
 * @since 2022-01-10
 */
@Service
public interface ProduceTrackHeadTemplateService extends IService<ProduceTrackHeadTemplate> {

    public IPage<ProduceTrackHeadTemplate> selectPage(Page page, QueryWrapper<ProduceTrackHeadTemplate> qw);

}
