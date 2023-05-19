package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (PrechargeFurnaceAssign)表实体类
 *
 * @author makejava
 * @since 2023-05-19 10:33:00
 */
@SuppressWarnings("serial")
@Data
public class PrechargeFurnaceAssign extends BaseEntity<PrechargeFurnaceAssign> {

    //预装炉id
    private Long furnaceId;
    //派工人
    private String assignBy;
    //派工时间
    private Date assignTime;
    //派工状态（0未派工，1已派工）
    private String assignStatus;
    //派给的班组
    private Integer assignSiteId;
    //派给的人员（用逗号隔开，具体人员数据去预装炉人员派工表去找）
    private String assignUser;
    //开工人
    private String startDoingUser;
    //开工状态
    private String isDoing;
    //报工人
    private String completeBy;
    //报工状态
    private String completeStatus;
    //钢种
    private String typeOfSteel;
    //冶炼设备
    private String smeltingEquipment;
    //锭型
    private String ingotCase;
    //毛坯类型 0锻件,1铸件,2钢锭
    private String workblankType;
    //钢水总重
    private Double totalMoltenSteel;
}
