package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.AssignHot;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.ForDispatchingDto;
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
    IPage<TrackItem> getPageAssignsByStatus(Page page, QueryWrapper<TrackItem> qw, String orderCol, String order, List<String> excludeOrderCols);

    IPage<TrackItem> getPageAssignsHot(Page page, QueryWrapper<TrackItem> qw);

    IPage<TrackItem> getPageAssignsByStatusAndTrack(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw, String orderCol, String order, List<String> excludeOrderCols);

    IPage<TrackItem> getPageAssignsByStatusAndRouter(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw, String orderCol, String order, List<String> excludeOrderCols);

    IPage<Assign> queryPage(Page page, String siteId, String trackNo, String routerNo, String startTime, String endTime, String state, String userId, String branchCode, String productNo, String classes, String order, String orderCol) throws ParseException;

    /**
     * 功能描述: 根据跟单号查询 跟单工序
     *
     * @param flowId
     * @Author: xinYu.hou
     * @return: List<QueryProcessVo>
     **/
    List<QueryProcessVo> queryProcessList(String flowId);

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

    /**
     * 功能描述: 批量开工
     *
     * @param assignId
     * @Author: xinYu.hou
     * @Date: 2022/9/21 15:21
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> startWorking(List<String> assignId);

    /**
     * 功能描述: 查询未派工数量
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/9/26 16:44
     * @return: CommonResult<Integer>
     **/
    Integer queryDispatchingNumber(String branchCode);

    /**
     * 功能描述: 查询未报工数量
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/9/26 16:45
     * @return: CommonResult<Integer>
     **/
    Integer workNumber(String branchCode);

    /**
     * 功能描述: 已派工查询
     *
     * @param dispatchingDto
     * @Author: xinYu.hou
     * @Date: 2022/9/28 10:38
     * @return: IPage<Assign>
     **/
    IPage<Assign> queryForDispatching(ForDispatchingDto dispatchingDto) throws ParseException;

    /**
     * 铸钢车间已派工查询
     * @param dispatchingDto
     * @return
     */
    IPage<AssignHot> queryDispatched(ForDispatchingDto dispatchingDto);

    /**
     * 功能描述:未报工查询
     *
     * @param dispatchingDto
     * @Author: xinYu.hou
     * @Date: 2022/9/28 10:39
     * @return: IPage<Assign>
     **/
    IPage<Assign> queryNotAtWork(ForDispatchingDto dispatchingDto) throws ParseException;

    /**
     * 功能描述:派工查询
     * 任泽文将代码逻辑移到service 未改动
     * @param id
     * @param tiId
     * @param state
     * @param trackId
     * @param trackNo
     * @param flowId
     * @return
     */
    List<Assign> find(String id, String tiId, String state, String trackId, String trackNo, String flowId);


    /**
     * 功能描述:删除派工
     * 任泽文将代码逻辑移到service 未改动
     * @param ids
     * @return
     */
    boolean deleteAssign(String[] ids);


    /**
     * 锻造根据工时标准计算额定工时
     * @param trackHead
     * @param trackItem
     */
    void calculationSinglePieceHours(TrackHead trackHead,TrackItem trackItem);
}
