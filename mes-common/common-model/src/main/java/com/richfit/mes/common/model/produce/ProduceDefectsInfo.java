package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@TableName(value = "produce_defects_info")
public class ProduceDefectsInfo extends BaseEntity<ProduceDefectsInfo> {
    @ApiModelProperty(value = "探伤记录id")
    private String recordId;
    @ApiModelProperty(value = "探伤记录模板类型（mt pt rt ut）")
    private String type;
    @ApiModelProperty(value = "出厂编号")
    private String factoryNo;
    @ApiModelProperty(value = "缺陷编号")
    private String defectsNumber;
    @ApiModelProperty(value = "缺陷情况")
    private String defects;
    @ApiModelProperty(value = "评定等级")
    private String rating;
    @ApiModelProperty(value = "检测结果")
    private String testResults;
    @ApiModelProperty(value = "标记号")
    private String marksNo;
    @ApiModelProperty(value = "底片号")
    private String theFilm;
    @ApiModelProperty(value = "埋藏深度")
    private String buriedDepth;
    @ApiModelProperty(value = "缺陷当量")
    private String defectsEquivalent;
    @ApiModelProperty(value = "db")
    private String db;
    @ApiModelProperty(value = "缺陷范围(mm)")
    private String scopeDefects;
}
