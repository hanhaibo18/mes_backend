package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author rzw
 * @date 2022-01-04 10:46
 */
@Data
@TableName("base_i_pdm_option")
public class PdmOption {

    private static final long serialVersionUID = 1L;

    //工序
    @TableId
    private String id;
    //工艺规程
    private String processId;
    //工序版本
    private String rev;
    //工序序号
    private String opNo;
    //工序类型
    private String type;
    //是否有工序图
    private String drawing;
    //工序名称
    private String name;
    //工序内容
    private String content;
    //关/重/试件
    private String gzs;
    //备注
    private String remark;
    @TableField(value = "dataGroup")
    private String dataGroup;
}
