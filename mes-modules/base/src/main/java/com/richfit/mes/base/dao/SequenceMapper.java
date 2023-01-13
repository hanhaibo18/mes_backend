package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.Sequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 马峰
 * @Description 工艺Mapper
 */
@Mapper
public interface SequenceMapper extends BaseMapper<Sequence> {

    /**
     * 功能描述: 查询工艺版本
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/8/18 16:36
     * @return: String
     **/
    @Select("SELECT router.version FROM base_sequence sequence LEFT JOIN base_router router ON sequence.router_id = router.id WHERE  #{optName} and sequence.branch_code = #{branchCode} limit 1")
    String queryVersion(@Param("optName") String optName, @Param("branchCode") String branchCode);
}
