package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * produce_track_head_steel_ingot
 *
 * @author
 */
@Data
public class TrackHeadSteelIngot extends BaseEntity<TrackHeadSteelIngot> {

    /**
     * 锭型
     */
    private String ingotCase;

    /**
     * 浇筑数量
     */
    private Integer pouringQty;

    /**
     * 工厂代码
     */
    private String branchCode;

    /**
     * 租户Id
     */
    private String tenantId;


    private static final long serialVersionUID = 1L;
}
