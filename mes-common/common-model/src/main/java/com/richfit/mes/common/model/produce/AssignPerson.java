package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.text.spi.DateFormatSymbolsProvider;
import java.util.Date;

/**
 * @author 马峰
 * @Description 派工人员表
 */
@Data
public class AssignPerson  {

    /**
     *
     */
    private String assignId;
    /**
     *
     */
    private String userId;
    /**
     *
     */
    private String userName;
    /**
     *
     */
    private Date modifyTime;

}
