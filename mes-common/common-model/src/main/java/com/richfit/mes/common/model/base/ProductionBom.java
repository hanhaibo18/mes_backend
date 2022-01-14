package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 物料
 */

@Data
public class ProductionBom extends BaseEntity<ProductionBom> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 机构编码
     */
    private String branchCode;

    /**
     * 上级产品图号
     */
    private String mainDrawingNo;

    /**
     * 产品图号
     */
    private String drawingNo;

    /**
     * 物料编码
     */
    private String materialNo;

    /**
     * 物料描述
     */
    @TableField(exist = false)
    private String materialDesc;

    /**
     * 产品名称
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 状态 0:待发布 1:已发布 2:停用
     */
    private String status;

    /**
     * 材质
     */
    @TableField(exist = false)
    private String texture;

    /**
     * 重量
     */
    @TableField(exist = false)
    private Float weight;

    /**
     * 单位
     */
    @TableField(exist = false)
    private String unit;

    /**
     * 产品图片
     */
    private String productImage;

    /**
     * 产品编号来源
     */
    private String productSource;

    /**
     * 是否关键件
     */
    private String isKeyPart;

    /**
     * 是否仓储领料
     */
    private String isNeedPicking;

    /**
     * 实物配送区分
     */
    private String isEdgeStore;

    /**
     * 是否齐套检查
     */
    private String isCheck;

    /**
     * 级别
     */
    private String grade;

    /**
     * 产品附件
     */
    private String productFile;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 产品类型
     */
    @TableField(exist = false)
    private String productType;

    @TableField(exist = false)
    private String objectType;

    private String trackType;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 发布日期
     */
    private Date publishTime;

    /**
     * 发布人
     */
    private String publishBy;

    @TableField(exist = false)
    private Product product;

    /**
     * 是否当前版本 0否 1是
     */
    private String isCurrent;
    
     /**
     * 工序ID
     */
    public String optId;
    
     /**
     * 工序名称
     */
    public String optName;

    public String isNumFrom;

    public String bomKey;

    public String sourceType;

    public Integer orderNo;

    @TableField(exist = false)
    private String isImport;
}
