package com.kld.mes.dnc.service;

import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;

/**
 * @author wcy
 * @date 2023/3/15 14:58
 */
public interface ProductToBsWmsService {

    /**
     * 配料申请单推送
     *
     * @param ingredientApplicationDto
     * @return
     */
    ApplicationResult anApplicationForm(IngredientApplicationDto ingredientApplicationDto);

    /**
     * 查询物料库存信息
     *
     * @param materialNo
     * @return
     */
    int queryMaterialCount(String materialNo);

    /**
     * 生产交库
     *
     * @param cert 合格证信息
     * @return
     */
    Boolean sendRequest(Certificate cert);
}
