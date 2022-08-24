package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordMt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author rzw
 * @date 2022-08-22 11:23
 */
@Mapper
public interface ProduceInspectionRecordMtMapper extends BaseMapper<ProduceInspectionRecordMt> {
}
