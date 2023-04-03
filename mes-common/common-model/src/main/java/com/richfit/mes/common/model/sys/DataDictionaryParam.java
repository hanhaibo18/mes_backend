package com.richfit.mes.common.model.sys;

import java.util.Date;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 数据字典参数表(SysDataDictionaryParam)表实体类
 *
 * @author makejava
 * @since 2023-04-03 15:11:45
 */
@Data
public class DataDictionaryParam extends BaseEntity<DataDictionaryParam> {

    //数据字典id
    private String dictionaryId;

    private String createBy;

    private Date createTime;

    private String modifyBy;

    private Date modifyTime;

    private String remark;
    //序号
    private Integer orderNum;
    //物料编码
    private String materialNo;
    //物料名称
    private String materialName;
    //材质
    private String texture;
    //物料规格
    private String specifications;
    //车间编码
    private String branchCode;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

}

