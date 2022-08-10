package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.TrackHeadTemplate;
import com.richfit.mes.common.model.sys.TrackHeadTemp;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 王瑞
 * @Description 跟单模板Mapper
 */
@Mapper
public interface TrackHeadTempMapper extends BaseMapper<TrackHeadTemp> {
}
