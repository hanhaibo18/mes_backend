package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.Note;
import com.richfit.mes.common.model.sys.NoteUser;
import com.richfit.mes.sys.dao.NoteMapper;
import com.richfit.mes.sys.dao.NoteUserMapper;
import org.springframework.stereotype.Service;

/**
 * @ClassName: NoteUserServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年03月01日 14:27:00
 */
@Service
public class NoteUserServiceImpl extends ServiceImpl<NoteUserMapper, NoteUser> implements NoteUserService {

}
