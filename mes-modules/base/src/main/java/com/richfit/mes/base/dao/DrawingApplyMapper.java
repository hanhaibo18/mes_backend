package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.DrawingApply;
import com.richfit.mes.common.model.produce.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 王瑞
 * @Description 图纸申请 Mapper
 */
@Mapper
public interface DrawingApplyMapper extends BaseMapper<DrawingApply> {
    //新加入图纸各个数量查询
    List<DrawingApply> list(@Param("param") DrawingApply drawingApply);
}
