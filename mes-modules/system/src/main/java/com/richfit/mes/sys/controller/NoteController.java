package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.NoteUser;
import com.richfit.mes.common.model.sys.dto.NoteDto;
import com.richfit.mes.sys.enmus.SenderEnum;
import com.richfit.mes.sys.service.NoteService;
import com.richfit.mes.sys.service.NoteUserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

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
     * 功能描述: 创建消息通知
     * @Author: xinYu.hou
     * @Date: 2022/3/4 14:42
     * @param note
     * @return: CommonResult<Boolean>
     **/
    @PostMapping("/save")
    public CommonResult<Boolean> saveNote(@Valid @RequestBody NoteDto note){
        return CommonResult.success(noteService.saveNote(note));
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
}
