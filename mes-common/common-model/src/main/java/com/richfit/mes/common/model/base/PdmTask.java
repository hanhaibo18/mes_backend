package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author rzw
 * @date 2022-01-04 10:46
 */
@Data
@TableName("base_i_pdm_task")
public class PdmTask{

    private static final long serialVersionUID = 1L;

    @TableId(value = "task_id",type = IdType.ASSIGN_UUID)
    private String taskId;
    //图号
    private String drawNo;
    //pdm 图号
    private String pdmDrawNo;
    //是否获取工艺，0/1
    private String reqProcess;
    //是否获取图纸，0/1
    private String reqDraw;
    //是否获取bom，0/1
    private String reqBom;
    //状态，0/1,未开始/已完成
    private String status;
    //处理结果 0/1,失败/成功
    private String result;
    //开始时间
    private Date recordOn;
    //完成时间
    private Date changeOn;

    @TableField(value = "datagroup")
    private String dataGroup;
    //发起人
    private String recordBy;
}
