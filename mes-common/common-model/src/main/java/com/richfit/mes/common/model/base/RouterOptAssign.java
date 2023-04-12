package com.richfit.mes.common.model.base;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.produce.AssignPerson;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author renzewen
 * @Description 工艺工序派工表
 */
@Data
public class RouterOptAssign extends BaseEntity<RouterOptAssign> {

    private static final long serialVersionUID = -2605519808352402867L;
    @ApiModelProperty(value = "id", dataType = "String")
    private String id;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;
    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码", dataType = "String")
    private String branchCode;

    @ApiModelProperty(value = "图号", dataType = "String")
    private String routerNo;
    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String userId;
    /**
     * 派工用户名称
     */
    @ApiModelProperty(value = "派工用户名称", dataType = "String")
    private String userName;
    /**
     * 派工工位ID
     */
    @ApiModelProperty(value = "派工工位ID", dataType = "String")
    private String siteId;
    /**
     * 派工工位名称
     */
    @ApiModelProperty(value = "派工工位名称", dataType = "String")
    private String siteName;
    /**
     * 派工设备ID
     */
    @ApiModelProperty(value = "派工设备ID", dataType = "String")
    private String deviceId;
    /**
     * 派工设备名称
     */
    @ApiModelProperty(value = "派工设备名称", dataType = "String")
    private String deviceName;
    /**
     * 派工优先级  3=High、2=Medium、1=Normal、0=Low
     */
    @ApiModelProperty(value = "派工优先级  3=High、2=Medium、1=Normal、0=Low", dataType = "int")
    private int priority;
    /**
     * 派工数量
     */
    @ApiModelProperty(value = "派工数量", dataType = "int")
    private int qty;

    @ApiModelProperty(value = "工序name", dataType = "String")
    private String optName;


    @TableField(exist = false)
    @ApiModelProperty(value = "派工班组list")
    private List<String> siteList;

    @TableField(exist = false)
    private List<AssignPerson> assignPersons;

    public List<String> getSiteList() {
        if(!StringUtils.isEmpty(siteId) && (ObjectUtil.isEmpty(siteList) || siteList.size()==0)){
            return Arrays.asList(siteId.split(","));
        }
        return siteList;
    }

    public String getSiteId() {
        StringBuilder stringBuilder = new StringBuilder();
        if(!ObjectUtil.isEmpty(this.siteList)){
            for (String siteId : this.siteList) {
                if(!StringUtils.isEmpty(String.valueOf(stringBuilder))){
                    stringBuilder.append(",");
                }
                stringBuilder.append(siteId);
            }
            siteId = String.valueOf(stringBuilder);
        }
        return siteId;
    }
}
