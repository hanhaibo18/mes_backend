package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.entity.ModelApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (ModelApply)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-23 14:49:09
 */
@Mapper
public interface ModelApplyMapper extends BaseMapper<ModelApply> {

    List<ModelApply> getModelApplyList(@Param("tenantId") String tenantId, @Param("sign") int sign, @Param("branchCode") String branchCode,
                                       @Param("drawingNo") String drawingNo, @Param("startTime") String startTime,
                                       @Param("endTime") String endTime, @Param("page") int page, @Param("limit") int limit);
}

