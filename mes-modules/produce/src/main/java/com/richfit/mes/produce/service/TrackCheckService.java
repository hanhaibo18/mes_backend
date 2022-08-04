package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.CountDto;

import java.util.List;

/**
 * @author 马峰
 * @Description 质检结果
 */
public interface TrackCheckService extends IService<TrackCheck> {

    List<CountDto> count(String dateType, String startTime, String endTime);

    /**
     * 功能描述: 获取下工序列表
     *
     * @param tiId
     * @Author: xinYu.hou
     * @Date: 2022/8/2 14:58
     * @return: List<TrackItem>
     **/
    List<TrackItem> getItemList(String tiId);
}
