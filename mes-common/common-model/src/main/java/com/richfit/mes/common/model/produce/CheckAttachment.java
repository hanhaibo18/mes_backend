package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_track_check_attachment
 *
 * @author
 */
@TableName("produce_track_check_attachment")
@Data
public class CheckAttachment extends BaseEntity<CheckAttachment> {

    /**
     * 跟单Id
     */
    @ApiModelProperty(value = "跟单Id", dataType = "String")
    private String thId;

    /**
     * 跟单工序ID
     */
    @ApiModelProperty(value = "跟单工序ID", dataType = "String")
    private String tiId;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型", dataType = "String")
    private String classify;

    /**
     * 文件Id
     */
    @ApiModelProperty(value = "文件Id", dataType = "String")
    private String fileId;

    /**
     * 工厂代码
     */
    @ApiModelProperty(value = "工厂代码", dataType = "String")
    private String branchCode;

    /**
     * 租户Id
     */
    @ApiModelProperty(value = "租户Id", dataType = "String")
    private String tenantId;

}
