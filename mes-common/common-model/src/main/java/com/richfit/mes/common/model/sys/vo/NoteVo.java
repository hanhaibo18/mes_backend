package com.richfit.mes.common.model.sys.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: NoteVo.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月16日 10:37:00
 */
@Data
public class NoteVo {
    private String id;
    private String noteUserId;
    private String userAccount;
    private String emplName;
    private String title;
    private Date createTime;
    private int start;
    private Date checkLook;
    private String stateName;
}
