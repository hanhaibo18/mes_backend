package com.richfit.mes.produce.service.heat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.util.OptNameUtil;
import com.richfit.mes.produce.dao.PrechargeFurnaceMapper;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.service.TrackItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhiqiang.lu
 * @Date: 2023.1.4
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PrechargeFurnaceServiceImpl extends ServiceImpl<PrechargeFurnaceMapper, PrechargeFurnace> implements PrechargeFurnaceService {


    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    public TrackAssignMapper trackAssignMapper;

    @Override
    public void furnaceCharging(List<Assign> assignList, String tempWork) {
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要有装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = new PrechargeFurnace();
        prechargeFurnace.setTempWork(tempWork);
        prechargeFurnace.setOptName(optNames(assignList));
        prechargeFurnace.setSiteId(assignList.get(0).getSiteId());
        prechargeFurnace.setTypeCode(assignList.get(0).getTypeCode());
        this.save(prechargeFurnace);
        for (Assign assign : assignList) {
            //跟单工序添加装炉id
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            trackItem.setPrechargeFurnaceId(prechargeFurnace.getId());
            trackItemService.updateById(trackItem);
        }
    }

    @Override
    public List<Assign> queryTrackItem(Long id) {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u.precharge_furnace_id", id);
        return trackAssignMapper.queryListAssignTrackStore(queryWrapper);
    }

    @Override
    public PrechargeFurnace addTrackItem(List<Assign> assignList) {
        //预装炉未开工状态
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要选择添加预装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = this.getById(assignList.get(0).getPrechargeFurnaceId());
        if (!PrechargeFurnace.STATE_WKG.equals(prechargeFurnace.getStatus())) {
            throw new GlobalException("只能添加未开工的预装炉的工序", ResultCode.FAILED);
        }
        for (Assign assign : assignList) {
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", assign.getTiId());
            updateWrapper.set("precharge_furnace_id", assign.getPrechargeFurnaceId());
            trackItemService.update(updateWrapper);
        }
        prechargeFurnace.setOptName(optNames(this.queryTrackItem(prechargeFurnace.getId())));

        this.updateById(prechargeFurnace);
        return prechargeFurnace;
    }

    @Override
    public PrechargeFurnace deleteTrackItem(List<Assign> assignList) {
        //预装炉未开工状态
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要选择删除预装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = this.getById(assignList.get(0).getPrechargeFurnaceId());
        if (!PrechargeFurnace.STATE_WKG.equals(prechargeFurnace.getStatus())) {
            throw new GlobalException("只能删除未开工的预装炉的工序", ResultCode.FAILED);
        }
        for (Assign assign : assignList) {
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", assign.getTiId());
            updateWrapper.set("precharge_furnace_id", null);
            trackItemService.update(updateWrapper);
        }
        prechargeFurnace.setOptName(optNames(this.queryTrackItem(prechargeFurnace.getId())));
        //设备类型赋值
        prechargeFurnace.setTypeCode(assignList.get(0).getTypeCode());
        this.updateById(prechargeFurnace);
        return prechargeFurnace;
    }

    private String optNames(List<Assign> assignList) {
        if (assignList == null || assignList.size() == 0) {
            return "";
        }
        Set<String> optNames = new HashSet<>();
        String name = "";
        for (Assign assign : assignList) {
            //拼接工序名称
            name = OptNameUtil.optName(assign.getOptName());
            optNames.add(name);
        }
        if (optNames.size() != 1) {
            throw new GlobalException("只有工序名称相同的工序才能放到一个预装炉内！", ResultCode.FAILED);
        }
        return name;
    }
}
