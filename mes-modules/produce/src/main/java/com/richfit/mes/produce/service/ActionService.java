package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Action;

/**
 * @author 王瑞
 * @Description 跟单工序
 */
public interface ActionService extends IService<Action> {

    Boolean saveAction(Action action);

}
