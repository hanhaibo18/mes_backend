package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.model.produce.ModelApply;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.produce.entity.ModelApplyItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ModelApplyMapper;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    @Autowired
    private ModelApplyItemService modelApplyItemService;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackAssignMapper trackAssignMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> applyModelNew(String branchCode, List<TrackItem> itemInfo) {
        Date now = new Date();
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        if (CollectionUtils.isEmpty(itemInfo)) {
            throw new GlobalException("请选择要请求模型的工序！", ResultCode.FAILED);
        }
        for (TrackItem trackItem : itemInfo) {
            if (!"18".equals(trackItem.getOptType())) {
                throw new GlobalException("只有造型工序可以申请模型！请重试", ResultCode.FAILED);
            } else if (!ObjectUtil.isEmpty(trackItem.getModelStatus()) && trackItem.getModelStatus() == 1) {
                throw new GlobalException("已配送的工序不可重复申请！", ResultCode.FAILED);
            }
            trackItem.setModelStatus(0);
        }
        //将工序按照图号版本号分组
        Map<String, Map<String, List<TrackItem>>> groupByDrawNoAndVer = itemInfo.stream().collect(Collectors.groupingBy(TrackItem::getDrawingNo, Collectors.groupingBy(TrackItem::getOptVer)));
        //检查版本号图号是否重复（重复添加数量和工序信息，不重复新增）
        QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
        groupByDrawNoAndVer.forEach((drawingNo, drawingNoMap) -> drawingNoMap.forEach((ver, item) -> {
            int num = 0;
            modelApplyQueryWrapper.eq("model_drawing_no", drawingNo).eq("model_version", ver)
                    .eq("apply_status", 0);
            ModelApply apply = this.getOne(modelApplyQueryWrapper);
            //有同版本号图号的模型请求时，删除掉申请请求中已请求过的工序，并将没有申请过的数量加进去
            if (!Objects.isNull(apply)) {
                QueryWrapper<ModelApplyItem> modelApplyItemQueryWrapper = new QueryWrapper<>();
                modelApplyItemQueryWrapper.eq("apply_id", apply.getId());
                List<ModelApplyItem> modelApplyItemList = modelApplyItemService.list(modelApplyItemQueryWrapper);
                Set<String> appliedItemIds = modelApplyItemList.stream().map(ModelApplyItem::getItemId).collect(Collectors.toSet());
                item = item.stream().filter(thisItem -> !appliedItemIds.contains(thisItem.getId())).collect(Collectors.toList());
                for (TrackItem trackItem : item) {
                    num += trackItem.getAssignableQty() == null ? 0 : trackItem.getAssignableQty();
                }
                apply.setApplyNum(apply.getApplyNum() + num);
                this.updateById(apply);
            } else {
                //如果没有模型请求，则新增模型请求
                for (TrackItem trackItem : item) {
                    num += trackItem.getAssignableQty() == null ? 0 : trackItem.getAssignableQty();
                }
                apply = new ModelApply();
                apply.setTenantId(tenantId);
                apply.setApplyNum(num);
                apply.setApplyStatus(0);
                apply.setBranchCode(branchCode);
                apply.setModelVersion(ver);
                apply.setModelDrawingNo(drawingNo);
                apply.setApplyTime(now);
                apply.setModelName(item.get(0).getMaterialName());
                this.save(apply);
            }
            List<ModelApplyItem> modelApplyItems = new ArrayList<>();
            for (TrackItem trackItem : item) {
                ModelApplyItem modelApplyItem = new ModelApplyItem();
                modelApplyItem.setItemId(trackItem.getId());
                modelApplyItem.setApplyId(apply.getId());
                modelApplyItems.add(modelApplyItem);
            }
            modelApplyItemService.saveBatch(modelApplyItems);
        }));
        trackItemService.updateBatchById(itemInfo);
        return CommonResult.success(true, "模型请求成功！");
    }

    @Override
    public CommonResult<Page<ModelApply>> getPageInfoNew(int applyStatus, String branchCode, String drawingNo, String startTime, String endTime, int page, int limit) {
        QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
        modelApplyQueryWrapper.eq("apply_status", applyStatus).eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        if (!StringUtils.isNullOrEmpty(startTime)) {
            modelApplyQueryWrapper.apply("UNIX_TIMESTAMP(apply_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            modelApplyQueryWrapper.apply("UNIX_TIMESTAMP(apply_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            modelApplyQueryWrapper.eq("model_drawing_no", drawingNo);
        }
        return CommonResult.success(this.page(new Page<ModelApply>(page, limit), modelApplyQueryWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deliveryNew(String modelId, String modelApplyId) {
        Date now = new Date();
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        ModelApply modelApply = this.getById(modelApplyId);
        HotModelStore model = hotModelStoreService.getById(modelId);
        //配送模型是木制模，根据图号版本号修改所有铸钢车间是当前工序的同图号版本号的造型工序
        if (model.getModelType() == 1) {
            if (model.getNormalNum() < 1) {
                throw new GlobalException("模型库数量不足！", ResultCode.FAILED);
            }
            reusableModels(now, tenantId, modelApply, model);
        }
        //一次性模型，需要匹配申请数量和当前库存数量
        else if (model.getModelType() == 0) {
            //若当前库存数大于申请数量，将所有申请工序调整为已配送
            if (model.getNormalNum() <= 0) {
                throw new GlobalException("该模型已没有库存！", ResultCode.FAILED);
            } else if (model.getNormalNum() >= modelApply.getApplyNum()) {
                adequateStock(now, modelApply, model);
            } //当库存剩余小于申请数量时，需要将当前申请拆分，库存足够派送的部分为已派送，不够的部分为已申请
            else {
                understockOperation(modelId, now, modelApply, model);
            }
        }
        this.updateById(modelApply);
        hotModelStoreService.updateById(model);
        return true;
    }

    private void reusableModels(Date now, String tenantId, ModelApply modelApply, HotModelStore model) {
        model.setNormalNum(model.getNormalNum() - 1);
        modelApply.setModelId(model.getId());
        modelApply.setModelName(model.getModelName());
        modelApply.setAssignNum(1);
        modelApply.setModelType(1);
        modelApply.setApplyStatus(1);
        modelApply.setDeliveryTime(now);
        QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
        trackItemQueryWrapper.eq("is_current", 1).eq("drawing_no", model.getModelDrawingNo())
                .eq("opt_ver", model.getVersion()).eq("tenant_id", tenantId)
                .eq("branch_code", modelApply.getBranchCode()).eq("opt_type", 18)
                .and(wrapper -> wrapper.isNull("model_type").or().ne("model_type", 0));
        List<TrackItem> trackItemList = trackItemService.list(trackItemQueryWrapper);
        for (TrackItem trackItem : trackItemList) {
            trackItem.setModelStatus(1);
            trackItem.setModelType(1);
        }
        trackItemService.updateBatchById(trackItemList);
    }

    private void adequateStock(Date now, ModelApply modelApply, HotModelStore model) {
        model.setNormalNum(model.getNormalNum() - modelApply.getApplyNum());
        modelApply.setModelId(model.getId());
        modelApply.setModelName(model.getModelName());
        modelApply.setAssignNum(modelApply.getApplyNum());
        modelApply.setModelType(0);
        modelApply.setApplyStatus(1);
        modelApply.setDeliveryTime(now);
        //修改申请过该模型的工序模型配送状态为已配送
        QueryWrapper<ModelApplyItem> modelApplyItemQueryWrapper = new QueryWrapper<>();
        modelApplyItemQueryWrapper.eq("apply_id", modelApply.getId());
        List<ModelApplyItem> modelApplyItemList = modelApplyItemService.list(modelApplyItemQueryWrapper);
        Set<String> itemIdSet = modelApplyItemList.stream().map(ModelApplyItem::getItemId).collect(Collectors.toSet());
        UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
        trackItemUpdateWrapper.set("model_status", 1).set("model_type", 0).in("id", itemIdSet);
        trackItemService.update(trackItemUpdateWrapper);
    }

    private void understockOperation(String modelId, Date now, ModelApply modelApply, HotModelStore model) {
        QueryWrapper<ModelApplyItem> modelApplyItemQueryWrapper = new QueryWrapper<>();
        modelApplyItemQueryWrapper.eq("apply_id", modelApply.getId());
        List<ModelApplyItem> modelApplyItemList = modelApplyItemService.list(modelApplyItemQueryWrapper);
        Set<String> itemIdSet = modelApplyItemList.stream().map(ModelApplyItem::getItemId).collect(Collectors.toSet());
        QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
        trackItemQueryWrapper.in("id", itemIdSet);
        List<TrackItem> trackItemList = trackAssignMapper.getPageAssignsHot(trackItemQueryWrapper);
        trackItemList = trackItemList.stream().sorted(Comparator.nullsLast(Comparator.comparing(TrackItem::getPlanEndTime))).collect(Collectors.toList());
        List<TrackItem> assignItemList = new ArrayList<>();
        Integer normalNum = model.getNormalNum();
        Integer assignNum = 0;
        for (TrackItem item : trackItemList) {
            if (normalNum == 0) {
                break;
            }
            if (item.getAssignableQty() <= normalNum) {
                assignNum += item.getAssignableQty();
                normalNum -= item.getAssignableQty();
                assignItemList.add(item);
            }
        }
        if (CollectionUtils.isEmpty(assignItemList)) {
            throw new GlobalException("剩余数量不足以完成一个跟单！", ResultCode.FAILED);
        }
        modelApply.setApplyNum(modelApply.getApplyNum() - assignNum);
        model.setNormalNum(model.getNormalNum() - assignNum);
        QueryWrapper<ModelApplyItem> modelApplyItemQueryWrapperDelete = new QueryWrapper<>();
        modelApplyItemQueryWrapperDelete.eq("apply_id", modelApply.getId()).
                in("item_id", assignItemList.stream().map(TrackItem::getId).collect(Collectors.toSet()));
        modelApplyItemService.remove(modelApplyItemQueryWrapperDelete);
        //新建一个已配送的模型请求
        ModelApply modelApplyNew = new ModelApply();
        modelApplyNew.setModelType(0);
        modelApplyNew.setTenantId(modelApply.getTenantId());
        modelApplyNew.setBranchCode(modelApply.getBranchCode());
        modelApplyNew.setModelId(modelId);
        modelApplyNew.setModelName(model.getModelName());
        modelApplyNew.setModelDrawingNo(modelApply.getModelDrawingNo());
        modelApplyNew.setApplyNum(assignNum);
        modelApplyNew.setAssignNum(assignNum);
        modelApplyNew.setModelVersion(model.getVersion());
        modelApplyNew.setApplyStatus(1);
        modelApplyNew.setApplyTime(modelApply.getApplyTime());
        modelApplyNew.setDeliveryTime(now);
        this.save(modelApplyNew);
        List<ModelApplyItem> modelApplyItems = new ArrayList<>();
        for (TrackItem trackItem : assignItemList) {
            ModelApplyItem modelApplyItem = new ModelApplyItem();
            modelApplyItem.setApplyId(modelApplyNew.getId());
            modelApplyItem.setItemId(trackItem.getId());
            modelApplyItems.add(modelApplyItem);
        }
        modelApplyItemService.saveBatch(modelApplyItems);
        UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
        trackItemUpdateWrapper.set("model_status", 1).set("model_type", 0).in("id", assignItemList.stream()
                .map(TrackItem::getId).collect(Collectors.toSet()));
        trackItemService.update(trackItemUpdateWrapper);
    }

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
                applyWrapper.eq("tenant_id", tenantId).eq("model_drawing_no", trackItem.getDrawingNo()).eq("item_id", trackItem.getId()).eq("model_version", trackItem.getOptVer());
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
                applyWrapper.eq("tenant_id", tenantId).eq("model_id", modelInfo.getId()).ne("apply_status", 2);
                List<ModelApply> applyList = this.list(applyWrapper);
                if (CollectionUtils.isNotEmpty(applyList)) {
                    appliedList.add(trackItem);
                    break;
                }
            }
            //可重复模型查询是否有退库过的
            if (modelInfo.getModelType() != null && modelInfo.getModelType().equals(1)) {
                QueryWrapper<ModelApply> applyWrapper = new QueryWrapper<>();
                applyWrapper.eq("tenant_id", tenantId).eq("model_id", modelInfo.getId()).eq("apply_status", 2);
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
                hotModelStoreQueryWrapper.eq("tenant_id", tenantId).eq("model_drawing_no", modelApply.getModelDrawingNo()).eq("version", modelApply.getModelVersion());
                HotModelStore modelInfo = hotModelStoreService.getOne(hotModelStoreQueryWrapper);
                if (modelInfo == null || modelInfo.getNormalNum() <= 0) {
                    throw new GlobalException("库存数量不足！", ResultCode.FAILED);
                }
                //更新模型库存数量
                modelInfo.setNormalNum(Math.max(modelInfo.getNormalNum() - modelApply.getApplyNum(), 0));
                //拿到未配送的申请列表,更改配送状态为已配送
                QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
                modelApplyQueryWrapper.eq("tenant_id", tenantId).eq("branch_code", modelApply.getBranchCode()).eq("model_drawing_no", modelApply.getModelDrawingNo()).eq("model_version", modelApply.getModelVersion()).eq("apply_status", 0).orderByAsc("plan_finish_time");
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
                modelApplyQueryWrapper.eq("tenant_id", tenantId).eq("branch_code", modelApply.getBranchCode()).eq("model_drawing_no", modelApply.getModelDrawingNo()).eq("model_version", modelApply.getModelVersion()).eq("apply_status", 0);
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
    @Transactional(rollbackFor = Exception.class)
    public Boolean sendBack(List<ModelApply> modelApplyList) {
        Date now = new Date();
        List<ModelApply> updateList = new ArrayList<>();
        for (ModelApply modelApply : modelApplyList) {
            if (modelApply.getModelType() != null && modelApply.getModelType().equals(0)) {
                throw new GlobalException("气化模不可退库！", ResultCode.FAILED);
            }
            QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
            modelApplyQueryWrapper.eq("tenant_id", modelApply.getTenantId()).eq("branch_code", modelApply.getBranchCode()).eq("model_drawing_no", modelApply.getModelDrawingNo()).eq("model_version", modelApply.getModelVersion()).eq("model_type", 1);
            ModelApply apply = this.getOne(modelApplyQueryWrapper);
            apply.setApplyStatus(2);
            apply.setBackTime(now);
            updateList.add(apply);
            updateItemList(apply);
        }
        return this.updateBatchById(updateList);
    }

    private void updateItemList(ModelApply apply) {
        UpdateWrapper<TrackItem> trackItemQueryWrapper = new UpdateWrapper<>();
        trackItemQueryWrapper.eq("tenant_id", apply.getTenantId()).eq("branch_code", apply.getBranchCode())
                .eq("opt_type", 18).eq("is_current", 1).eq("model_status", 1).eq("model_type", 1)
                .set("model_status", 2);
        trackItemService.update(trackItemQueryWrapper);
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

