package com.richfit.mes.produce.service.heat;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.produce.entity.CompleteDto;

import java.util.List;

/**
 * @author renzewen
 * @Description 报工服务
 */
public interface HeatTrackCompleteService extends IService<TrackComplete> {

    /**
     * 功能描述: 新增报工
     *
     * @param completeDtoList
     * @Author: xinYu.hou
     * @Date: 2022/7/12 14:08
     * @return: Boolean
     **/
    CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList);



}
