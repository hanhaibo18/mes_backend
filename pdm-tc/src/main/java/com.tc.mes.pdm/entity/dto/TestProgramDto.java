package com.tc.mes.pdm.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * pdm 试验大纲
 */
@Data
public class TestProgramDto {
    /**
     *  试验编码
     */
    private String testId;
    /**
     *  未标注
     */
    private String testRevId;
    /**
     *  试验名称
     */
    private String testName;
    /**
     *  附件（PDF附件）
     */
    private List<String> previewUrl;


}
