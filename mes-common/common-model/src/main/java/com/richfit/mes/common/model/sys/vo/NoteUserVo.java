package com.richfit.mes.common.model.sys.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName: NoteVo.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月15日 15:45:00
 */
@Data
@Accessors(chain = true)
public class NoteUserVo {
    private String id;
    private String noteId;
    private int state;
    private Date checkLook;
    private String title;
    private Date createTime;
    private String userAccount;
    private String emplName;
    private String stateName;
}
