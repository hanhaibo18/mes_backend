package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.dao.TrackAssemblyBindingMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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


    @Override
    public CommonResult<Boolean> saveAssemblyBinding(TrackAssemblyBinding assembly) {
        return CommonResult.success(this.saveOrUpdate(assembly));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateBinding(String id, int isBinding) {
        TrackAssemblyBinding assemblyBinding = this.getById(id);
        assemblyBinding.setIsBinding(isBinding);
        TrackAssembly trackAssembly = trackAssemblyService.getById(assemblyBinding.getAssemblyId());
        TrackHead trackHead = trackHeadService.getById(trackAssembly.getTrackHeadId());
        if (1 == isBinding) {
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() + 1);
            if (null != trackHead && StringUtils.isNullOrEmpty(trackHead.getProductNo())) {
                ProjectBom projectBom = baseServiceClient.queryBom(trackHead.getProjectBomWork(), trackAssembly.getBranchCode());
                if (projectBom != null) {
                    trackHead.setProductNo(projectBom.getDrawingNo() + " " + assemblyBinding.getNumber());
                } else {
                    trackHead.setProductNo(trackHead.getDrawingNo() + " " + assemblyBinding.getNumber());
                }
            }
        } else {
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() - 1);
            trackHead.setProductNo("");
        }
        trackHeadService.updateById(trackHead);
        this.updateById(assemblyBinding);
        trackAssemblyService.updateById(trackAssembly);
        Boolean expend = lineStoreService.zpExpend(trackAssembly.getDrawingNo(), assemblyBinding.getNumber(), assemblyBinding.getQuantity(), isBinding);
        return CommonResult.success(expend);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteAssemblyBinding(String id) {
        TrackAssemblyBinding assemblyBinding = this.getById(id);
        if (null != assemblyBinding.getIsBinding() && 1 == assemblyBinding.getIsBinding()) {
            TrackAssembly trackAssembly = trackAssemblyService.getById(assemblyBinding.getAssemblyId());
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() - 1);
            trackAssemblyService.updateById(trackAssembly);
        }
        return CommonResult.success(removeById(id));
    }

    @Override
    public List<TrackAssemblyBinding> queryAssemblyBindingList(String assemblyId) {
        QueryWrapper<TrackAssemblyBinding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assembly_id", assemblyId);
        return this.list(queryWrapper);
    }

}
