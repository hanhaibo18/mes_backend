package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackHeadInfo;
import com.richfit.mes.produce.dao.TrackHeadInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
@Transactional
public class TrackHeadServiceInfoImpl extends ServiceImpl<TrackHeadInfoMapper, TrackHeadInfo> implements TrackHeadInfoService {


}
