package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Note;
import com.richfit.mes.common.model.sys.NoteUser;
import com.richfit.mes.common.model.sys.UserRole;
import com.richfit.mes.common.model.sys.vo.DustbinVo;
import com.richfit.mes.common.model.sys.vo.NoteUserVo;
import com.richfit.mes.common.model.sys.vo.NoteVo;
import com.richfit.mes.sys.dao.NoteMapper;
import com.richfit.mes.sys.dao.NoteUserMapper;
import com.richfit.mes.sys.enmus.SenderEnum;
import com.richfit.mes.sys.enmus.RecipientsEnum;
import com.richfit.mes.sys.entity.dto.DropDto;
import com.richfit.mes.sys.entity.dto.NoteDto;
import com.richfit.mes.sys.entity.dto.QueryDto;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @ClassName: NoteServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月14日 15:00:00
 */
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Resource
    private NoteService noteService;

    @Resource
    private NoteMapper noteMapper;

    @Resource
    private NoteUserService noteUserService;

    @Resource
    private NoteUserMapper noteUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveNote(NoteDto noteDto) {
        Note note = new Note();
        note.setTitle(noteDto.getTitle())
                .setContent(noteDto.getContent())
                .setTenantId(noteDto.getTenantId())
                .setBranchCode(noteDto.getBranchCode())
                .setState(0);
        String id = noteMapper.insertGetId(note);
        List<String> userIdList = Arrays.asList(noteDto.getUsers().split(","));
        List<NoteUser> userList = new ArrayList<>();
        userIdList.forEach(userid -> {
            NoteUser noteUser = new NoteUser();
            noteUser.setUserAccount(userid)
                    .setState(0)
                    .setTenantId(noteDto.getTenantId())
                    .setBranchCode(noteDto.getBranchCode())
                    .setNoteId(id);
            userList.add(noteUser);
        });
        return noteUserService.saveBatch(userList);
    }

    @Override
    public CommonResult<Note> queryById(String id,String userId) {
        QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id",id);
        queryWrapper.eq("user_account",userId);
        NoteUser noteUser = noteUserService.getOne(queryWrapper);
        if (noteUser.getState() == 0){
            noteUser.setState(1);
            noteUserService.updateById(noteUser);
        }
        return CommonResult.success(noteService.getById(id));
    }

    @Override
    public CommonResult<IPage<NoteUserVo>> queryRecipients(QueryDto<String> queryDto) {
        QueryWrapper<NoteUserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",queryDto.getData());
        queryWrapper.notIn("state",SenderEnum.DELETE.getStateId());
        IPage<NoteUserVo> noteUserList = noteUserMapper.queryRecipients(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
        if (noteUserList.getCurrent() != 0){
            noteUserList.getRecords().forEach(note -> {
               note.setStateName(SenderEnum.getMessage(note.getState()));
            });
        }
        return CommonResult.success(noteUserList);
    }

    @Override
    public CommonResult<IPage<NoteVo>> querySender(QueryDto<String> queryDto) {
        QueryWrapper<NoteVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creatBy",queryDto.getData());
        queryWrapper.notIn("state",RecipientsEnum.DELETE.getStateId());
        IPage<NoteVo> noteList = noteMapper.querySender(new Page<>(queryDto.getPage(),queryDto.getSize()),queryWrapper);
        if (noteList.getCurrent() != 0){
            noteList.getRecords().forEach(note -> {
                note.setStateName(RecipientsEnum.getMessage(note.getStart()));
            });
        }
        return CommonResult.success(noteList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteRecipients(List<String> idList) {
        idList.forEach(id -> noteMapper.deleteSender(id));
        return CommonResult.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteSender(List<String> idList) {
        idList.forEach(id -> noteUserMapper.deleteRecipients(id));
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<IPage<DustbinVo>> queryDustbinVoList(QueryDto<String> queryDto) {
        QueryWrapper<DustbinVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note.create_by",queryDto.getData())
                .and(wrapper -> wrapper.eq("note.state",SenderEnum.DELETE.getStateId()).or().eq("note_user.state",RecipientsEnum.DELETE.getStateId()));
        return CommonResult.success(noteMapper.queryDustbin(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper));
    }

    @Override
    public CommonResult<Boolean> deleteMessage(DropDto dropDto) {
        boolean message = false;
        // 1= 删除收件 2 = 删除发件
        if (dropDto.getState() == 1) {
            message = noteService.removeById(dropDto.getId());
        } else if (dropDto.getState() == 2) {
            QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("note_id",dropDto.getId());
            message = noteUserService.remove(queryWrapper);
        }else {
            message = noteService.removeById(dropDto.getId());
            QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("note_id",dropDto.getId());
            message = noteUserService.remove(queryWrapper);
        }
        return CommonResult.success(message);
    }


}
