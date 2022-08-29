package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.*;

import java.util.List;
import java.util.Map;

/**
 * @author 王瑞
 * @Description 跟单服务
 */
public interface TrackHeadService extends IService<TrackHead> {


    /**
     * 功能描述: 工序资料下载指定位置
     *
     * @param flowId 跟单分流id
     * @param path   保存路径
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     **/
    void downloadTrackItem(String flowId, String path) throws Exception;

    /**
     * 功能描述: 下载料单文件
     *
     * @param id   料单id
     * @param path 保存路径
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     **/
    void downloadStoreFile(String id, String path) throws Exception;

    /**
     * 功能描述: 跟单分流信息查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     **/
    List<TrackHead> selectTrackFlowList(Map<String, String> map) throws Exception;

    /**
     * 描述: 其他资料列表
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    List<LineStore> otherData(String flowId) throws Exception;

    /**
     * 描述: 通过跟单分流id生成完工资料并打包zip
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    String completionDataZip(String flowId);

    /**
     * 描述: 完工资料生成
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    void completionData(String flowId);

    /**
     * 描述: 根据跟单编码查询唯一跟单
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    TrackHead selectByTrackNo(String trackNo, String branchCode);

    /**
     * 描述: 跟单新增
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    boolean saveTrackHead(TrackHead trackHead);

    /**
     * 描述: 跟单更新
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    boolean updataTrackHead(TrackHead trackHead, List<TrackItem> trackItems);

    /**
     * 功能描述: 跟单完成
     *
     * @param flowId 跟单分流id
     * @Author: zhiqiang.lu
     * @Date: 2022/7/6 18:07
     * @return: void
     **/
    void trackHeadFinish(String flowId);

    /**
     * 功能描述: 跟单报废
     *
     * @param id 跟单id
     * @Author: zhiqiang.lu
     * @Date: 2022/8/17 18:07
     * @return: void
     **/
    void trackHeadUseless(String id);

    boolean deleteTrackHead(List<TrackHead> trackHeads);

    IPage<TrackHead> selectTrackHeadRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query);

    IPage<TrackHead> selectTrackHeadCurrentRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query);

    /**
     * 功能描述: 对当前跟单增加计划
     *
     * @param trackHeads 跟单列表
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 18:07
     * @return: boolean
     **/
    boolean updateTrackHeadPlan(List<TrackHead> trackHeads);

    /**
     * 功能描述: 根据计划Id 查询跟单
     *
     * @param workPlanId 计划Id
     * @Author: xinYu.hou
     * @Date: 2022/4/20 11:42
     * @return: 数量
     **/
    Integer queryTrackHeadList(String workPlanId);

    /**
     * 功能描述: 来料入库合格证查询
     *
     * @param page          页
     * @param size          数量
     * @param certificateNo 合格证编号
     * @param drawingNo     图号
     * @param branchCode    分组
     * @param tenantId      租户
     * @Author: xinYu.hou
     * @Date: 2022/4/25 15:23
     * @return: IPage<IncomingMaterialVO>
     **/
    IPage<IncomingMaterialVO> queryMaterialList(Integer page, Integer size, String certificateNo, String drawingNo, String branchCode, String tenantId);

    /**
     * 功能描述: 跟单台账分页查询
     *
     * @param standingBookDto 查询对象
     * @Author: xinYu.hou
     * @Date: 2022/4/27 22:49
     * @return: IPage<TrackHead>
     **/
    IPage<TrackHead> queryTrackHeadPage(QueryDto<StandingBookDto> standingBookDto);

    /**
     * 功能描述: 分页查询工作清单
     *
     * @param queryWork
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:23
     * @return: IPage<WorkDetailedListVo>
     **/
    IPage<WorkDetailedListVo> queryWorkDetailedList(QueryDto<QueryWork> queryWork);

    /**
     * 功能描述: 更改优先级
     *
     * @param trackNo
     * @param priority
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:43
     * @return: Boolean
     **/
    Boolean updateWorkDetailed(String trackNo, String priority);

    /**
     * 功能描述: 跟踪调度 跟单列表查询
     *
     * @param afterDto
     * @Author: xinYu.hou
     * @Date: 2022/5/8 8:13
     * @return: IPage<TailAfterVo>
     **/
    IPage<TailAfterVo> queryTailAfterList(QueryDto<QueryTailAfterDto> afterDto);

    /**
     * 功能描述: 根据合格证号查询跟单
     *
     * @param certificateId
     * @Author: Gaol
     * @Date: 2022/6/16 6:34
     * @return: List<TrackHead>
     **/
    List<TrackHead> queryListByCertId(String certificateId);

    /**
     * 功能描述: 绑定合格证号
     *
     * @param thId
     * @param certNo
     * @return
     * @Author Gaol
     */
    Boolean linkToCert(String thId, String certNo);

    /**
     * 功能描述: 置空合格证号
     *
     * @param thId
     * @return
     * @Author Gaol
     */
    Boolean unLinkFromCert(String thId);

    List<TrackHead> queryTrackAssemblyByTrackNo(String flowId);

    /**
     * 功能描述: 跟单拆分
     *
     * @param trackHead    原跟单号信息
     * @param trackNoNew   新跟单号
     * @param trackFlow    原跟单产品列表
     * @param trackFlowNew 新跟单产品列表
     */
    void trackHeadSplit(TrackHead trackHead, String trackNoNew, List<TrackFlow> trackFlow, List<TrackFlow> trackFlowNew);

    /**
     * 功能描述: 跟单id查询分流（生产线）List
     *
     * @param trackHead 回收的跟单信息
     */
    void trackHeadSplitBack(TrackHead trackHead);

    /**
     * 功能描述: 跟单id查询分流（生产线）List
     *
     * @param trackHeadId 跟单id
     */
    List<TrackFlow> trackFlowList(String trackHeadId);

    /**
     * 功能描述: 跟单入库品总计查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/10 15:06
     **/
    List<Map> selectTrackStoreCount(String drawingNos);

    /**
     * 功能描述: 添加跟单的产品编码（装配业务，如果后续还有其他不同业务在进行补充）
     *
     * @param flowId    生产线id
     * @param productNo 物料产品编码，无需拼接图号，接口会自动拼接图号
     * @Author: zhiqiang.lu
     * @Date: 2022/8/25 15:06
     **/
    void addTrackHeadProductNo(String flowId, String productNo);
}
