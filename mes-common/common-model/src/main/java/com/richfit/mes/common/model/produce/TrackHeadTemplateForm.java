package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 跟单模板表单
 */
@Data
public class TrackHeadTemplateForm extends BaseEntity<TrackHeadTemplateForm> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 组织机构编号
     */
    private String branchCode;

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 标签名
     */
    private String labelName;

    /**
     * 所在行
     */
    private Integer rowPlace;

    /**
     * 所在列
     */
    private Integer colPlace;

    /**
     * 排序号
     */
    private Integer orderNo;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 是否必填
     */
    private String isRequired;

}
