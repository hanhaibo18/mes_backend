package com.kld.mes.erp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: MaterialTypeDto.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年02月11日 14:09:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialTypeDto {
    String oldCode;
    String newCode;
    String desc;
}
