package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackAssembly;

import java.util.List;

/**
 * @author 马峰
 * @Description 产品装配服务
 */
public interface TrackAssemblyService extends IService<TrackAssembly> {

    /**
     * 功能描述: 查询装配绑定
     *
     * @param page
     * @param trackHeadId
     * @param branchCode
     * @param order
     * @param orderCol
     * @Author: xinYu.hou
     * @Date: 2022/7/18 9:52
     * @return: IPage<TrackAssembly>
     **/
    IPage<TrackAssembly> queryTrackAssemblyPage(Page<TrackAssembly> page, String trackHeadId, String branchCode, String order, String orderCol);

    /**
     * 功能描述:绑定非关键件
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2022/7/18 16:48
     * @return: Boolean
     **/
    Boolean updateComplete(List<String> idList);
}
