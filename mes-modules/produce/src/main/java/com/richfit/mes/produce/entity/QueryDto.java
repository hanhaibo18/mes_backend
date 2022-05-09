package com.richfit.mes.produce.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName: queryDto.java
 * @Author: Hou XinYu
 * @Description: 公共查询类
 * @CreateTime: 2022年01月27日 13:43:00
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryDto<T> {
    private T param;
    private Long page = 1L;
    private Long size = 10L;
    private String branchCode;
    private String tenantId;
}

