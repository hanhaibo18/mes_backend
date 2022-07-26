package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.KittingVo;
import com.richfit.mes.produce.entity.QueryProcessVo;
import org.apache.ibatis.annotations.Param;

import java.text.ParseException;
import java.util.List;

/**
 * @author 马峰
 * @Description 跟单派工
 */
public interface TrackAssignService extends IService<Assign> {
    IPage<TrackItem> getPageAssignsByStatus(Page page, QueryWrapper<TrackItem> qw);

    IPage<TrackItem> getPageAssignsByStatusAndTrack(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw);

    IPage<TrackItem> getPageAssignsByStatusAndRouter(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw);

    IPage<Assign> queryPage(Page page, String siteId, String trackNo, String routerNo, String startTime, String endTime, String state, String userId, String branchCode, String productNo, String classes) throws ParseException;

    /**
     * 功能描述: 根据跟单号查询 跟单工序
     *
     * @param trackHeadId
     * @Author: xinYu.hou
     * @return: List<QueryProcessVo>
     **/
    List<QueryProcessVo> queryProcessList(String trackHeadId);

    /**
     * 功能描述: 修改派工
     *
     * @param assign
     * @Author: xinYu.hou
     * @return: double
     **/
    boolean updateProcess(Assign assign);

    /**
     * 功能描述: 齐套性检查
     *
     * @param trackHeadId 跟单Id
     * @Author: xinYu.hou
     * @Date: 2022/7/7 15:46
     * @return: List<KittingVo>
     **/
    List<KittingVo> kittingExamine(String trackHeadId);

    /**
     * 功能描述: 按类型获取待派工跟单
     *
     * @Author: mafeng
     * @Date: 2022/7/26 09:00
     * @return: IPage<TrackHead>
     **/
    IPage<TrackHead> getPageTrackHeadByType(Page page, QueryWrapper<TrackHead> qw);

}
