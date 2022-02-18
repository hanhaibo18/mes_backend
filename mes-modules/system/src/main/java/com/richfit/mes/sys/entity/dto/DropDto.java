package com.richfit.mes.sys.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: dropDto.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月18日 10:03:00
 */
@Data
@Accessors(chain = true)
public class DropDto {
    private String id;
    private int state;
}
