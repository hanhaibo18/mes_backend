package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.NoteUser;
import com.richfit.mes.common.model.sys.dto.NoteDto;
import com.richfit.mes.common.model.sys.vo.NoteUserVo;
import com.richfit.mes.common.model.sys.vo.NoteVo;
import com.richfit.mes.sys.enmus.SenderEnum;
import com.richfit.mes.sys.entity.dto.QueryDto;
import com.richfit.mes.sys.service.NoteService;
import com.richfit.mes.sys.service.NoteUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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
    /**
     * 功能描述: 删除收到的信息
     * @Author: xinYu.hou
     * @Date: 2022/2/16 15:29
     * @param id
     * @return: CommonResult<Boolean>
     **/
    @PutMapping("/delete/recipients/{id}")
    public CommonResult<Boolean> deleteRecipients(@PathVariable String id){
        return noteService.deleteRecipients(id);
    }

    /**
     * 功能描述: 删除发送邮件
     * @Author: xinYu.hou
     * @Date: 2022/2/16 15:30
     * @param id
     * @return: CommonResult<Boolean>
     **/
    @PutMapping("/delete/sender/{id}")
    public CommonResult<Boolean> deleteSender(@PathVariable String id){
        return noteService.deleteSender(id);
    }

    /**
     * 功能描述: 发送个人短信
     * @Author:
     * @Date:
     * @param
     * @return:
     **/
    @PostMapping ("/save")
    public CommonResult<Boolean> save(@RequestParam("sendUser") String sendUser,
                                      @RequestParam("sendTitle") String  sendTitle,
                                      @RequestParam("sendContent") String  sendContent,
                                      @RequestParam("reseiverUsers") String  reseiverUsers,
                                      @RequestParam("branchCode") String  branchCode,
                                      @RequestParam("tenantId") String  tenantId){
        NoteDto noteDto = new NoteDto();
        noteDto.setContent(sendContent);
        noteDto.setTitle(sendTitle);
        noteDto.setTenantId(tenantId);
        noteDto.setBranchCode(branchCode);
        noteDto.setUsers(reseiverUsers);

        return CommonResult.success(noteService.saveNote(noteDto));
    }

    /**
     * 功能描述: 查询收到的邮件
     * @Author: xinYu.hou
     * @Date: 2022/2/15 16:05
     * @param queryDto
     * @return: CommonResult<IPage<NoteVo>>
     **/
    @PostMapping("/query/recipients_page")
    public CommonResult<IPage<NoteUserVo>> queryRecipients(@RequestBody QueryDto<String> queryDto){
        return noteService.queryRecipients(queryDto);
    }

    /**
     * 功能描述: 查询发送出去的信息
     * @Author: xinYu.hou
     * @Date: 2022/2/16 10:47
     * @param queryDto
     * @return: CommonResult<IPage<NoteVo>>
     **/
    @PostMapping("/query/sender_page")
    public CommonResult<IPage<NoteVo>> querySender(@RequestBody QueryDto<String> queryDto){
        return noteService.querySender(queryDto);
    }

    /**
     * 功能描述: 根据用户Id获取未读数量
     * @Author: xinYu.hou
     * @Date: 2022/3/4 14:49
     * @param userId
     * @return: CommonResult<Integer>
     **/
    @GetMapping("/query/message_number/{userId}")
    public CommonResult<Integer> queryMessageNumber(@PathVariable String userId){
        QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userId);
        queryWrapper.eq("state", SenderEnum.NOT_READ.getStateId());
        int count = noteUserService.count(queryWrapper);
        return CommonResult.success(count);
    }

    /**
     * 功能描述: 已读
     * @Author: xinYu.hou
     * @Date: 2022/3/8 16:53
     * @param id
     * @return: CommonResult<Boolean>
     **/
    @PutMapping("/read/{id}")
    public CommonResult<Boolean> read(@PathVariable String id){
        NoteUser noteUser = new NoteUser();
        noteUser.setId(id);
        noteUser.setState(1);
        return CommonResult.success(noteUserService.updateById(noteUser));
    }
}
