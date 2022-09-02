package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.base.RouterCheck;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 马峰
 * @Description 工序质检表
 */
@Data
public class TrackCheckDetail extends BaseEntity<TrackCheckDetail> {

    private static final long serialVersionUID = 4386745442903137730L;
    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;

    /**
     * @Author: xinYu.hou
     * @Date: 2022/8/31 11:20
     **/
    @ApiModelProperty(value = "分流Id")
    private String flowId;
    /**
     * 跟单工序项ID
     */
    protected String tiId;
    /**
     * 规则ID
     */
    protected String checkId;

    /**
     * 功能描述: trackCheck表Id
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/31 14:18
     **/
    private String trackCheckId;
    /**
     * 质检类型 规则类型 0-人工判断 1-枚举 2-区间 3-允差
     */
    protected String checkType;
    /**
     * 质检结果 0-不合格 1-合格
     */
    protected Integer result;
    /**
     * 检查名称
     */
    protected String checkName;
    /**
     * 检查方法
     */
    protected String checkMethod;
    /**
     * 填写值
     */
    protected String value;

    /**
     * 填写说明
     */
    protected String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "base工艺工序质检明细")
    private RouterCheck routerCheck;


}
