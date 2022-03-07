package com.richfit.mes.sys.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.soap.Text;

/**
 * @ClassName: NoteDto.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年02月14日 14:39:00
 */
@Data
public class NoteDto {
    private String title;
    private String content;
    private String users;
    private String tenantId;
    private String branchCode;
}
