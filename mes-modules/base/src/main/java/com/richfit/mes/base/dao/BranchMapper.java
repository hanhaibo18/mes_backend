package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.Branch;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 王瑞
 * @Description 组织结构Mapper
 */
@Mapper
public interface BranchMapper extends BaseMapper<Branch> {
}
