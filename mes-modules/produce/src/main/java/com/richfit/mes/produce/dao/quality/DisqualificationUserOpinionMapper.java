package com.richfit.mes.produce.dao.quality;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.common.model.produce.DisqualificationUserOpinion;
import com.richfit.mes.produce.entity.quality.DisqualificationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: xinYu.hou
 * @Date: 2022/9/29 15:10
 **/
@Mapper
public interface DisqualificationUserOpinionMapper extends BaseMapper<DisqualificationUserOpinion> {

    /**
     * 功能描述: 查询审核人分页数据
     *
     * @param page
     * @param query
     * @Author: xinYu.hou
     * @Date: 2022/10/17 11:16
     * @return: IPage<DisqualificationVo>
     **/
    @Select("select dis.*,opinion.id opinionId FROM produce_disqualification_user_opinion opinion LEFT JOIN produce_disqualification dis ON opinion.disqualification_id = dis.id ${ew.customSqlSegment}")
    IPage<DisqualificationVo> queryCheck(IPage<Disqualification> page, @Param(Constants.WRAPPER) Wrapper<DisqualificationVo> query);
}
