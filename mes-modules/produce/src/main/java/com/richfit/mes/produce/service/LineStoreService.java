package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.store.LineStoreSum;

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
     * 功能描述: 根据合格证对应的跟单信息，实现半成品成品自动入库
     *
     * @param trackHead
     * @return
     * @Author Gaol
     */
    LineStore autoInByCertTrack(TrackHead trackHead);

    /**
     * 功能描述: 根据合格证编号删除入库信息
     *
     * @param certificateNo
     * @return
     * @Author Gaol
     */
    void delInByCertNo(String certificateNo);

    /**
     * 功能描述: 根据合格证编号删除固定数量的入库信息
     *
     * @param certificateNo
     * @param number        指定数量的物料
     * @return
     * @Author Gaol
     */
    void delFixedInByCertNo(String certificateNo, Integer number);
}
