package com.kld.mes.plm.entity;

import lombok.Data;

/**
 * PDM 通用返回类
 */
@Data
public class PdmResult {
    /**
     * 状态码
     */
    private int code;

    /**
     * 信息
     */
    private String msg;
}
