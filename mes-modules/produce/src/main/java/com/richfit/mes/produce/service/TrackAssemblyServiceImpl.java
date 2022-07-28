package com.richfit.mes.produce.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.AESUtil;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackAssemblyMapper;
import com.richfit.mes.produce.entity.*;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource
    private TrackHeadService trackHeadService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssignService trackAssignService;

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
    public Boolean updateComplete(List<String> idList, String itemId) {
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
                assemblyBinding.setItemId(itemId);
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

    @Override
    public List<AssembleKittingVo> kittingExamine(String trackHeadId, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        queryWrapper.eq("is_check", "1");
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", tenantId);
        List<TrackAssembly> trackAssemblyList = this.list(queryWrapper);
        List<AssembleKittingVo> list = new ArrayList<>();
        for (TrackAssembly trackAssembly : trackAssemblyList) {
            AssembleKittingVo assemble = new AssembleKittingVo();
            assemble.setDrawingNo(trackAssembly.getDrawingNo());
            assemble.setMaterialNo(trackAssembly.getMaterialNo());
            assemble.setMaterialName(trackAssembly.getName());
            //需要安装数量
            assemble.setNeedNumber(trackAssembly.getNumber());
            //安装数量
            assemble.setInstallNumber(trackAssembly.getNumberInstall());
            //线边库
            Integer integer = lineStoreMapper.selectTotalNum(trackAssembly.getMaterialNo(), branchCode, tenantId);
            if (integer != null) {
                assemble.setRepertoryNumber(integer);
            } else {
                assemble.setRepertoryNumber(0);
            }
            //可配送数量 WMS库存数量
            assemble.setDeliverableQuantity(queryMaterialCount(trackAssembly.getMaterialNo()));
            //已领取数量
            assemble.setAcquireNumber(0);
            //缺件数量
            assemble.setShortQuantity(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
            assemble.setIsNeedPicking(trackAssembly.getIsNeedPicking());
            assemble.setIsEdgeStore(trackAssembly.getIsEdgeStore());
            assemble.setIsKeyPart(trackAssembly.getIsKeyPart());
            list.add(assemble);
        }
        return list;
    }
    

    @Override
    public ApplicationResult application(AdditionalMaterialDto additionalMaterialDto) {
        TrackHead trackHead = trackHeadService.getById(additionalMaterialDto.getTrackHeadId());
        TrackItem trackItem = trackItemService.getById(additionalMaterialDto.getTiId());
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_id", trackHead.getId());
        queryWrapper.eq("ti_id", trackItem.getId());
        List<Assign> list = trackAssignService.list(queryWrapper);
        IngredientApplicationDto ingredient = new IngredientApplicationDto();
        //申请单号
        ingredient.setSqd(trackItem.getId() + "@0");
        ingredient.setGc(additionalMaterialDto.getBranchCode());
        //车间
        ingredient.setCj(additionalMaterialDto.getBranchCode());
        //车间名称
        //工位 == 车间?
        ingredient.setGw(additionalMaterialDto.getBranchCode());
        //工位名称
        //工序
        ingredient.setGx(trackItem.getId());
        //工序名称
        ingredient.setGxName(trackItem.getOptName());
        //生产订单编号
        ingredient.setScdd(trackHead.getProductionOrder());
        //跟单Id
        ingredient.setGd(trackHead.getId());
        //产品编号
        ingredient.setCp(trackHead.getProductNo());
        //产品名称
        ingredient.setCpName(trackHead.getProductName());
        //优先级
        ingredient.setYxj(Integer.parseInt(trackHead.getPriority()));
        //派工时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSS");
        ingredient.setPgsj(format.format(list.get(0).getAssignTime()));
        //追加物料
        List<LineList> lineLists = new ArrayList<LineList>();
        LineList lineList = new LineList();
        //
        lineList.setMaterialDesc(additionalMaterialDto.getMaterialName());
        lineList.setMaterialNum(additionalMaterialDto.getMaterialNo());
        lineList.setSwFlag(additionalMaterialDto.getIsEdgeStore());
        lineList.setQuantity(additionalMaterialDto.getCount());
        //单位
        lineLists.add(lineList);
        ingredient.setLineList(lineLists);
        ApplicationResult applicationResult = null;
        try {
            applicationResult = anApplicationForm(ingredient);
        } catch (Exception e) {
            ApplicationResult result = new ApplicationResult();
            result.setRetStatus("500");
            result.setRetMsg(e.getMessage());
            e.printStackTrace();
            return result;
        }
        return applicationResult;
    }

    private int queryMaterialCount(String materialNo) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("wstr", materialNo);
        params.put("page", 1);
        params.put("token", "66da1b74a0f22adadc4a865e00435e72");
        String url = "http://10.134.100.21:908/getapi.php";
        String number = HttpUtil.get(url, params);
        if (StringUtil.isNullOrEmpty(number)) {
            return 0;
        }
        String replaceAll = number.replaceAll("\\ufeff", "");
        double value = Double.parseDouble(replaceAll);
        return (int) value;
    }

    private ApplicationResult anApplicationForm(IngredientApplicationDto ingredientApplicationDto) throws Exception {
        String jsonStr = JSONUtil.toJsonStr(ingredientApplicationDto);
//        String aes = AESUtil.decryptAES(jsonStr, "1234123412ABCDEF", "0102030405060708");
        String aes = AESUtil.encrypt(jsonStr, "123");
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", aes);
        String url = "http://11.11.136.204:9081/bsj/mes2barcode/scddUpload?i_data=" + aes;
        String s = HttpUtil.get(url, params, 120000);
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    @Override
    public List<TrackAssembly> queryTrackAssemblyByTrackNo(String trackNo) {
        QueryWrapper<TrackAssembly> wrapper = new QueryWrapper();
        wrapper.eq("track_head_id", trackNo);
        return this.list(wrapper);
    }
}
