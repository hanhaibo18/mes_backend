package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.LineStore;
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

    Map useItem(int num, String drawingNo, String workblankNo);

    public boolean rollBackItem(int num, String id);

    LineStore autoInAndOutStoreByTrackHead(TrackHead trackHead, String product);

    /**
     * 功能描述: 装配库存查询
     *
     * @param parMap
     * @return
     * @Author Gaol
     */
    List<LineStoreSumZp> queryLineStoreSumZp(Map parMap) throws Exception;

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
    void reSetCertNoByTrackHead(String certificateNo);

    /**
     * 功能描述: 跟单与合格证解绑 根据合跟单号更新半成品/成品料单状态为在制，清空合格证号
     *
     * @param trackHead
     * @return
     * @Author Gaol
     */
    void reSetCertNoByTrackHead(TrackHead trackHead);
}
