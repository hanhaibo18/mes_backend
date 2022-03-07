package com.richfit.mes.sys.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.sys.Note;
import com.richfit.mes.common.model.sys.vo.DustbinVo;
import com.richfit.mes.common.model.sys.vo.NoteVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Author: xinYu.hou
 * @Date: 2022/2/14 14:19
 **/

@Mapper
public interface NoteMapper extends BaseMapper<Note> {

    /**
     * 功能描述: 查询发送邮件
     * @Author: xinYu.hou
     * @Date: 2022/2/16 10:44
     * @param page
     * @param queryWrapper
     * @return: IPage<NoteVo>
     **/
    @Select("SELECT\n" +
            "note.id,\n" +
            "note_user.user_account,\n" +
            "sys.empl_name,\n" +
            "note.title,\n" +
            "note.create_time,\n" +
            "note_user.state,\n" +
            "note_user.check_look\n" +
            "FROM\n" +
            "sys_note note\n" +
            "LEFT JOIN sys_tenant_user sys ON note.create_by = sys.user_account\n" +
            "LEFT JOIN sys_note_user note_user ON note_user.id = note.id ${ew.customSqlSegment}")
    IPage<NoteVo> querySender(IPage<NoteVo> page,@Param(Constants.WRAPPER)QueryWrapper<NoteVo> queryWrapper);

    /**
     * 功能描述: 逻辑删除
     * @Author: xinYu.hou
     * @Date: 2022/2/16 15:46
     * @param id
     * @return: boolean
     **/
    @Select("UPDATE sys_note note SET note.state = 2 WHERE note.id = #{id}}")
    boolean deleteSender(@Param("id") String id);

    /**
     * 功能描述: 分页查询垃圾箱
     * @Author: xinYu.hou
     * @Date: 2022/2/16 17:28
     * @param page
     * @param queryWrapper
     * @return: IPage<Map<String,Object>>
     **/
    @Select("SELECT\n" +
            "note.title,\n"+
            "note.id,\n" +
            "note.title,\n" +
            "note_user.check_look,\n" +
            "sys.empl_name,\n" +
            "note.create_by\n" +
            "FROM\n" +
            "sys_note note\n" +
            "LEFT JOIN sys_note_user note_user ON note_user.user_account = note.create_by\n" +
            "LEFT JOIN sys_tenant_user sys ON sys.user_account = note.create_by ${ew.customSqlSegment}")
    IPage<DustbinVo> queryDustbin(IPage<DustbinVo> page, @Param(Constants.WRAPPER)QueryWrapper<DustbinVo> queryWrapper);
}
