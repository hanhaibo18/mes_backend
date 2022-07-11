package com.richfit.mes.produce.entity.extend;

import com.richfit.mes.common.model.base.ProjectBom;
import lombok.Data;

/**
 * 功能描述: 物料齐套性检查数据
 *
 * @Author: zhiqiang.lu
 * @Date: 2022/7/11 11:37
 **/
@Data
public class ProjectBomComplete extends ProjectBom {
    private static final long serialVersionUID = -4268928750243592745L;
    /**
     * 是否完成
     */
    private String isFinish;
    /**
     * 已安装数量
     */
    private int installNumber;
    /**
     * 第三方仓储数量
     */
    private int erpNumber;
    /**
     * 本地仓储数量
     */
    private int storeNumber;
    /**
     * 缺件数量
     */
    private int missingNumber;
}
