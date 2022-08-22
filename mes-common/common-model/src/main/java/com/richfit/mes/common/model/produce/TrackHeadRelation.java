package com.richfit.mes.common.model.produce;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 跟单库存关联表
 */
@Data
public class TrackHeadRelation {

    /**
     * 描述: 主键
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/20 10:25
     **/
    @ApiModelProperty(value = "主键")
    private long id;

    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单ID")
    private String thId;

    /**
     * 跟单分流ID
     */
    @ApiModelProperty(value = "跟单分流ID")
    private String flowId;

    /**
     * 库存ID
     */
    @ApiModelProperty(value = "库存ID")
    private String lsId;

    /**
     * 类型 0输入物料 1输出物料
     */
    @ApiModelProperty(value = "类型 0输入物料 1输出物料")
    private String type;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;

}
