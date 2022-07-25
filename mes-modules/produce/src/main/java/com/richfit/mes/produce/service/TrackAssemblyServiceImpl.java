package com.richfit.mes.produce.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackAssemblyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 马峰
 * @Description 产品装配服务
 */
@Service
public class TrackAssemblyServiceImpl extends ServiceImpl<TrackAssemblyMapper, TrackAssembly> implements TrackAssemblyService {

    @Resource
    private TrackAssemblyBindingService assemblyBindingService;
    @Resource
    private LineStoreMapper lineStoreMapper;

    @Override
    public IPage<TrackAssembly> queryTrackAssemblyPage(Page<TrackAssembly> page, String trackHeadId, String branchCode, String order, String orderCol) {
        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if (order.equals("desc")) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if (order.equals("asc")) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("modify_time");
        }
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        IPage<TrackAssembly> trackAssemblyPage = this.page(page, queryWrapper);
        for (TrackAssembly trackAssembly : trackAssemblyPage.getRecords()) {
            Integer zpNumber = lineStoreMapper.selectTotalNum(trackAssembly.getMaterialNo(), branchCode, SecurityUtils.getCurrentUser().getTenantId());
            if (zpNumber != null) {
                trackAssembly.setNumberInventory(zpNumber);
            } else {
                trackAssembly.setNumberInventory(0);
            }
            trackAssembly.setNumberRemaining(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
            if (trackAssembly.getNumber() == trackAssembly.getNumberInstall()) {
                trackAssembly.setIsComplete(1);
            } else {
                trackAssembly.setIsComplete(0);
            }
            QueryWrapper<TrackAssemblyBinding> queryWrapperBinding = new QueryWrapper<>();
            queryWrapperBinding.eq("assembly_id", trackAssembly.getId());
            queryWrapperBinding.eq("is_binding", 1);
            trackAssembly.setAssemblyBinding(assemblyBindingService.list(queryWrapperBinding));
        }
        return trackAssemblyPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateComplete(List<String> idList) {
        boolean isComplete = false;
        for (String id : idList) {
            TrackAssembly trackAssembly = this.getById(id);
            List<TrackAssemblyBinding> bindingList = assemblyBindingService.queryAssemblyBindingList(id);
            if ("0".equals(trackAssembly.getIsKeyPart()) && bindingList.isEmpty()) {
                trackAssembly.setNumberInstall(trackAssembly.getNumber());
                isComplete = this.updateById(trackAssembly);
                TrackAssemblyBinding assemblyBinding = new TrackAssemblyBinding();
                assemblyBinding.setPartDrawingNo(trackAssembly.getDrawingNo());
                assemblyBinding.setQuantity(trackAssembly.getNumber());
                assemblyBinding.setAssemblyId(id);
                assemblyBinding.setIsBinding(1);
                assemblyBindingService.save(assemblyBinding);
            }
        }
        return isComplete;
    }

    @Override
    public Boolean unbindComplete(List<String> idList) {
        boolean isSucceed = false;
        for (String id : idList) {
            TrackAssembly trackAssembly = this.getById(id);
            if ("0".equals(trackAssembly.getIsKeyPart())) {
                trackAssembly.setNumberInstall(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
                QueryWrapper<TrackAssemblyBinding> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("assembly_id", id);
                this.updateById(trackAssembly);
                isSucceed = assemblyBindingService.remove(queryWrapper);
            }
        }
        return isSucceed;
    }
}
