package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: OutsourceComplete.java
 * @Author: Hou XinYu
 * @Description: 外协报工DTO
 * @CreateTime: 2023年02月07日 10:12:00
 */
@Data
public class OutsourceCompleteDto {

    private List<String> trackHeadId;

    private List<OutsourceDto> outsourceDtoList;

    private List<String> prodNoList;
}
