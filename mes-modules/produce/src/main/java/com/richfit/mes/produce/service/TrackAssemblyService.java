package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.AdditionalMaterialDto;
import com.richfit.mes.produce.entity.AssembleKittingVo;

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
    Boolean updateComplete(List<String> idList, String itemId);

    /**
     * 功能描述:解绑非关键件
     *
     * @param idList
     * @Author: xinYu.hou
     * @Date: 2022/7/20 10:53
     * @return: Boolean
     **/
    Boolean unbindComplete(List<String> idList);

    /**
     * 功能描述: 齐套性检查
     *
     * @param trackHeadId
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/7/22 16:59
     * @return: List<AssembleKittingVo>
     **/
    List<AssembleKittingVo> kittingExamine(String trackHeadId, String branchCode);

    /**
     * 功能描述: 齐套性检查(计划)
     *
     * @param trackHeadId
     * @param branchCode
     * @param isComplete
     * @Author: xinYu.hou
     * @Date: 2022/8/12 16:30
     * @return: List<TrackAssembly>
     **/
    List<TrackAssembly> planKittingExamine(String trackHeadId, String branchCode, Boolean isComplete);

    List<TrackAssembly> queryTrackAssemblyByTrackNo(String flowId);

    /**
     * 功能描述: 追加物料
     *
     * @param additionalMaterialDto
     * @Author: xinYu.hou
     * @Date: 2022/7/26 17:56
     * @return: ApplicationResult
     **/
    ApplicationResult application(AdditionalMaterialDto additionalMaterialDto);

    Page<TrackAssembly> getDeliveredDetail(Page<TrackAssembly> trackAssemblyPage, String id);

    /**
     * 功能描述: 添加跟单 装配信息添加
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/23 10:59
     **/
    void addTrackAssemblyByTrackHead(TrackHead trackHead);
}
