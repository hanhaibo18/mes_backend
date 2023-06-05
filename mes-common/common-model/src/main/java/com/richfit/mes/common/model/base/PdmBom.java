package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 10:46
 */
@Data
@TableName("base_i_pdm_bom")
public class PdmBom {

    private static final long serialVersionUID = 1L;

    @TableField(value = "bom_id")
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
    private String ver;
    /**
     * 名称
     */
    @TableField(value = "name")
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

    @TableField(value = "datagroup")
    private String dataGroup;

    @TableField(exist = false)
    private List<PdmBom> childBom;

    @TableField(exist = false)
    private String  showName;

    @TableField(exist = false)
    private String type;

    public String getShowName(){
        if(StringUtils.isEmpty(materiaNo)){
            materiaNo = "---";
        }
        if(StringUtils.isEmpty(quantity) || quantity.equals("0")){
            quantity = " 未填写";
        }
        return  this.showName = "["+orderNo+"]"+name+" 【产品名:"+productName+"】 【图号:"+id+"】 【物料号:"+materiaNo+"】 【数量:"+quantity+"】";
    }


}
