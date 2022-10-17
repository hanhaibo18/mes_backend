package com.richfit.mes.produce.dao.quality;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
