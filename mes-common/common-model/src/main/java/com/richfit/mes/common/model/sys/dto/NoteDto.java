package com.richfit.mes.common.model.sys.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

/**
 * @ClassName: NoteDto.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年02月14日 14:39:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDto {
    @Nonnull
    private String title;
    @NotNull
    private String content;
    @Nonnull
    private String users;
    @NotNull
    private String tenantId;
    @NotNull
    private String branchCode;
}
