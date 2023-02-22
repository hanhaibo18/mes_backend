package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * produce_track_head_forge
 *
 * @author Hou XinYu
 */
@Data
public class ProduceTrackHeadForge extends BaseEntity<ProduceTrackHeadForge> {

    /**
     * 下料规格
     */
    private String blankingSpecification;

    /**
     * 代用材质
     */
    private String substituteMaterial;

    /**
     * 单号
     */
    private String forgeNumber;

}
