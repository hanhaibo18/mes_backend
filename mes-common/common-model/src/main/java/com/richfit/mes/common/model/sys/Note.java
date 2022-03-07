package com.richfit.mes.common.model.sys;


import com.richfit.mes.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.xml.soap.Text;

/**
 * sys_note
 * @author
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Note extends BaseEntity<Note> {
    /**
     * 状态
     */
    private Integer state;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    private String tenantId;

    private String branchCode;

    private static final long serialVersionUID = 1L;
}
