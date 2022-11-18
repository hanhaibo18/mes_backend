package com.richfit.mes.produce.entity.extend;

import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;
import lombok.Data;

import java.util.List;

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
     * 计划数量
     */
    private int planNumber;
    /**
     * 计划所需数量
     */
    private int planNeedNumber;

    /**
     * 已安装trackAssembly id 逗号隔开
     */
    private String installIds;
    /**
     * 已安装数量
     */
    private double installNumber;
    /**
     * ERP仓储数量
     */
    private double erpNumber;

    /**
     * WMS仓储数量
     */
    private double wmsNumber;
    /**
     * 本地仓储数量
     */
    private double storeNumber;
    /**
     * 缺件数量
     */
    private double missingNumber;
    /**
     * 部件绑定信息
     */
    private List<TrackAssemblyBinding> trackAssemblyBindingList;
}
