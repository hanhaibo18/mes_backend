package com.richfit.mes.common.model.produce;


import lombok.Data;

import java.util.Set;

@Data
public class ProductTypeDto {

    /**
     * 物料号Set
     */
    Set<String> materialNoSet;
}
