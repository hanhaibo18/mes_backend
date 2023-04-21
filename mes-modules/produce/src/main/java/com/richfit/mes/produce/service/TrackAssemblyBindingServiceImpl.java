package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssemblyBindingMapper;
import com.richfit.mes.produce.dao.TrackFlowMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private TrackHeadFlowService flowService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssemblyBindingMapper trackAssemblyBindingMapper;


    @Override
    public CommonResult<Boolean> saveAssemblyBinding(TrackAssemblyBinding assembly) {
        assembly.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        QueryWrapper<TrackAssemblyBinding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("number", assembly.getNumber());
        DrawingNoUtil.queryEq(queryWrapper, "part_drawing_no", assembly.getPartDrawingNo());
        queryWrapper.eq("branch_code", assembly.getBranchCode());
        queryWrapper.eq("tenant_id", assembly.getTenantId());
        List<TrackAssemblyBinding> bindingList = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(bindingList)) {
            throw new GlobalException("新增失败,编号以使用", ResultCode.FAILED);
        }
        TrackAssembly trackAssembly = trackAssemblyService.getById(assembly.getAssemblyId());
        if (trackAssembly.getNumber() == trackAssembly.getNumberInstall()) {
            throw new GlobalException("新增失败,零件已全部绑定", ResultCode.FAILED);
        }
        return CommonResult.success(this.saveOrUpdate(assembly));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateBinding(String id, int isBinding, String itemId,String branchCode) {
        TrackAssemblyBinding assemblyBinding = this.getById(id);
        //校验编号是否被其他跟单绑定过
        QueryWrapper<TrackAssemblyBinding> trackAssemblyBindingQueryWrapper = new QueryWrapper<>();
        trackAssemblyBindingQueryWrapper.eq("number",assemblyBinding.getNumber())
                .eq("is_binding",1)
                .ne("id",id);
        List<TrackAssemblyBinding> repeats = this.list(trackAssemblyBindingQueryWrapper);
        if(repeats.size()>0){
            Set<String> assemblyIds = repeats.stream().map(item -> item.getAssemblyId()).collect(Collectors.toSet());
            //校验是不是此车间下的数据  如果是提示错误信息（已被绑定）
            QueryWrapper<TrackAssembly> trackAssemblyQueryWrapper = new QueryWrapper<>();
            trackAssemblyQueryWrapper.in("id",assemblyIds)
                    .eq("branch_code",branchCode);
            List<TrackAssembly> trackAssemblies = trackAssemblyService.list(trackAssemblyQueryWrapper);
            Set<String> trackNos = trackAssemblies.stream().map(item -> item.getTrackNo()).collect(Collectors.toSet());
            String join = String.join(",",new ArrayList<>(trackNos));
            if(StringUtils.isNotBlank(join)){
                throw new GlobalException("编码:"+assemblyBinding.getNumber()+",已被跟单"+join+"绑定",ResultCode.FAILED);
            }
        }

        assemblyBinding.setIsBinding(isBinding);
        assemblyBinding.setItemId(itemId);
        TrackAssembly trackAssembly = trackAssemblyService.getById(assemblyBinding.getAssemblyId());
        TrackHead trackHead = trackHeadService.getById(trackAssembly.getTrackHeadId());
        QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHead.getId());
        TrackFlow trackFlow;
        try {
            trackFlow = flowService.getOne(queryWrapper);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("异常提示跟单数据异常出现多个生产流程", ResultCode.FAILED);
        }
        if (1 == isBinding) {
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() + assemblyBinding.getQuantity());
            //判断是否是编号来源
            if ("1".equals(trackAssembly.getIsNumFrom())) {
                //生成产品编
                String produceNo = trackAssembly.getDrawingNo() + " " + assemblyBinding.getNumber();
                trackHead.setProductNo(assemblyBinding.getNumber());
                trackHead.setProductNoDesc(produceNo);
                trackFlow.setProductNo(produceNo);
                //修改工序表中产品编号
                UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("track_head_id", trackHead.getId());
                updateWrapper.set("product_no", produceNo);
                trackItemService.update(updateWrapper);
//                trackHeadService.changeProductNo();
                //料单入库生成入库单
//                LineStore lineStore = new LineStore();
//                lineStore.setTenantId(trackAssembly.getTenantId());
//                lineStore.setTrackType("0");
//                lineStore.setMaterialType("1");
//                lineStore.setInTime(new Date());
//                lineStore.setMaterialNo(trackAssembly.getMaterialNo());
//                lineStore.setInputType("2");
//                lineStore.setProductName(trackAssembly.getName());
//                lineStore.setMaterialName(trackAssembly.getName());
//                lineStore.setBranchCode(trackAssembly.getBranchCode());
//                lineStore.setProdNo(produceNo);
//                lineStore.setDrawingNo(trackAssembly.getDrawingNo());
//                lineStore.setStatus("1");
//                lineStoreService.save(lineStore);
//                trackAssembly.setLineStoreId(lineStore.getId());
            }
            //绑定
//            String expend = lineStoreService.zpExpend(trackAssembly.getTrackHeadId(), trackFlow.getId(), trackAssembly.getMaterialNo(), assemblyBinding.getQuantity(), trackHead.getBranchCode(), trackHead.getTenantId());
//            assemblyBinding.setLineStoreId(expend);
        } else {
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() - assemblyBinding.getQuantity());
            trackHead.setProductNo("");
            trackHead.setProductNoDesc("");
            trackFlow.setProductNo("");
//            lineStoreService.unbundling(assemblyBinding.getLineStoreId());
            trackAssembly.setLineStoreId("");
            lineStoreService.removeById(trackAssembly.getLineStoreId());
        }
        try {
            this.updateById(assemblyBinding);
            trackHeadService.updateById(trackHead);
            flowService.updateById(trackFlow);
            trackAssemblyService.updateById(trackAssembly);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("绑定失败", ResultCode.FAILED);
        }
        return CommonResult.success(true, "绑定成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> deleteAssemblyBinding(String id) {
        TrackAssemblyBinding assemblyBinding = this.getById(id);
        if (null != assemblyBinding.getIsBinding() && 1 == assemblyBinding.getIsBinding()) {
            TrackAssembly trackAssembly = trackAssemblyService.getById(assemblyBinding.getAssemblyId());
            trackAssembly.setNumberInstall(trackAssembly.getNumberInstall() - assemblyBinding.getQuantity());
            trackAssemblyService.updateById(trackAssembly);
            //解绑
            lineStoreService.unbundling(assemblyBinding.getLineStoreId());
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
