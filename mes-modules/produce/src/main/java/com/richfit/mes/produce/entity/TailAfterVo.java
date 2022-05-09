package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @ClassName: TailAfterVo.java
 * @Author: Hou XinYu
 * @Description: 跟踪返回数据
 * @CreateTime: 2022年05月08日 08:13:00
 */
@Data
public class TailAfterVo {
    /**
     * 跟单号
     **/
    private String trackNo;
    /**
     * 零部件名称
     **/
    private String productName;
    /**
     * 产品号
     **/
    private String productNo;
    /**
     * 图号
     **/
    private String drawingNo;
    /**
     * 工作号
     **/
    private String workNo;
    /**
     * 产品名称
     **/
    /**
     * 物料号
     **/
    private String materialNo;
    /**
     * 数量
     **/
    private Integer number;
    /**
     * 重量
     **/
    private Float weight;
    /**
     * 材质
     **/
    private String texture;
    /**
     * 跟踪类型
     **/
    private String trackType;
    /**
     * 计划号
     **/
    private String workPlanNo;
    /**
     * 备注
     **/
    private String remark;
    /**
     * 备用材料
     **/
    private String replaceMaterial;
}
