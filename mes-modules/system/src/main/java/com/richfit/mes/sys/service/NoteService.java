package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Note;
import com.richfit.mes.common.model.sys.vo.DustbinVo;
import com.richfit.mes.common.model.sys.vo.NoteUserVo;
import com.richfit.mes.common.model.sys.vo.NoteVo;
import com.richfit.mes.common.model.sys.dto.NoteDto;
import com.richfit.mes.sys.entity.dto.QueryDto;
import com.richfit.mes.sys.entity.dto.DropDto;

/**
 * @ClassName: NoteService.java
 * @Author: Hou XinYu
 * @Description: 创建信息
 * @CreateTime: 2022年02月14日 14:35:00
 */
public interface NoteService extends IService<Note> {

    /**
     * 功能描述: 创建短信通知
     * @Author: xinYu.hou
     * @Date: 2022/2/14 14:57
     * @param note
     * @return: boolean
     **/
    boolean saveNote(NoteDto note);

    /**
     * 功能描述: 查询详情
     * @Author: xinYu.hou
     * @Date: 2022/2/14 17:11
     * @param id
     * @param userId
     * @return: CommonResult<Note>
     **/
    CommonResult<Note> queryById(String id,String userId);

    /**
     * 功能描述: 查询收到的邮件
     * @Author: xinYu.hou
     * @Date: 2022/2/15 16:05
     * @param queryDto
     * @return: CommonResult<IPage<NoteVo>>
     **/
    CommonResult<IPage<NoteUserVo>> queryRecipients(QueryDto<String> queryDto);

    /**
     * 功能描述: 查询发送出去的信息
     * @Author: xinYu.hou
     * @Date: 2022/2/16 10:47
     * @param queryDto
     * @return: CommonResult<IPage<NoteVo>>
     **/
    CommonResult<IPage<NoteVo>> querySender(QueryDto<String> queryDto);

    /**
     * 功能描述: 删除收到的信息
     * @Author: xinYu.hou
     * @Date: 2022/2/16 15:29
     * @param id
     * @return: CommonResult<Boolean>
     **/
    Boolean deleteRecipients(String id);

    /**
     * 功能描述: 删除发送邮件
     * @Author: xinYu.hou
     * @Date: 2022/2/16 15:30
     * @param id
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> deleteSender(String id);

    /**
     * 功能描述: 查询垃圾箱列表
     * @Author: xinYu.hou
     * @Date: 2022/2/17 10:45
     * @param queryDto
     * @return: CommonResult<DustbinVo>
     **/
    CommonResult<IPage<DustbinVo>> queryDustbinVoList(QueryDto<String> queryDto);

    /**
     * 功能描述: 通用物理删除
     * @Author: xinYu.hou
     * @Date: 2022/2/18 10:05
     * @param dropDto
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> deleteMessage(DropDto dropDto);
}
