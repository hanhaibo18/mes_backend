package com.richfit.mes.sys.service;

import com.alibaba.nacos.common.utils.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Note;
import com.richfit.mes.common.model.sys.NoteUser;
import com.richfit.mes.common.model.sys.vo.DustbinVo;
import com.richfit.mes.common.model.sys.vo.NoteUserVo;
import com.richfit.mes.common.model.sys.vo.NoteVo;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.sys.dao.NoteMapper;
import com.richfit.mes.sys.dao.NoteUserMapper;
import com.richfit.mes.sys.enmus.SenderEnum;
import com.richfit.mes.sys.enmus.RecipientsEnum;
import com.richfit.mes.sys.entity.dto.DropDto;
import com.richfit.mes.common.model.sys.dto.NoteDto;
import com.richfit.mes.sys.entity.dto.QueryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * @ClassName: NoteServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月14日 15:00:00
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Resource
    private NoteService noteService;

    @Resource
    private NoteMapper noteMapper;

    @Resource
    private NoteUserService noteUserService;

    @Resource
    private NoteUserMapper noteUserMapper;

    @Resource
    private TenantUserService tenantUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveNote(NoteDto noteDto) {
        Note note = new Note();
        note.setId(UuidUtils.generateUuid().replace("-",""));
        note.setTitle(noteDto.getTitle())
                .setContent(noteDto.getContent())
                .setTenantId(noteDto.getTenantId())
                .setBranchCode(noteDto.getBranchCode())
                .setState(0);
        noteService.save(note);
        List<String> userIdList = Arrays.asList(noteDto.getUserAccount().split(","));
        List<NoteUser> userList = new ArrayList<>();
        userIdList.forEach(userid -> {
            NoteUser noteUser = new NoteUser();
            noteUser.setUserAccount(userid)
                    .setState(0)
                    .setTenantId(noteDto.getTenantId())
                    .setBranchCode(noteDto.getBranchCode())
                    .setNoteId(note.getId());
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
        assert noteUser != null;
        if (noteUser.getState() == 0){
            noteUser.setState(1);
            noteUser.setCheckLook(new Date());
            noteUserService.updateById(noteUser);
        }
        Note note = noteService.getById(noteUser.getNoteId());
        note.setUserAccount(noteUser.getUserAccount());
        List<String> list = new ArrayList<>();
        list.add(note.getUserAccount());
        note.setUserAccountList(list);
        return CommonResult.success(note);
    }

    @Override
    public CommonResult<IPage<NoteUserVo>> queryRecipients(QueryDto<String> queryDto) {
        QueryWrapper<NoteUserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_user.user_account",queryDto.getParam());
        queryWrapper.notIn("note_user.state",SenderEnum.DELETE.getStateId());
        IPage<NoteUserVo> noteUserList = noteUserMapper.queryRecipients(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
        if (noteUserList.getCurrent() != 0){
            TenantUserVo tenantUserVo = tenantUserService.get(noteUserList.getRecords().get(0).getCreateBy());
            noteUserList.getRecords().forEach(note -> {
               note.setStateName(SenderEnum.getMessage(note.getState()))
                       .setUserAccount(tenantUserVo.getUserAccount())
                       .setEmplName(tenantUserVo.getEmplName());
            });
        }
        return CommonResult.success(noteUserList);
    }

    @Override
    public CommonResult<IPage<NoteVo>> querySender(QueryDto<String> queryDto) {
        QueryWrapper<NoteVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note.create_By",queryDto.getParam());
//        queryWrapper.notIn("note.state",RecipientsEnum.DELETE.getStateId());
        queryWrapper.notIn("note_user.is_delete",1);
        IPage<NoteVo> noteList = noteMapper.querySender(new Page<>(queryDto.getPage(),queryDto.getSize()),queryWrapper);
        if (noteList.getCurrent() != 0){
            noteList.getRecords().forEach(note -> {
                if (null==note.getCheckLook()) {
                    note.setStateName(RecipientsEnum.getMessage(note.getStart()));
                }
            });
        }
        return CommonResult.success(noteList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRecipients(String id) {
        return noteUserMapper.deleteRecipients(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSender(NoteVo noteVo) {
        QueryWrapper<NoteUserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note.id",noteVo.getNoteUserId());
        noteUserMapper.deleteSenderById(queryWrapper);
        return noteMapper.deleteSender(noteVo.getId());
    }

    @Override
    public CommonResult<IPage<DustbinVo>> queryDustbinVoList(QueryDto<String> queryDto) {
        QueryWrapper<DustbinVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note.create_by",queryDto.getParam())
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
