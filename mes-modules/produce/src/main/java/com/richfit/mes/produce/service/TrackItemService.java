package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.ItemMessageDto;
import com.richfit.mes.produce.entity.QueryDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionListDto;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序
 */
public interface TrackItemService extends IService<TrackItem> {
    //查询跟单最大完工的工序
    List<TrackItem> selectFinalTrackItems(String trackHeadId);

    List<TrackItem> selectTrackItem(QueryWrapper<TrackItem> query);

    List<TrackItem> selectTrackItemAssign(QueryWrapper<TrackItem> query);

    /**
     * 功能描述: 根据跟单ID查询跟单工序
     *
     * @param trackNo
     * @Author: xinYu.hou
     * @Date: 2022/5/9 8:02
     * @return: List<TrackItem>
     **/
    List<TrackItem> queryTrackItemByTrackNo(String trackNo);

    /**
     * 功能描述: 分页查询未探伤
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/5/11 7:11
     * @return: IPage<TrackItem>
     **/
    IPage<TrackItem> queryFlawDetectionList(QueryDto<QueryFlawDetectionDto> queryDto);

    /**
     * 功能描述: 增加探伤报告
     *
     * @param trackItem
     * @Author: xinYu.hou
     * @Date: 2022/5/11 7:10
     * @return: Boolean
     **/
    Boolean updateFlawDetection(TrackItem trackItem);

    /**
     * 功能描述: 分页查询探伤
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/5/11 7:11
     * @return: IPage<TrackItem>
     **/
    IPage<TrackItem> queryFlawDetectionPage(QueryDto<QueryFlawDetectionListDto> queryDto);

    /**
     * 功能描述: 绑定合格证号
     *
     * @param tiId
     * @param certNo
     * @return
     * @Author gaol
     */
    Boolean linkToCert(String tiId, String certNo);

    /**
     * 功能描述: 绑定工序的完工合格证号
     *
     * @param thId        跟单id
     * @param certificate 合格证信息
     * @return
     * @Author zhiqiang.lu
     */
    Boolean linkToCertNew(String thId, Certificate certificate);


    /**
     * 功能描述: 验证是否已经开具过合格证
     *
     * @param certificate 合格证信息
     * @return
     * @Author gaol
     */
    public Boolean checkIsCertRepeat(Certificate certificate);

    /**
     * 功能描述: 置空合格证号
     *
     * @param tiId
     * @return
     * @Author gaol
     */
    Boolean unLinkFromCert(String tiId);


    /**
     * 重置跟单工序状态
     *
     * @param tiId
     * @param resetType
     * @return
     * @Author WangRui
     */
    String resetStatus(String tiId, Integer resetType, HttpServletRequest request);

    /**
     * 更新至下工序
     *
     * @param flowId
     * @return
     * @Author WangRui
     */
    String nextSequence(String flowId);

    /**
     * 回退工序
     *
     * @param flowId
     * @return
     * @Author WangRui
     */
    String backSequence(String flowId);

    /**
     * 功能描述: 添加跟单 生产线工序信息添加
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/23 10:59
     **/
    void addItemByTrackHead(TrackHead trackHead, List<TrackItem> trackItems, String productsNo, Integer number, String flowId,String priority);

    /**
     * 功能描述: 查询工序信息
     *
     * @param itemId
     * @Author: xinYu.hou
     * @Date: 2022/8/26 17:54
     * @return: ItemMessageDto
     **/
    ItemMessageDto queryItemMessageDto(String itemId);

    /**
     * 功能描述: 不合格品查询工序详情
     *
     * @param tiId
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/10/13 14:43
     * @return: DisqualificationItemVO
     **/
    DisqualificationItemVo queryItem(String tiId, String branchCode);

    /**
     * 功能描述: 询当前跟根据跟单号查单号下工序序号最大一条已派工的工序 并根据flow_id 查询全部工序
     *
     * @param trackHeadId
     * @Author: xinYu.hou
     * @Date: 2022/10/27 9:57
     * @return: List<TrackItem>
     **/
    List<TrackItem> queryItemByTrackHeadId(String trackHeadId);

    /**
     * 功能描述: 通过跟单id查询所有的工序
     *
     * @param trackHeadId
     * @Author: zhiqiang.lu
     * @Date: 2023/5/8 9:57
     * @return: List<TrackItem>
     **/
    List<TrackItem> queryItemByThId(String trackHeadId);

    /**
     * 功能描述: 根据预装炉id生成excel文件
     *
     * @Author: Han Haibo
     * @Date: 2023/2/17 10:59
     **/
    void exportHeatTrackLabel(HttpServletResponse response, String id, String classes);

    /**
     * 功能描述: 工序查询不合格信息接口
     *
     * @param tiId
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2023/3/29 17:53
     * @return: DisqualificationItemVo
     **/
    DisqualificationItemVo queryDisqualificationByItem(String tiId, String branchCode);

    List<TrackItem> getTrackItemList(Wrapper<TrackItem> wrapper);

    /**
     * 冶炼车间工艺信息赋值
     * @param trackItemList
     * @return
     */
    List<TrackItem> ylItemListSetRouterInfo(List<TrackItem> trackItemList);

    /**
     * 炼钢车间工艺信息赋值
     * @param trackItemList
     * @return
     */
    List<TrackItem> zGItemListSetRouterInfo(List<TrackItem> trackItemList);


    /**
     * 热工设置工艺信息
     * @param trackItemList
     * @return
     */
    public List<TrackItem> rGSetRouterInfo(List<TrackItem> trackItemList);
}
