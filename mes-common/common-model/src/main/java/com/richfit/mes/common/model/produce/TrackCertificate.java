package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单
 */
@Data
public class TrackCertificate{

    @TableId(type = IdType.ASSIGN_UUID)
    private String Id;

    /**
     * 合格证ID
     */
    private String certificateId;

    /**
     * 合格证类型 0 工序 1 完工
     */
    private String certificateType;

    /**
     * 跟单ID
     */
    private String thId;

    /**
     * 跟单工序ID
     */
    private String tiId;

}
