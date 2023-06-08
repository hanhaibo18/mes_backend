package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    //派给的班组编码
    private String assignSiteId;
    //派给的班组名称
    private String assignSiteName;
    //派给的人员（用逗号隔开，具体人员数据去预装炉人员派工表去找）
    private String assignUser;
    //派给的人员中文名称（仅用于前端展示）
    private String assignUserName;
    //开工人
    private String startDoingUser;
    //开工状态(0未报工1已报工)
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
    //工序类型
    private String optType;
    //材质
    private String texture;
    //报工时间
    private Date finishTime;
    private String branchCode;
    private String tenantId;
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer num;
    @ApiModelProperty(value = "记录审核状态", dataType = "Integer")
    private Integer recordStatus;

}
