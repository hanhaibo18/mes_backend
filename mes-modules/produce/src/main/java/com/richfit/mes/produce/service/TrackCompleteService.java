package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.OutsourceCompleteDto;
import com.richfit.mes.produce.entity.QueryWorkingTimeVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author 马峰
 * @Description 报工服务
 */
public interface TrackCompleteService extends IService<TrackComplete> {
    IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query);

    /**
     * 功能描述: 工时人员统计
     *
     * @param trackNo
     * @param startTime
     * @param endTime
     * @param branchCode
     * @param workNo
     * @param userId
     * @Author: xinYu.hou
     * @Date: 2023/2/10 15:17
     * @return: Map<String, Object>
     **/
    Map<String, Object> queryTrackCompleteList(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo);

    List<TrackComplete> queryList(String tiId, String branchCode, String order, String orderCol);

    /**
     * 功能描述: 新增报工
     *
     * @param completeDtoList
     * @Author: xinYu.hou
     * @Date: 2022/7/12 14:08
     * @return: Boolean
     **/
    CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList, HttpServletRequest request);


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
    CommonResult<QueryWorkingTimeVo> queryDetails(String assignId, String tiId, Integer state, String classes);

    CommonResult<QueryWorkingTimeVo> queryDetailsHot(Integer state, String furnaceId, String classes);

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

    /**
     * 功能描述: 校验前端输入字符的合法性
     *
     * @param trackComplete（报工实体信息），trackItem(跟单工序信息),companyCode(当前登录用户的公司信息)
     * @return String
     * @Author: panshi.zhang
     */
    String verifyTrackComplete(TrackComplete trackComplete, TrackItem trackItem, String companyCode);

    /**
     * 功能描述: 外协报工
     *
     * @param outsource
     * @Author: xinYu.hou
     * @Date: 2023/2/7 10:28
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveOutsource(OutsourceCompleteDto outsource);

    /**
     * 功能描述: 新外协报工
     *
     * @param outsource
     * @Author: xinYu.hou
     * @Date: 2023/5/15 16:15
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveOutsourceNew(OutsourceCompleteDto outsource);

    /**
     * 功能描述: 根据订单id统计工时
     *
     * @param trackNo
     * @param startTime
     * @param endTime
     * @param branchCode
     * @param workNo
     * @param userId
     * @param orderNo
     * @return
     */
    Map<String, Object> queryTrackCompleteListByOrder(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo);

    /**
     * 功能描述: 根据工作号统计工时
     *
     * @param trackNo
     * @param startTime
     * @param endTime
     * @param branchCode
     * @param workNo
     * @param userId
     * @param orderNo
     * @return
     */
    Map<String, Object> queryTrackCompleteListByWorkNo(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo);

    Map<String, Object> queryTrackCompleteListByBranch(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo);

    void knockoutLabel(HttpServletResponse response, String tiId);

    IPage<PrechargeFurnace> prechargeFurnaceYl(Long prechargeFurnaceId, String texture, String startTime, String endTime, String workblankType, String status, int page, int limit);
}
