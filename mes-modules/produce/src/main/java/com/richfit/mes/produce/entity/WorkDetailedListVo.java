package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @ClassName: WorkDetailedListVo.java
 * @Author: Hou XinYu
 * @Description: 工作清单
 * @CreateTime: 2022年04月29日 09:17:00
 */
@Data
public class WorkDetailedListVo {
    /**
     * 优先级
     **/
    private String priority;
    /**
     * 工作号
     **/
    private String workNo;
    /**
     * 图号
     **/
    private String drawingNo;
    /**
     * 名称
     **/
    private String productName;
    /**
     * 数量
     **/
    private int number;
    /**
     * 编号
     **/
    /**
     * 试棒
     **/
    private String testBarType;
    /**
     * 工艺
     **/
    private String optName;
    /**
     * 下工序
     **/
    private String nextOptSequence;
    /**
     * 重量
     **/
    private String weight;
    /**
     * 材质
     **/
    private String texture;
    /**
     * 跟单号
     **/
    private String trackNo;
    /**
     * 来料日期
     **/

    /**
     * 派工日期
     **/
}
