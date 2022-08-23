package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;
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
            if (null != trackHead && StringUtils.isNullOrEmpty(trackHead.getProductNo())) {
                //生成产品编号
                ProjectBom projectBom = baseServiceClient.queryBom(trackHead.getProjectBomWork(), trackAssembly.getBranchCode());
                QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("track_head_id", trackHead.getId());
                TrackFlow trackFlow = trackFlowMapper.selectOne(queryWrapper);
                if (projectBom != null) {
                    String produceNo = projectBom.getDrawingNo() + " " + assemblyBinding.getNumber();
                    trackHead.setProductNo(produceNo);
                    trackFlow.setProductNo(produceNo);
                } else {
                    String produceNo = trackHead.getDrawingNo() + " " + assemblyBinding.getNumber();
                    trackHead.setProductNo(trackHead.getDrawingNo() + " " + assemblyBinding.getNumber());
                    trackFlow.setProductNo(produceNo);
                }
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
            return CommonResult.failed("绑定失败");
        }
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
