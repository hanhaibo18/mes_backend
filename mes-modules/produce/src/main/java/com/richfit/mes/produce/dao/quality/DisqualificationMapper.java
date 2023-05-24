package com.richfit.mes.produce.dao.quality;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.entity.quality.DisqualificationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: xinYu.hou
 * @Date: 2022/9/29 15:09
 **/
@Mapper
public interface DisqualificationMapper extends BaseMapper<Disqualification> {

    /**
     * 功能描述: 查询
     *
     * @param tiId
     * @Author: xinYu.hou
     * @Date: 2022/10/17 9:53
     * @return: DisqualificationItemVo
     **/
    @Select("SELECT * FROM produce_disqualification dis WHERE dis.track_item_id = #{tiId}")
    DisqualificationItemVo queryDisqualificationByItemId(String tiId);

    @Select("SELECT dis.* FROM produce_disqualification dis LEFT JOIN produce_disqualification_final_result final ON dis.id = final.id ${ew.customSqlSegment}")
    IPage<Disqualification> query(@Param("page") Page page, @Param(Constants.WRAPPER) Wrapper<Disqualification> wrapper);

    @Select("SELECT * FROM produce_disqualification dis LEFT JOIN produce_disqualification_final_result final ON dis.id = final.id ${ew.customSqlSegment}")
    List<DisqualificationVo> query(@Param("page") Page page, @Param(Constants.WRAPPER) Wrapper<Disqualification> wrapper);

}
