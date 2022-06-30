package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Data
@TableName("base_mes_pdm_draw")
public class PdmMesDraw {

    private static final long serialVersionUID = 1L;
    /**
     * 图纸
     */
    @TableField(value = "draw_id")
    private String drawId;
    /**
     * 图号
     */
    private String itemId;
    /**
     * 版本
     */
    private String itemRev;
/*    //文档类别
   private String fileCategory;
*/
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件
     */
    private String fileUrl;
    /**
     * 关联工序
     */
    private String opId;
    /**
     * 关联工序版本
     */
    private String opVer;
    /**
     * 保留
     */
    private String reserve1;
    /**
     * 保留
     */
    private String reserve2;
    /**
     * 保留
     */
    private String reserve3;
    /**
     * 同步时间
     */
    private Date sycTime;

    @TableField(value = "isop")
    private String op;

    @TableField(value = "dataGroup")
    private String dataGroup;


}
