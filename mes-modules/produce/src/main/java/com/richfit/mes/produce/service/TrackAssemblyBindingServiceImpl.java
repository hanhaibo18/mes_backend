package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.dao.TrackAssemblyBindingMapper;
import com.richfit.mes.produce.dao.TrackFlowMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: TrackAssemblyBindingService.java
 * @Author: Hou XinYu
 * @Description: 装配绑定记录
 * @CreateTime: 2022年07月18日 10:36:00
 */
@Service
public class TrackAssemblyBindingServiceImpl extends ServiceImpl<TrackAssemblyBindingMapper, TrackAssemblyBinding> implements TrackAssemblyBindingService {

    @Resource
    private TrackAssemblyService trackAssemblyService;
    @Resource
    private LineStoreService lineStoreService;
    @Resource
    private TrackHeadService trackHeadService;
    @Resource
    private BaseServiceClient baseServiceClient;
    @Resource
    private TrackFlowMapper trackFlowMapper;
    @Resource
    private TrackAssemblyBindingMapper trackAssemblyBindingMapper;


    @Override

    public CommonResult<Boolean> saveAssemblyBinding(TrackAssemblyBinding assembly) {
        return CommonResult.success(this.saveOrUpdate(assembly));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateBinding(String id, int isBinding, String itemId) {
        TrackAssemblyBinding assemblyBinding = this.getById(id);
        assemblyBinding.setIsBinding(isBinding);
        assemblyBinding.setItemId(itemId);
        TrackAssembly trackAssembly = trackAssemblyService.getById(assemblyBinding.getAssemblyId());
        TrackHead trackHead = trackHeadService.getById(trackAssembly.getTrackHeadId());
        if (1 == isBinding) {
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() + 1);
            //判断是否是编号来源
            if ("1".equals(trackAssembly.getIsNumFrom())) {
                //生成产品编
                QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("track_head_id", trackHead.getId());
                TrackFlow trackFlow = trackFlowMapper.selectOne(queryWrapper);
                String produceNo = trackAssembly.getDrawingNo() + " " + assemblyBinding.getNumber();
                trackHead.setProductNo(produceNo);
                trackFlow.setProductNo(produceNo);
            }
        } else {
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() - 1);
            trackHead.setProductNo("");
        }
        trackHeadService.updateById(trackHead);
        this.updateById(assemblyBinding);
        trackAssemblyService.updateById(trackAssembly);
        boolean expend = lineStoreService.zpExpend(trackAssembly.getDrawingNo(), assemblyBinding.getNumber(), assemblyBinding.getQuantity(), isBinding);
        if (expend) {
            return CommonResult.success(true, "绑定成功");
        } else {
            throw new GlobalException("绑定失败", ResultCode.FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteAssemblyBinding(String id) {
        TrackAssemblyBinding assemblyBinding = this.getById(id);
        if (null != assemblyBinding.getIsBinding() && 1 == assemblyBinding.getIsBinding()) {
            TrackAssembly trackAssembly = trackAssemblyService.getById(assemblyBinding.getAssemblyId());
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() - assemblyBinding.getQuantity());
            trackAssemblyService.updateById(trackAssembly);
            lineStoreService.zpExpend(trackAssembly.getDrawingNo(), assemblyBinding.getNumber(), assemblyBinding.getQuantity(), 0);
        }
        return CommonResult.success(removeById(id));
    }

    @Override
    public List<TrackAssemblyBinding> queryAssemblyBindingList(String assemblyId) {
        QueryWrapper<TrackAssemblyBinding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assembly_id", assemblyId);
        return this.list(queryWrapper);
    }

    @Override
    public List<TrackAssemblyBinding> queryBindingList(String assemblyIdList) {
        boolean contains = assemblyIdList.contains(",");
        List<String> idList = new ArrayList<>();
        if (contains) {
            idList = Arrays.asList(assemblyIdList.split(","));
        } else {
            idList.add(assemblyIdList);
        }
        List<TrackAssemblyBinding> list = new ArrayList<>();
        idList.forEach(assemblyId -> {
            list.addAll(trackAssemblyBindingMapper.selectAssemblyBindingList(assemblyId));
        });
        return list;
    }

}
