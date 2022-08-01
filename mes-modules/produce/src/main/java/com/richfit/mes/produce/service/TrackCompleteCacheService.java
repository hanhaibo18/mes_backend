package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackCompleteCache;
import com.richfit.mes.produce.entity.CompleteDto;

import java.util.List;

/**
 * @ClassName: TrackCompleteCacheService.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月14日 14:03:00
 */
public interface TrackCompleteCacheService extends IService<TrackCompleteCache> {

    /**
     * 功能描述: 新增数据到报工缓存区
     *
     * @param completeDtoList
     * @Author: xinYu.hou
     * @Date: 2022/7/14 14:11
     * @return: boolean
     **/
    CommonResult<Boolean> saveCompleteCache(List<CompleteDto> completeDtoList);
}
