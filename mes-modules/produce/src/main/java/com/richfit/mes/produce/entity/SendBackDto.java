package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: SendBackDto.java
 * @Author: Hou XinYu
 * @Description: 通知退回DTO
 * @CreateTime: 2023年05月30日 16:12:00
 */
@Data
public class SendBackDto {
    private List<String> idList;
    private String reasonReturn;
}
