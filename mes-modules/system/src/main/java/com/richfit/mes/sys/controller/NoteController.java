package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.IErrorCode;
import com.richfit.mes.common.model.sys.Note;
import com.richfit.mes.common.model.sys.NoteUser;
import com.richfit.mes.common.model.sys.dto.NoteDto;
import com.richfit.mes.common.model.sys.vo.NoteUserVo;
import com.richfit.mes.common.model.sys.vo.NoteVo;
import com.richfit.mes.sys.enmus.SenderEnum;
import com.richfit.mes.sys.entity.dto.QueryDto;
import com.richfit.mes.sys.service.NoteService;
import com.richfit.mes.sys.service.NoteUserService;
import io.swagger.annotations.Api;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: NoteController.java
 * @Author: Hou XinYu
 * @Description: 站内短信
 * @CreateTime: 2022年03月01日 09:29:00
 */
@Api("站内短信")
@RestController
@RequestMapping("/api/sys/note")
public class NoteController {

    @Resource
    private NoteService noteService;

    @Resource
    private NoteUserService noteUserService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 功能描述: 删除收到的信息
     *
     * @param list
     * @Author: xinYu.hou
     * @Date: 2022/2/16 15:29
     * @return: CommonResult<Boolean>
     **/
    @PutMapping("/delete/recipients")
    public CommonResult<Boolean> deleteRecipients(@RequestBody List<NoteUserVo> list) {
        Boolean recipients = false;
        for (NoteUserVo noteUserVo : list) {
            recipients = noteService.deleteRecipients(noteUserVo.getId());
        }
        return CommonResult.success(recipients);
    }

    /**
     * 功能描述: 删除发送邮件
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2022/2/16 15:30
     * @return: CommonResult<Boolean>
     **/
    @PutMapping("/delete/sender")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteSender(@RequestBody List<NoteVo> idList) {
        Boolean sender = false;
        for (NoteVo noteVo : idList) {
            sender = noteService.deleteSender(noteVo);
        }
        return CommonResult.success(sender);
    }

    /**
     * 功能描述: 发送个人短信
     *
     * @param
     * @Author:
     * @Date:
     * @return:
     **/
    @PostMapping("/save")
    public CommonResult<Boolean> save(@RequestParam("sendUser") String sendUser,
                                      @RequestParam("sendTitle") String sendTitle,
                                      @RequestParam("sendContent") String sendContent,
                                      @RequestParam("reseiverUsers") String reseiverUsers,
                                      @RequestParam("branchCode") String branchCode,
                                      @RequestParam("tenantId") String tenantId) {
        NoteDto noteDto = new NoteDto();
        noteDto.setContent(sendContent);
        noteDto.setTitle(sendTitle);
        noteDto.setTenantId(tenantId);
        noteDto.setBranchCode(branchCode);
        noteDto.setUserAccount(reseiverUsers);

        return CommonResult.success(noteService.saveNote(noteDto));
    }

    /**
     * 功能描述: 查询收到的邮件
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/2/15 16:05
     * @return: CommonResult<IPage < NoteVo>>
     **/
    @GetMapping("/query/recipients_page")
    public CommonResult<IPage<NoteUserVo>> queryRecipients(QueryDto<String> queryDto) {
        return noteService.queryRecipients(queryDto);
    }

    /**
     * 功能描述: 查询发送出去的信息
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/2/16 10:47
     * @return: CommonResult<IPage < NoteVo>>
     **/
    @GetMapping("/query/sender_page")
    public CommonResult<IPage<NoteVo>> querySender(QueryDto<String> queryDto) {
        return noteService.querySender(queryDto);
    }

    /**
     * 功能描述: 根据用户Id获取未读数量
     *
     * @param userId
     * @Author: xinYu.hou
     * @Date: 2022/3/4 14:49
     * @return: CommonResult<Integer>
     **/
    @GetMapping("/query/message_number/{userId}")
    public CommonResult<Integer> queryMessageNumber(@PathVariable String userId) {
        QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userId);
        queryWrapper.eq("state", SenderEnum.NOT_READ.getStateId());
        int count = noteUserService.count(queryWrapper);
        return CommonResult.success(count);
    }

    /**
     * 功能描述: 已读
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2022/3/8 16:53
     * @return: CommonResult<Boolean>
     **/
    @PutMapping("/read")
    public CommonResult<Boolean> read(@RequestBody List<NoteUserVo> idList) {
        boolean update = false;
        for (NoteUserVo noteUserVo : idList) {
            NoteUser noteUser = new NoteUser();
            noteUser.setId(noteUserVo.getId());
            if (noteUser.getState() != 1) {
                noteUser.setCheckLook(new Date());
            }
            noteUser.setState(1);
            update = noteUserService.updateById(noteUser);
        }
        return CommonResult.success(update);
    }

    /**
     * 功能描述: 页面创建信息
     *
     * @param noteDto
     * @Author: xinYu.hou
     * @Date: 2022/3/11 17:27
     * @return: CommonResult<Boolean>
     **/
    @PostMapping("/save_page")
    public CommonResult<Boolean> save(@RequestBody NoteDto noteDto) {
        boolean saveNote = noteService.saveNote(noteDto);
        if (saveNote) {
            return CommonResult.success(true);
        }
        return CommonResult.failed(new IErrorCode() {
            @Override
            public long getCode() {
                return 500;
            }

            @Override
            public String getMessage() {
                return "请选择收件人";
            }
        });
    }

    /**
     * 功能描述: 查询个详情
     *
     * @param id
     * @param userAccount
     * @Author: xinYu.hou
     * @Date: 2022/3/14 10:14
     * @return: CommonResult<Note>
     **/
    @GetMapping("/query_one/{id}/{userAccount}")
    public CommonResult<Note> queryById(@PathVariable String id, @PathVariable String userAccount) {
        return noteService.queryById(id, userAccount);
    }

    @GetMapping("/query_one/note/{id}")
    public CommonResult<Note> queryNote(@PathVariable String id) {
        QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", id);
        List<NoteUser> noteUserList = noteUserService.list(queryWrapper);
        List<String> tableNames = noteUserList.stream().map(NoteUser::getUserAccount).collect(Collectors.toList());
        return CommonResult.success(noteService.getById(id).setUserAccountList(tableNames));
    }
}
