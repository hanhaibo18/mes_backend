package com.richfit.mes.produce.service.heat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.produce.entity.ForDispatchingDto;

import java.text.ParseException;
import java.util.List;

/**
 * @author zhiqiang.lu
 * @Description 跟单派工
 */
public interface HeatTrackAssignService extends IService<Assign> {
    /**
     * 功能描述:派工信息是否生产
     *
     * @param dispatchingDto
     * @Author: zhiqiang.lu
     * @Date: 2023/1/3 16:24
     * @return: IPage<Assign>
     **/
    IPage<Assign> queryWhetherProduce(ForDispatchingDto dispatchingDto, boolean IsProduce) throws ParseException;


    /**
     * 功能描述:跟单派工
     *
     * @param assign
     * @Author: renzewen
     * @Date: 2023/1/5 16:24
     * @return: Assign
     **/
    boolean assignItem(List<Assign> assign) throws Exception;

    /**
     * 功能描述:热工自动派工
     * @param itemId
     * @return
     * @throws Exception
     */
    Boolean automaticProcess(String itemId) throws Exception;
}
