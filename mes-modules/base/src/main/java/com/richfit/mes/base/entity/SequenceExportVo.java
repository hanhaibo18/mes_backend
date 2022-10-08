package com.richfit.mes.base.entity;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class SequenceExportVo extends BaseEntity<SequenceExportVo> {


    /**
     * 是否导入
     */
    private String status;
    /**
     *工艺号
     */
    private String content;
    /**
     *工艺描述
     */
    private String remark;
    /**
     *版本号
     */
    private String versionCode;
    /**
     *工序名
     */
    private String optName;
    /**
     *工序号
     */
    private String opNo;
    /**
     *工序类型
     */
    private String optType;
    /**
     *单件
     */
    private String singlePieceHours;
    /**
     *准结
     */
    private String prepareEndHours;
    /**
     *质检确认
     */
    private String isQualityCheck;
    /**
     *调度确认
     */
    private String isScheduleCheck;
}
