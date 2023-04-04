package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Data
@TableName("base_mes_pdm_draw")
public class PdmMesDraw {

    private static final long serialVersionUID = 1L;
    /**
     * 图纸
     */
    @ApiModelProperty(value = "pdm图纸id", dataType = "String")
    @TableField(value = "draw_id")
    private String drawId;
    /**
     * 图号
     */
    @ApiModelProperty(value = "图号", dataType = "String")
    private String itemId;
    /**
     * 版本
     */
    @ApiModelProperty(value = "图号版本", dataType = "String")
    private String itemRev;
    /**
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型", dataType = "String")
    private String fileType;
    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称", dataType = "String")
    private String fileName;
    /**
     * 文件
     */
    @ApiModelProperty(value = "文件路径", dataType = "String")
    private String fileUrl;
    /**
     * 关联工序
     */
    @ApiModelProperty(value = "关联工序 ", dataType = "String")
    private String opId;
    /**
     * 关联工序版本
     */
    @ApiModelProperty(value = "关联工序版本 ", dataType = "String")
    private String opVer;
    /**
     * 保留
     */
    private String reserve1;
    /**
     * 保留
     */
    private String reserve2;
    /**
     * 保留
     */
    private String reserve3;
    /**
     * 同步时间
     */
    @ApiModelProperty(value = "同步时间 ", dataType = "Date")
    private Date sycTime;

    @TableField(value = "isop")
    private String op;
    @ApiModelProperty(value = "数据组 ", dataType = "String")
    @TableField(value = "dataGroup")
    private String dataGroup;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人 ", dataType = "String")
    private String create_by;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间 ", dataType = "Date")
    private Date createTime;

    /**
     * 文件id，多个用，隔开
     * */
    @ApiModelProperty(value = "文件id，多个用，隔开 ", dataType = "String")
    private String filesId;

    /**
     * 是否为手动上传 0否 , 1是
     * */
    @ApiModelProperty(value = "是否为手动上传 0否 , 1是", dataType = "Integer")
    private Integer isUpload;

    /**
     * 文件id
     * */
    @ApiModelProperty(value = "文件id", dataType = "String")
    private String fileId;

    /**
     * 工序id
     * */
    @ApiModelProperty(value = "工序id", dataType = "String")
    @TableField(exist = false)
    private String routerId;
}
