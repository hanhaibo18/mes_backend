package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.Abnormal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 马峰
 * @Description 异常Mapper
 */
@Mapper
public interface AbnormalMapper extends BaseMapper<Abnormal> {

   }
