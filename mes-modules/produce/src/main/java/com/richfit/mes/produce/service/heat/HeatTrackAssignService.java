package com.richfit.mes.produce.service.heat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.produce.entity.ForDispatchingDto;

import java.text.ParseException;

/**
 * @author zhiqiang.lu
 * @Description 跟单派工
 */
public interface HeatTrackAssignService extends IService<Assign> {
    /**
     * 功能描述:未报工查询
     *
     * @param dispatchingDto
     * @Author: zhiqiang.lu
     * @Date: 2023/1/3 16:24
     * @return: IPage<Assign>
     **/
    IPage<Assign> queryNotAtWork(ForDispatchingDto dispatchingDto) throws ParseException;
}
