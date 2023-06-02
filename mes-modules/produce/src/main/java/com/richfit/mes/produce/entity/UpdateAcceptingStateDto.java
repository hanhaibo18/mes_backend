package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: UpdateAcceptingStateDto.java
 * @Author: Hou XinYu
 * @Description: 修改接受状态
 * @CreateTime: 2023年05月30日 19:02:00
 */
@Data
public class UpdateAcceptingStateDto {
    private List<String> idList;
    private String state;
}
