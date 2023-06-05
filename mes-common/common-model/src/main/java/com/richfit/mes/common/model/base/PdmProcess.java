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
@TableName("base_i_pdm_process")
public class PdmProcess{

    private static final long serialVersionUID = 1L;

    @TableId(value = "draw_id_group",type = IdType.ASSIGN_UUID)
    private String drawIdGroup;
    private String id;
    private String rev;
    private String processUser;
    private String name;
    private String itemStatus;
    private String releaseTime;
    private String processType;
    private String blankType;
    private String substituteMat;
    private String tyItemId;
    private String reserve1;
    private String reserve2;
    private String reserve3;
    //图号
    private String drawNo;
    private Date sycTime;
    @TableField(value = "dataGroup")
    private String dataGroup;
    @TableField(exist = false)
    private String type;
}
