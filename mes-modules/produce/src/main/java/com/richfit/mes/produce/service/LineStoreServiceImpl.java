package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * @Author: 王瑞
 * @Date: 2020/8/11
 */
@Slf4j
@Service
@Transactional
public class LineStoreServiceImpl  extends ServiceImpl<LineStoreMapper, LineStore> implements LineStoreService{

    @Autowired
    LineStoreMapper lineStoreMapper;

    @Override
    public IPage<LineStore> selectGroup(Page<LineStore> page, QueryWrapper<LineStore> query){
        return lineStoreMapper.selectGroup(page, query);
    }

    @Override
    public IPage<LineStore> selectLineStoreByProduce(Page<LineStore> page, QueryWrapper<LineStore> query){
        return lineStoreMapper.selectLineStoreByProduce(page, query);
    }

    @Override
    public boolean changeStatus(TrackHead trackHead) {

        String pNo = trackHead.getUserProductNo(); //毛坯编号

        /*UpdateWrapper<LineStore> update = new UpdateWrapper<LineStore>();
        UpdateWrapper<LineStore> update2 = new UpdateWrapper<LineStore>();
        update.eq("workblank_no", pNo);
        update.eq("drawing_no", trackHead.getDrawingNo());
        update.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        update.apply("number = user_num");
        update.set("status", "3"); //将状态设置为已消耗
        update.set("out_time", new Date());
        update2.set("status", "1"); //将状态设置为完工
        update2.set("in_time", new Date());
        update2.eq("workblank_no", trackHead.getProductNo());
        update2.eq("drawing_no", trackHead.getDrawingNo());
        update2.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        int count = lineStoreMapper.update(null , update);
        if(count > 0){
            count = lineStoreMapper.update(null , update2);
            if(count > 0){
                return true;
            }
        }*/
        UpdateWrapper<LineStore> update2 = new UpdateWrapper<LineStore>();
        update2.set("status", "1"); //将状态设置为完工
        update2.set("in_time", new Date());
        update2.eq("workblank_no", trackHead.getProductNo());
        update2.eq("drawing_no", trackHead.getDrawingNo());
        update2.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        int count = lineStoreMapper.update(null , update2);
        if(count > 0){
            return true;
        }
        return false;
    }

}
