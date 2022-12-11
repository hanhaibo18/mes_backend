package com.richfit.mes.common.model.produce;

import lombok.Data;

/**
 * @ClassName: ApplicationResult.java
 * @Author: Hou XinYu
 * @Description: 申请单返回结果
 * @CreateTime: 2022年07月26日 17:05:00
 */
@Data
public class ApplicationResult {
    private String retCode;
    private String retMsg;
    private String encryption;
}
