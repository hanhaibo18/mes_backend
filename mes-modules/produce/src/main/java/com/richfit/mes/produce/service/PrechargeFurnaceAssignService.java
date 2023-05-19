package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnaceAssign;

import java.util.List;


/**
 * (PrechargeFurnaceAssign)表服务接口
 *
 * @author makejava
 * @since 2023-05-19 10:36:13
 */
public interface PrechargeFurnaceAssignService extends IService<PrechargeFurnaceAssign> {

    boolean furnaceAssign(Assign assign, List<Long> furnaceIds);

}

