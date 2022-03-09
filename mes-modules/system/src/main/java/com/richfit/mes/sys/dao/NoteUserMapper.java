package com.richfit.mes.sys.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.sys.NoteUser;
import com.richfit.mes.common.model.sys.vo.NoteUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * @Author: xinYu.hou
 * @Date: 2022/2/14 14:19
 **/

@Mapper
public interface NoteUserMapper extends BaseMapper<NoteUser> {

    /**
     * 功能描述: 根据收件人查询邮件
     * @Author: xinYu.hou
     * @Date: 2022/2/15 16:14
     * @param page
     * @param queryWrapper
     * @return: IPage<NoteVo>
     **/
    @Select("SELECT\n" +
            "note_user.id,\n" +
            "note_user.note_id,\n" +
            "note.create_by,\n" +
            "note_user.state,\n" +
            "note_user.check_look,\n" +
            "note.title,\n" +
            "note.create_time,\n" +
            "sys.user_account,\n" +
            "sys.empl_name\n" +
            "FROM\n" +
            "sys_note_user note_user\n" +
            "LEFT JOIN sys_note note ON note_user.note_id = note.id\n" +
            "LEFT JOIN sys_tenant_user sys ON note_user.user_account = sys.user_account ${ew.customSqlSegment}")
    IPage<NoteUserVo> queryRecipients(IPage<NoteUserVo> page, @Param(Constants.WRAPPER) QueryWrapper<NoteUserVo> queryWrapper);

    /**
     * 功能描述: 逻辑删除
     * @Author: xinYu.hou
     * @Date: 2022/2/16 16:07
     * @param id
     * @return: boolean
     **/
    @Select("UPDATE sys_note_user note SET note.state = 3 WHERE note.id = #{id}}")
    boolean deleteRecipients(@Param("id") String id);
}
