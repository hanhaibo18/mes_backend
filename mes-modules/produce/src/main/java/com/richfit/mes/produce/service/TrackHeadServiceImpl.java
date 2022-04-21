package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackHeadRelation;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.dao.TrackHeadRelationMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
@Transactional
public class TrackHeadServiceImpl extends ServiceImpl<TrackHeadMapper, TrackHead> implements TrackHeadService{

    @Autowired
    private TrackHeadMapper trackHeadMapper;

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private LineStoreMapper lineStoreMapper;

    @Autowired
    private TrackHeadRelationMapper trackHeadRelationMapper;

    @Autowired
    private PlanService planService;

    @Override
    public boolean saveTrackHead(TrackHead trackHead, List<LineStore> lineStores, List<TrackItem> trackItems){
        int result = trackHeadMapper.insert(trackHead);
        if(!StringUtils.isNullOrEmpty(trackHead.getWorkPlanNo())) {
            planService.setPlanStatusStart(trackHead.getWorkPlanNo(), trackHead.getTenantId());
        }
        if(result > 0){

            String[] products = trackHead.getProductNo().split(",");
            int num = trackHead.getNumber();
            for(int i=0; i<products.length; i++){
                if(num == 0){
                    break;
                }
                int userNum = 0; //本次使用数量
                //修改库存状态
                LineStore lineStore1 = lineStoreMapper.selectOne(new QueryWrapper<LineStore>().eq("drawing_no", trackHead.getDrawingNo()).eq("workblank_no", products[i]).eq("tenant_id", trackHead.getTenantId()));
                if(lineStore1 != null) {
                    if(lineStore1.getNumber() - lineStore1.getUserNum() <= num){
                        userNum = lineStore1.getNumber() - lineStore1.getUserNum();
                        num -= lineStore1.getNumber() - lineStore1.getUserNum();
                        lineStore1.setUserNum(lineStore1.getNumber());
                    } else {
                        userNum = num;
                        lineStore1.setUserNum(lineStore1.getUserNum() + num);
                        num = 0;
                    }
                    if(lineStore1.getMaterialType().equals("0")){
                        lineStore1.setOutTime(new Date());
                    }
                    if(lineStore1.getUserNum().equals(lineStore1.getNumber())){
                        lineStore1.setStatus("3");
                    }
                    TrackHeadRelation relation = new TrackHeadRelation();
                    relation.setThId(trackHead.getId());
                    relation.setLsId(lineStore1.getId());
                    relation.setType("0");
                    relation.setNumber(userNum);
                    trackHeadRelationMapper.insert(relation);
                    lineStoreMapper.updateById(lineStore1);
                }
            }
            //新增一条半成品/成品信息
            /*for (LineStore lineStore: lineStores) {
                lineStoreMapper.insert(lineStore);
                TrackHeadRelation relation = new TrackHeadRelation();
                relation.setThId(trackHead.getId());
                relation.setLsId(lineStore.getId());
                relation.setType("1");
                relation.setNumber(1);
                trackHeadRelationMapper.insert(relation);
            }*/
            if(trackItems != null && trackItems.size() > 0){
                int count = 0;
                for (TrackItem item : trackItems) {
                    if(item.getId() != null && !item.getId().equals("")){
                        count += trackItemMapper.updateById(item);
                    } else {
                        item.setTrackHeadId(trackHead.getId());
                        count += trackItemMapper.insert(item);
                    }
                }
                return true;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteTrackHead(List<TrackHead> trackHeads){
        List<String> ids =  trackHeads.stream().filter(trackHead -> {
            if(trackHead.getStatus().equals("0")){
                return true;
            } else {
                return false;
            }
        }).map(trackHead -> trackHead.getId()).collect(Collectors.toList());
        int result = trackHeadMapper.deleteBatchIds(ids);
        if(result > 0){
            for (String id : ids) {
                Map<String,Object> map = new HashMap<>();
                map.put("track_head_id", id);
                trackItemMapper.deleteByMap(map);
                List<TrackHeadRelation> relations = trackHeadRelationMapper.selectList(new QueryWrapper<TrackHeadRelation>().eq("th_id", id));
                for(TrackHeadRelation relation : relations){
                    if(relation.getType().equals("0")){ //输入物料
                        UpdateWrapper<LineStore> update = new UpdateWrapper<>();
                        update.setSql("user_num = user_num - "+relation.getNumber());
                        update.set("status", "1");
                        update.eq("id", relation.getLsId());
                        lineStoreMapper.update(null, update);
                    } else if (relation.getType().equals("1")){ //输出物料
                        lineStoreMapper.deleteById(relation.getLsId());
                    }
                }
            }

            for(TrackHead head: trackHeads){
                Map<String,Object> map = new HashMap<>();
                map.put("work_plan_no", head.getWorkPlanNo());
                map.put("tenant_id", head.getTenantId());
                List<TrackHead> list = trackHeadMapper.selectByMap(map);
                if(list.size() == 0){
                    planService.setPlanStatusNew(head.getWorkPlanNo(), head.getTenantId());
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public IPage<TrackHead> selectTrackHeadRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query){
        return trackHeadMapper.selectTrackHeadRouter(page, query);
    }

    @Override
    public IPage<TrackHead> selectTrackHeadCurrentRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query){
        return trackHeadMapper.selectTrackHeadCurrentRouter(page, query);
    }

    /**
     * 功能描述: 对当前跟单增加计划
     * @Author: xinYu.hou
     * @Date: 2022/4/19 18:07
     * @param documentaryId 跟单Id
     * @param workPlanId 计划Id
     * @return: boolean
     **/
    @Override
    public boolean updateTrackHeadPlan(String documentaryId, String workPlanId) {
        TrackHead trackHead = this.getById(documentaryId);
        trackHead.setWorkPlanId(workPlanId);
        return this.updateById(trackHead);
    }

    @Override
    public Integer queryTrackHeadList(String workPlanId) {
        return trackHeadMapper.selectTrackHeadNumber(workPlanId);
    }
}
