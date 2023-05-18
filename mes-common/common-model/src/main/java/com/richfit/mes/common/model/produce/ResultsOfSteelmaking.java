package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * (ProduceResultsOfSteelmaking)表实体类
 *
 * @author makejava
 * @since 2023-05-12 14:02:31
 */
@Data
public class ResultsOfSteelmaking extends BaseEntity<ResultsOfSteelmaking> {

    //炼钢记录id
    private String steelmakingId;

    private Double c;
    
    private Double si;
    
    private Double mn;
    
    private Double p;
    
    private Double s;
    
    private Double cr;
    
    private Double mo;
    
    private Double ni;

    }

