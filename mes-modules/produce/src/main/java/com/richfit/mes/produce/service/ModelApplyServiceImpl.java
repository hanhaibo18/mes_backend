package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.produce.ModelApply;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ModelApplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (ModelApply)表服务实现类
 *
 * @author makejava
 * @since 2023-04-23 14:49:09
 */
@Service
public class ModelApplyServiceImpl extends ServiceImpl<ModelApplyMapper, ModelApply> implements ModelApplyService {

    @Autowired
    private HotModelStoreService hotModelStoreService;
    @Autowired
    private ModelApplyMapper modelApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> applyModel(String branchCode, List<TrackItem> itemInfo) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        if (CollectionUtils.isEmpty(itemInfo)) {
            throw new GlobalException("请选择要请求模型的工序！", ResultCode.FAILED);
        }
        //记录已申请过的工序
        List<TrackItem> appliedList = new ArrayList<>();
        //记录根据图号和版本号查不到模型的工序
        List<TrackItem> noModelList = new ArrayList<>();
        for (TrackItem trackItem : itemInfo) {
            //先根据图号找到对应的模型信息
            QueryWrapper<HotModelStore> modelWrapper = new QueryWrapper<>();
            modelWrapper.eq("tenant_id", tenantId).eq("model_drawing_no", trackItem.getDrawingNo()).eq("version", trackItem.getOptVer());
            HotModelStore modelInfo = hotModelStoreService.getOne(modelWrapper);
            if (modelInfo == null) {
                noModelList.add(trackItem);
                break;
            }
            //一次性的模型查询该工序是否已经申请过模型请求
            if (modelInfo.getModelType() != null && modelInfo.getModelType().equals(0)) {
                QueryWrapper<ModelApply> applyWrapper = new QueryWrapper<>();
                applyWrapper.eq("tenant_id", tenantId).eq("model_drawing_no", trackItem.getDrawingNo())
                        .eq("item_id", trackItem.getId()).eq("model_version", trackItem.getOptVer());
                List<ModelApply> applyList = this.list(applyWrapper);
                //如果申请过继续下一条
                if (CollectionUtils.isNotEmpty(applyList)) {
                    appliedList.add(trackItem);
                    break;
                }
            }
            //可重复的模型查询该模型是否被申请过且没有退库
            if (modelInfo.getModelType() != null && modelInfo.getModelType().equals(1)) {
                QueryWrapper<ModelApply> applyWrapper = new QueryWrapper<>();
                applyWrapper.eq("tenant_id", tenantId).eq("model_id", modelInfo.getId())
                        .ne("apply_status", 2);
                List<ModelApply> applyList = this.list(applyWrapper);
                if (CollectionUtils.isNotEmpty(applyList)) {
                    appliedList.add(trackItem);
                    break;
                }
            }
            //可重复模型查询是否有退库过的
            if (modelInfo.getModelType() != null && modelInfo.getModelType().equals(1)) {
                QueryWrapper<ModelApply> applyWrapper = new QueryWrapper<>();
                applyWrapper.eq("tenant_id", tenantId).eq("model_id", modelInfo.getId())
                        .eq("apply_status", 2);
                ModelApply apply = this.getOne(applyWrapper);
                if (apply != null) {
                    apply.setDeliveryTime(null);
                    apply.setApplyStatus(0);
                    this.updateById(apply);
                    break;
                }
            }

            //构建模型申请信息
            ModelApply modelApply = new ModelApply();
            modelApply.setTenantId(tenantId);
            modelApply.setModelId(modelInfo.getId());
            modelApply.setBranchCode(branchCode);
            modelApply.setModelName(modelInfo.getModelName());
            modelApply.setModelDrawingNo(trackItem.getDrawingNo());
            modelApply.setApplyNum(1);
            modelApply.setModelType(modelInfo.getModelType());
            modelApply.setModelVersion(modelInfo.getVersion());
            modelApply.setItemId(trackItem.getId());
            modelApply.setApplyStatus(0);
            modelApply.setApplyTime(new Date());
            modelApply.setPlanFinishTime(new Date());
            this.save(modelApply);
        }
        if (CollectionUtils.isNotEmpty(noModelList)) {
            Map<String, String> drawNoVerMap = noModelList.stream().collect(Collectors.toMap(x -> x.getDrawingNo(), x -> x.getOptVer()));
            throw new GlobalException("以下工序查询不到模型信息：图号=版本号" + drawNoVerMap, ResultCode.FAILED);
        }
        return CommonResult.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delivery(List<ModelApply> modelApplyList) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        for (ModelApply modelApply : modelApplyList) {
            //如果模型是气化模,按照计划完成时间修改配送状态
            if (modelApply.getModelType() != null && modelApply.getModelType().equals(0)) {
                //根据图号和版本号获取库存信息
                QueryWrapper<HotModelStore> hotModelStoreQueryWrapper = new QueryWrapper<>();
                hotModelStoreQueryWrapper.eq("tenant_id", tenantId).eq("model_drawing_no", modelApply.getModelDrawingNo())
                        .eq("version", modelApply.getModelVersion());
                HotModelStore modelInfo = hotModelStoreService.getOne(hotModelStoreQueryWrapper);
                if (modelInfo == null || modelInfo.getNormalNum() <= 0) {
                    throw new GlobalException("库存数量不足！", ResultCode.FAILED);
                }
                //更新模型库存数量
                modelInfo.setNormalNum(Math.max(modelInfo.getNormalNum() - modelApply.getApplyNum(), 0));
                //拿到未配送的申请列表,更改配送状态为已配送
                QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
                modelApplyQueryWrapper.eq("tenant_id", tenantId).eq("branch_code", modelApply.getBranchCode())
                        .eq("model_drawing_no", modelApply.getModelDrawingNo()).eq("model_version", modelApply.getModelVersion())
                        .eq("apply_status", 0).orderByAsc("plan_finish_time");
                List<ModelApply> notDeliveryApplyList = this.list(modelApplyQueryWrapper);
                if (CollectionUtils.isNotEmpty(notDeliveryApplyList)) {
                    for (int i = 0; i < (modelInfo.getNormalNum() > modelApply.getApplyNum() ? modelApply.getApplyNum() : modelInfo.getNormalNum()); i++) {
                        notDeliveryApplyList.get(i).setApplyStatus(1);
                        notDeliveryApplyList.get(i).setDeliveryTime(new Date());
                    }
                }
                //更新模型申请
                this.updateBatchById(notDeliveryApplyList);
            }
            //如果模型是木制模 直接修改状态为已配送
            if (modelApply.getModelType() != null && modelApply.getModelType().equals(1)) {
                QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
                modelApplyQueryWrapper.eq("tenant_id", tenantId).eq("branch_code", modelApply.getBranchCode())
                        .eq("model_drawing_no", modelApply.getModelDrawingNo()).eq("model_version", modelApply.getModelVersion())
                        .eq("apply_status", 0);
                ModelApply apply = this.getOne(modelApplyQueryWrapper);
                if (apply != null) {
                    apply.setApplyStatus(1);
                    apply.setDeliveryTime(new Date());
                    this.updateById(apply);
                }
            }
        }
        return true;
    }

    @Override
    public Boolean sendBack(List<ModelApply> modelApplyList) {
        List<ModelApply> updateList = new ArrayList<>();
        for (ModelApply modelApply : modelApplyList) {
            if (modelApply.getModelType() != null && modelApply.getModelType().equals(0)) {
                throw new GlobalException("气化模不可退库！", ResultCode.FAILED);
            }
            QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
            modelApplyQueryWrapper.eq("tenant_id", modelApply.getTenantId()).eq("branch_code", modelApply.getBranchCode())
                    .eq("model_drawing_no", modelApply.getModelDrawingNo()).eq("model_version", modelApply.getModelVersion())
                    .eq("model_type", 1);
            ModelApply apply = this.getOne(modelApplyQueryWrapper);
            apply.setApplyStatus(2);
            updateList.add(apply);
        }
        return this.updateBatchById(updateList);
    }

    @Override
    public CommonResult<Page<ModelApply>> getPageInfo(int sign, String branchCode, String drawingNo, String startTime, String endTime, int page, int limit) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        List<ModelApply> modelApplyList = modelApplyMapper.getModelApplyList(tenantId, sign, branchCode, drawingNo, startTime, endTime, page, limit);
        Page<ModelApply> pageInfo = new Page<>();
        pageInfo.setTotal(modelApplyList.size());
        pageInfo.setCurrent(page);
        pageInfo.setSize(limit);
        pageInfo.setRecords(modelApplyList.stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList()));
        return CommonResult.success(pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deliveryActive(List<HotModelStore> hotModelStoreList) {
        if (hotModelStoreList == null) {
            throw new GlobalException(ResultCode.FAILED);
        }
        List<HotModelStore> updateModelList = new ArrayList<>();
        List<ModelApply> updateApplyList = new ArrayList<>();
        for (HotModelStore hotModelStore : hotModelStoreList) {
            //查询该模型是否被请求过
            QueryWrapper<ModelApply> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_id", hotModelStore.getTenantId()).eq("model_id", hotModelStore.getId()).orderByAsc("plan_finish_time");
            List<ModelApply> modelApplyList = this.list(queryWrapper);
            if (CollectionUtils.isEmpty(modelApplyList)) {
                break;
            }
            //如果是木制模，修改模型请求状态为已配送
            if (hotModelStore.getModelType() != null && hotModelStore.getModelType().equals(1)) {
                for (ModelApply modelApply : modelApplyList) {
                    modelApply.setApplyStatus(1);
                }
                updateApplyList.addAll(modelApplyList);
            }
            //气化模先看库存和需求哪个大
            if (hotModelStore.getModelType() != null && hotModelStore.getModelType().equals(0)) {
                //库存大于需求，全部派送并消耗库存
                if (hotModelStore.getNormalNum() - modelApplyList.size() >= 0) {
                    hotModelStore.setNormalNum(hotModelStore.getNormalNum() - modelApplyList.size());
                    updateModelList.add(hotModelStore);
                    for (ModelApply modelApply : modelApplyList) {
                        modelApply.setApplyStatus(1);
                    }
                    updateApplyList.addAll(modelApplyList);
                } else if (hotModelStore.getNormalNum() > 0 && hotModelStore.getNormalNum() - modelApplyList.size() < 0) {
                    //库存小于请求数，邮箱更新计划时间排名靠前的
                    for (int i = 0; i < hotModelStore.getNormalNum(); i++) {
                        modelApplyList.get(i).setApplyStatus(1);
                        updateApplyList.add(modelApplyList.get(i));
                    }
                    hotModelStore.setNormalNum(0);
                    updateModelList.add(hotModelStore);
                }
            }
        }
        this.updateBatchById(updateApplyList);
        hotModelStoreService.updateBatchById(updateModelList);
        return true;
    }
}

