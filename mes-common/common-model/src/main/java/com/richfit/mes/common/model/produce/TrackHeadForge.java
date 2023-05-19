package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * produce_track_head_forge
 *
 * @author Hou XinYu
 */
@Data
public class TrackHeadForge extends BaseEntity<TrackHeadForge> {

    /**
     * 代用材质
     */
    private String substituteMaterial;
    /**
     * 单号
     */
    private String forgeNumber;

    private String branchCode;

    private String tenantId;

    private String attachmentId;

}
