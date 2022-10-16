package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.model.produce.store.LineStoreSumZp;

import java.util.List;
import java.util.Map;

public interface LineStoreService extends IService<LineStore> {

    LineStore LineStoreById(String id);

    IPage<LineStoreSum> selectGroup(Page<LineStore> page, QueryWrapper<LineStore> query);

    IPage<LineStore> selectLineStoreByProduce(Page<LineStore> page, QueryWrapper<LineStore> query);

    boolean changeStatus(TrackHead trackHead);

    boolean addStore(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo,
                     Boolean isAutoMatchProd, Boolean isAutoMatchPur, String branchCode);

    boolean checkCodeExist(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo);

    Map useItem(int num, TrackHead trackHead, String workblankNo);

    public boolean rollBackItem(int num, String id);

    LineStore autoInAndOutStoreByTrackHead(int num, TrackHead trackHead, String product);

    /**
     * 功能描述: 根据推送合格证入库
     *
     * @param certificate
     * @return
     * @Author Gaol
     */
    Boolean addStoreByCertTransfer(Certificate certificate) throws Exception;

    /**
     * 功能描述: 装配库存查询
     *
     * @param parMap
     * @return
     * @Author Gaol
     */
    IPage<LineStoreSumZp> queryLineStoreSumZp(Page<LineStoreSumZp> page, Map parMap) throws Exception;

    /**
     * 功能描述: 装配库存数量查询
     *
     * @param parMap
     * @Author: xinYu.hou
     * @Date: 2022/7/7 17:08
     * @return: Integer
     **/
    Integer queryLineStoreSumZpNumber(Map parMap);

    /**
     * 功能描述: 合格证生成，根据合格证对应的跟单信息，实现半成品成品合格证信息更新
     *
     * @param trackHead
     * @return
     * @Author Gaol
     */
    LineStore updateCertNoByCertTrack(TrackHead trackHead);

    /**
     * 功能描述: 对应的合格证被删除，根据合格证编号更新半成品/成品料单状态为在制，清空合格证号
     *
     * @param certificateNo
     * @return
     * @Author Gaol
     */
    void reSetCertNoByCertNo(String certificateNo);

    /**
     * 功能描述: 跟单与合格证解绑 根据合跟单号更新半成品/成品料单状态为在制，清空合格证号
     *
     * @param trackHead
     * @return
     * @Author Gaol
     */
    void reSetCertNoByTrackHead(TrackHead trackHead);

    /**
     * 功能描述:根据图号和物料号 消耗数量
     *
     * @param drawingNo
     * @param materialNo
     * @param number
     * @param state
     * @Author: xinYu.hou
     * @Date: 2022/7/19 10:59
     * @return: Boolean
     **/
    boolean zpExpend(String drawingNo, String materialNo, int number, int state);

    /**
     * 功能描述: 根据料单Id，查询附件并存储到临时目录，等待压缩下载
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/7/19 10:59
     * @return: Boolean
     **/
    public String loadFileToFolder(String id) throws Exception;

    /**
     * 功能描述: 根据料单Id，查询附件id
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/7/19 10:59
     * @return: List<String>
     **/
    List<String> queryStoreFileIdList(String id);

    /**
     * 功能描述: 仓储配送，物料接收入库
     *
     * @param materialReceiveDetails
     * @Author: Gaol
     * @Date: 2022/7/19 10:59
     * @return: Boolean
     **/
    boolean addStoreByWmsSend(List<MaterialReceiveDetail> materialReceiveDetails, String branchCode);

    /**
     * 功能描述: 机加添加跟单时 添加成品信息
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/23 10:59
     **/
    LineStore addCpStoreByTrackHead(TrackHead trackHead, String productsNo, Integer number);


    /**
     * 功能描述: 通过flowId查询库存信息
     *
     * @param flowId
     * @Author: zhiqiang.lu
     * @Date: 2022/10/9 16:59
     * @return: List<String>
     **/
    List<LineStore> listByFlowId(String flowId, String type);
}
