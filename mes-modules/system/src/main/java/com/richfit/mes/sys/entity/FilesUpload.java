package com.richfit.mes.sys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: FilesUpload.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年06月28日 15:13:00
 */
@Data
public class FilesUpload {

    @ApiModelProperty(value = "跟单Id", dataType = "String")
    private String thId;

    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String tiId;

    @ApiModelProperty(value = "类型", dataType = "String")
    private String classify;

    @ApiModelProperty(value = "工厂代码", dataType = "String")
    private String branchCode;
}
