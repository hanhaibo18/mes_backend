package com.richfit.mes.sys.controller;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.sys.entity.dto.NoteDto;
import com.richfit.mes.sys.service.NoteService;
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
    @PostMapping ("/send")
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


}
