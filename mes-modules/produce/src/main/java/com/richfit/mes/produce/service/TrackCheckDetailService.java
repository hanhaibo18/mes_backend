package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackCheckDetail;

import java.util.List;

/**
 * @author 马峰
 * @Description 质检结果
 */
public interface TrackCheckDetailService extends IService<TrackCheckDetail> {

    /**
     * 描述: 根据工序id查询质检的工序项目列表
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    List<TrackCheckDetail> selectByTiId(String tiId);
}
