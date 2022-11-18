package com.richfit.mes.common.model.produce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * produce_application_number
 *
 * @author
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationNumber {
    /**
     * 申请单数字Id
     */
    private Integer id;

    /**
     * 工序Id
     */
    private String itemId;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;

    private String remark;

    public void applicationNumber(String itemId, String branchCode, String user, String tenantId) {
        this.branchCode = branchCode;
        this.itemId = itemId;
        this.createBy = user;
        this.createTime = new Date();
        this.modifyBy = user;
        this.modifyTime = new Date();
        this.tenantId = tenantId;
    }
}
