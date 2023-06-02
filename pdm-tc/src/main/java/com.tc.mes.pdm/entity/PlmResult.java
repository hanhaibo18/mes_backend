package com.tc.mes.pdm.entity;

import lombok.Data;

/**
 * PDM 通用返回类
 */
@Data
public class PlmResult {
    /**
     * 状态码
     */
    private int code;

    /**
     * 信息
     */
    private String msg;
}
