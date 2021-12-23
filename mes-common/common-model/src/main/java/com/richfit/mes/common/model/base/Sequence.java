package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class Sequence extends BaseEntity<Sequence> {


      /**
     * 工序ID
     */
    private String routerId;   
    /**
     * 工序顺序
     */
    private int optOrder;
     /**
     * 工序顺序
     */
    private String optName;
     /**
     * 工序顺序
     */
    private String optType;
     /**
     * 工序编码
     */
    private String optCode;
  
    /**
     * 准结工时
     */
    private int prepareEndHours;
    /**
     * 额定工时
     */
    private int singlePieceHours;
    /**
     * 技术要求
     */
    private String technologySequence;
    /**
     * 是否质检
     */
    private String isQualityCheck;
    /**
     * 是否调度
     */
    private String isScheduleCheck;
    /**
     * 是否并行
     */
    private String isParallel;
        /**
     * 是否并行
     */
    private String isAutoAssign;
    /**
     * 状态
     */
    private String status;
        /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
     /**
     * 工序ID
     */
    private String optId;
      /**
     * 下个工序
     */
    private int optNextOrder;
}
