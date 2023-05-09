package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 造型/制芯工序报工记录表(ModelingCore)表实体类
 *
 * @author makejava
 * @since 2023-05-08 10:07:03
 */
@Data
public class ModelingCore extends BaseEntity<ModelingCore> {
    //砂型强度是否符合要求0：不符合，1：符合
    protected Integer satisfy;
    //特种砂配比v（铬：宝）
    protected String score;
    //跟单工序id
    protected String itemId;

}

