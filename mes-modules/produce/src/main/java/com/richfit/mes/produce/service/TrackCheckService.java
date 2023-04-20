package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
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

    /**
     * 功能描述:查询未质检数量
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/9/27 16:10
     * @return: Integer
     **/
    Integer qualityTestingNumber(String branchCode);

    /**
     * 功能描述: 获取分页数据
     *
     * @param page
     * @param qw
     * @Author: xinYu.hou
     * @Date: 2022/11/23 19:30
     * @return: IPage<TrackCheck>
     **/
    IPage<TrackCheck> queryCheckPage(Page<TrackCheck> page, QueryWrapper<TrackCheck> qw);

    /**
     * 功能描述: 根据规则Id查询是否被使用
     *
     * @param rulesId
     * @Author: xinYu.hou
     * @Date: 2022/11/23 19:30
     * @return: Boolean
     **/
    Boolean countQueryRules(String rulesId);

    /**
     * 功能描述: 质检审核分页查询
     *
     * @param page
     * @param limit
     * @param isExistQualityCheck
     * @param isScheduleComplete
     * @param startTime
     * @param endTime
     * @param trackNo
     * @param productNo
     * @param tenantId
     * @param isRecheck
     * @param drawingNo
     * @param order
     * @param orderCol
     * @Author: xinYu.hou
     * @Date: 2023/4/19 18:15
     * @return: IPage<TrackItem>
     **/
    CommonResult<IPage<TrackItem>> queryQualityPage(int page, int limit, String isExistQualityCheck, String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String tenantId, Boolean isRecheck, String drawingNo, String order, String orderCol);


    /**
     * 功能描述: 调度审核分页查询
     *
     * @param page
     * @param limit
     * @param isExistScheduleCheck
     * @param isScheduleComplete
     * @param startTime
     * @param endTime
     * @param trackNo
     * @param productNo
     * @param branchCode
     * @param tenantId
     * @param drawingNo
     * @param order
     * @param orderCol
     * @Author: xinYu.hou
     * @Date: 2023/4/19 18:15
     * @return: IPage<TrackCheck>
     **/
    CommonResult<IPage<TrackItem>> queryDispatchPage(int page, int limit, String isExistScheduleCheck, String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String branchCode, String tenantId, String drawingNo, String order, String orderCol);

}
