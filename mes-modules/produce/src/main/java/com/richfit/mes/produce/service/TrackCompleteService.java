package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.QueryWorkingTimeVo;

import java.util.List;

/**
 * @author 马峰
 * @Description 报工服务
 */
public interface TrackCompleteService extends IService<TrackComplete> {
    IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query);

    /**
     * 功能描述: 新增报工
     *
     * @param completeDtoList
     * @Author: xinYu.hou
     * @Date: 2022/7/12 14:08
     * @return: Boolean
     **/
    CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList);


    /**
     * 功能描述: 查询详情
     *
     * @param assignId
     * @param tiId
     * @param state
     * @Author: xinYu.hou
     * @Date: 2022/7/13 13:51
     * @return: CommonResult<QueryWorkingTimeVo>
     **/
    CommonResult<QueryWorkingTimeVo> queryDetails(String assignId, String tiId, Integer state);

    /**
     * 功能描述: 修改
     *
     * @param completeDto
     * @Author: xinYu.hou
     * @Date: 2022/7/14 14:35
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> updateComplete(CompleteDto completeDto);

    /**
     * 功能描述:回滚
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/7/14 16:50
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> rollBack(String id);
}
