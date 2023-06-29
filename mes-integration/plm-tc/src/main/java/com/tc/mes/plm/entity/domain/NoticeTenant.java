package com.tc.mes.plm.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName notice_tenant
 */
@TableName(value ="notice_tenant")
@Data
public class NoticeTenant implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 通知外键
     */
    private String noticeId;

    /**
     * 单位
     */
    private String unit;

    /**
     * 执行=1 落成 =2
     */
    private String unitType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}