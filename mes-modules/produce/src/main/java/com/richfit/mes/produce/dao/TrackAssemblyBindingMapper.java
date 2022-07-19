package com.richfit.mes.produce.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Xinyu.Hou
 */
@Mapper
public interface TrackAssemblyBindingMapper extends BaseMapper<TrackAssemblyBinding> {

    /**
     * 功能描述: 查询已绑定数量
     *
     * @param assemblyId
     * @Author: xinYu.hou
     * @Date: 2022/7/18 11:28
     * @return: int
     **/
    @Select("SELECT COUNT(*) FROM produce_track_assembly_binding track WHERE track.assembly_id = #{assemblyId} AND track.is_binding = 1 ")
    int selectAssemblyBinding(@Param("assemblyId") String assemblyId);
}
