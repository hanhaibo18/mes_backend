package com.bsjx.mes.pdm.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@Table(name = "base_i_pdm_bom")
public class PdmBom {
    @Id
    @GenericGenerator(name = "jpa-uuid", strategy = "uuid")
    @GeneratedValue(generator = "jpa-uuid")
    private String bomId;
    /**
     * 父BOMID
     */
    private String pId;
    /**
     * 图号
     */
    private String id;
    /**
     * 版本
     */
    @Column(name = "ver")
    private String rev;
    /**
     * 名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 创建人
     */
    private String bomUser;
    /**
     * 发布状态
     */
    private String itemStatus;
    /**
     * 发布时间
     */
    private String releaseTime;
    /**
     * 重量
     */
    private String materiaWeight;
    /**
     * 材料
     */
    private String materiaName;
    /**
     * 材料编码
     */
    private String materiaNo;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 坯料类型
     */
    private String blankType;
    /**
     * 关重特性
     */
    private String itemKey;
    /**
     * 数量
     */
    private String quantity;
    /**
     * 备注
     */
    private String remark;
    /**
     * 类型
     */
    private String objectType;
    /**
     * 创建人员
     */
    private String itemUser;
    /**
     * 备用
     */
    private String revserve1;
    /**
     * 备用
     */
    private String revserve2;
    /**
     * 备用
     */
    private String revserve3;
    /**
     * 同步时间
     */
    private Date sycTime;
    /**
     * 序号
     */
    private String orderNo;

    @Column(name = "datagroup")
    private String dataGroup;


}
