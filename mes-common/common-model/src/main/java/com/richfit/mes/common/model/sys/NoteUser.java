package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * sys_note_user
 *
 * @author
 */
@Data
@Accessors(chain = true)
public class NoteUser extends BaseEntity<NoteUser> {

    /**
     * 第一次查看时间
     */
    private Date checkLook;

    /**
     * 信息表ID
     */
    private String noteId;

    /**
     * 查看状态,[{"未读":"0"},{"已读":"1"}]
     */
    private Integer state = 0;

    /**
     * 收件人
     */
    private String userAccount;

    private String tenantId;

    private String branchCode;

    private static final long serialVersionUID = 1L;
}
