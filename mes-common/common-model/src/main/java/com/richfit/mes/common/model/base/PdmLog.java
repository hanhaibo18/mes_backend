package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("base_i_pdm_log")
public class PdmLog {

    @TableId
    private String id;
    private String type;
    private Date queryTime;
    //0失败,1新增,2更新
    private String status;
    private String remark;
    private String par;
}
