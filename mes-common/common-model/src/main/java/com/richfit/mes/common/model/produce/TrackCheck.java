package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 工序质检表
 */
@Data
public class TrackCheck extends BaseEntity<TrackCheck> {

    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 跟单工序项ID
     */
    protected String tiId;
      /**
     * 跟单ID
     */
    protected String thId;
    /**
     * 质检类型 0-半检 1-全检 2-抽检
     */
    protected int type;
     /**
     * 质检结果 0-不合格 1-合格
     */
    protected int result;
     /**
     * 不合格原因
     */
    protected String reason;
     /**
     * 合格数量
     */
    protected int qualify;
      /**
     * 不合格数量
     */
    protected int unqualify;
         /**
     * 让步数量
     */
    protected int stepQty;
     /**
     * 返修数量
     */
    protected int fixQty;
    /**
     * 废弃数量
     */
    protected int discardQty;
    /**
     * 处理单号
     */
    protected String dealNo;
    
     /**
     * 处理人
     */
    protected String dealBy;
    /**
     * 处理时间
     */
    protected Date dealTime;
    
       /**
     * 处理意见
     */
    protected String remark;
    
    
}
