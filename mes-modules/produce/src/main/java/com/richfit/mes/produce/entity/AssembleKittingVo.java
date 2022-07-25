package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @ClassName: assembleKittingVo.java
 * @Author: Hou XinYu
 * @Description: 装配报工齐套性检查
 * @CreateTime: 2022年07月22日 16:46:00
 */
@Data
public class AssembleKittingVo {
    /**
     * 图号
     **/
    private String drawingNo;
    /**
     * 物料号
     **/
    private String materialNo;
    /**
     * 物料名称
     **/
    private String materialName;
    /**
     * 需要数量
     **/
    private Integer needNumber;
    /**
     * 安装数量
     **/
    private Integer installNumber;
    /**
     * 线边库
     **/
    private Integer repertoryNumber;
    /**
     * 可配送数量
     **/
    private Integer deliverableQuantity;
    /**
     * 已领数量
     **/
    private Integer acquireNumber;
    /**
     * 缺件数量
     **/
    private Integer shortQuantity;
    /**
     * 仓储领料
     **/
    private String isNeedPicking;
    /**
     * 实物配送
     **/
    private String isEdgeStore;
    /**
     * 关键件
     **/
    private String isKeyPart;
}
