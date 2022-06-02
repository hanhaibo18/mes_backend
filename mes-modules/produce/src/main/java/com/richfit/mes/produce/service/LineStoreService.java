package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.store.LineStoreSum;

public interface LineStoreService extends IService<LineStore> {

    IPage<LineStoreSum> selectGroup(Page<LineStore> page, QueryWrapper<LineStore> query);

    IPage<LineStore> selectLineStoreByProduce(Page<LineStore> page, QueryWrapper<LineStore> query);

    boolean changeStatus(TrackHead trackHead);

    boolean addStore(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo, Boolean isAutoMatchProd, Boolean isAutoMatchPur, String branchCode);

    boolean checkCodeExist(LineStore lineStore, Integer startNo, Integer endNo, String suffixNo);

    LineStore useItem(int num, String drawingNo, String workblankNo);

    public boolean rollBackItem(int num, String id);
}
