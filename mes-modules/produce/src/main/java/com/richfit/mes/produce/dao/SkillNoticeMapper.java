package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.SkillNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author hou xinyu
 */
@Mapper
public interface SkillNoticeMapper extends BaseMapper<SkillNotice> {

    /**
     * 功能描述: 查询执行单位是本租户数据
     *
     * @param page
     * @param wrapper
     * @Author: xinYu.hou
     * @Date: 2023/5/31 10:20
     * @return: IPage<Notice>
     **/
    @Select("SELECT n.*,t.unit FROM produce_skill_notice n,produce_skill_notice_tenant t ${ew.customSqlSegment} ")
    IPage<SkillNotice> queryAcceptingPage(@Param("page") Page<SkillNotice> page, @Param(Constants.WRAPPER) Wrapper<SkillNotice> wrapper);


}
