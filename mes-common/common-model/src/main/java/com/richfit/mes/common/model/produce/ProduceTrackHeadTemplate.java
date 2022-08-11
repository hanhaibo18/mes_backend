package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author gwb
 * @since 2022-01-10
 */
@Data
@Accessors(chain = true)
@TableName("produce_track_head_template")
public class ProduceTrackHeadTemplate extends BaseEntity<ProduceTrackHeadTemplate> {


    @ApiModelProperty(value = "", required = false)
    @TableField("template_code")
    private String templateCode;


    @ApiModelProperty(value = "", required = false)
    @TableField("template_name")
    private String templateName;
    
    
    @ApiModelProperty(value = "", required = false)
    @TableField("type")
    private String type;


    @ApiModelProperty(value = "", required = false)
    @TableField("file_path")
    private String filePath;


    @ApiModelProperty(value = "", required = false)
    @TableField("branch_code")
    private String branchCode;


    @ApiModelProperty(value = "", required = false)
    @TableField("sheet1")
    private String sheet1;


    @ApiModelProperty(value = "", required = false)
    @TableField("sheet2")
    private String sheet2;


    @ApiModelProperty(value = "", required = false)
    @TableField("sheet3")
    private String sheet3;


    @ApiModelProperty(value = "", required = false)
    @TableField("sheet4")
    private String sheet4;

    @ApiModelProperty(value = "", required = false)
    @TableField("file_id")
    private String fileId;

    @ApiModelProperty(value = "", required = false)
    @TableField("tenant_id")
    private String tenantId;

}
