package com.richfit.mes.produce.dao;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @ClassName: NoticeMapper.java
 * @Author: Hou XinYu
 * @Description: 通知实体
 * @CreateTime: 2023年05月29日 18:22:00
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 功能描述: 查询执行单位 落成单位是本租户数据
     *
     * @param page
     * @param wrapper
     * @Author: xinYu.hou
     * @Date: 2023/5/31 10:20
     * @return: IPage<Notice>
     **/
    @Select("SELECT n.*,t.executable_unit FROM sys_notice n,sys_notice_tenant t WHERE n.id = t.notice_id ${ew.customSqlSegment} ")
    IPage<Notice> queryAcceptingPage(@Param("page") Page<Notice> page, @Param(Constants.WRAPPER) Wrapper<Notice> wrapper);

}
