package com.richfit.mes.common.model.sys.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName: DustbinVo.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年02月17日 10:07:00
 */
@Data
@Accessors(chain = true)
public class DustbinVo {
    private String id;
    private String title;
    private Date createTime;
    private Date checkLook;
    private String emplName;
    private String createBy;
    private String stateName = "删除";
}
