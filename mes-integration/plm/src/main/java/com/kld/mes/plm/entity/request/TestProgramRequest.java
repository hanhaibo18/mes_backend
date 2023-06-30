package com.kld.mes.plm.entity.request;

import lombok.Data;

import java.util.List;

/**
 * pdm 试验大纲
 */
@Data
public class TestProgramRequest {
    /**
     *  试验编码
     */
    private String test_id;
    /**
     *  试验版本id
     */
    private String test_rev_id;
    /**
     *  试验名
     */
    private String test_name;
    /**
     *  附件（PDF附件）
     */
    private List<String> preview_url;


}
