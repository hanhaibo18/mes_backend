package com.richfit.mes.produce.service.heat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.produce.entity.heat.HeatCompleteDto;

import java.util.List;
import java.util.Map;

/**
 * @author renzewen
 * @Description 热工报工服务
 */
public interface HeatTrackCompleteService extends IService<TrackComplete> {


    List<TrackComplete> queryList(QueryWrapper queryWrapper);

    /**
     * 功能描述: 热工报工
     *
     * @param heatCompleteDto
     * @Author: renzewen
     * @Date: 2023/1/8 14:08
     * @return: Boolean
     **/
    Boolean saveComplete(HeatCompleteDto heatCompleteDto) throws Exception;

    /**
     * 功能描述: 编辑报工
     *
     * @param trackCompleteList
     * @Author: renzewen
     * @Date: 2023/1/16 14:08
     * @return: Boolean
     **/
    boolean updateComplete(List<TrackComplete> trackCompleteList) throws Exception;

    /**
     * 功能描述: 热工报工开工
     *
     * @param prechargeFurnaceId
     * @Author: renzewen
     * @Date: 2023/1/8 14:08
     * @return: Boolean
     **/
    boolean startWork(String prechargeFurnaceId);

    boolean pLstartWork(String prechargeFurnaceAssignId);

    /**
     * 功能描述: 根据预装炉id获取报工信息
     *
     * @param prechargeFurnaceId
     * @Author: renzewen
     * @Date: 2023/1/10 10:08
     * @return: Map<String,Object>
     **/
    Map<String,Object> getCompleteInfoByFuId(String prechargeFurnaceId);


    /**
     * 功能描述: 工时统计
     * @param trackNo
     * @param startTime
     * @param endTime
     * @param branchCode
     * @param workNo
     * @return
     */
    Map<String, Object> queryTrackCompleteList(String trackNo, String startTime, String endTime, String branchCode, String workNo);

    /**
     * 报工获取炉号
     * @param deviceName
     * @param branchCode
     * @param code
     * @return
     */
    String getFurnaceNo(String deviceName,String branchCode,String code) throws Exception;

    /**
     * 报工步骤回滚
     * @param prechargeFurnaceId
     * @return
     */
    boolean rollBack(Long prechargeFurnaceId);



}
