package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * sys_notice_tenant
 *
 * @author
 */
@Data
public class NoticeTenant implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 通知外键
     */
    private String noticeId;

    /**
     * 执行单位
     */
    private String executableUnit;

}
