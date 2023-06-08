package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.BaseProductReceiptExtendMapper;
import com.richfit.mes.base.dao.BaseProductReceiptMapper;
import com.richfit.mes.base.enmus.ReceiptStatusEnum;
import com.richfit.mes.base.entity.ReceiptDTO;
import com.richfit.mes.base.entity.ReceiptExtendDTO;
import com.richfit.mes.base.util.BeanUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.BaseProductReceipt;
import com.richfit.mes.common.model.base.BaseProductReceiptExtend;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangchenyu
 * @description 针对表【base_product_receipt(产品交接单据)】的数据库操作Service实现
 * @createDate 2023-06-05 09:23:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseProductReceiptServiceImpl extends ServiceImpl<BaseProductReceiptMapper, BaseProductReceipt> implements BaseProductReceiptService {

    @Autowired
    private BaseProductReceiptMapper baseProductReceiptMapper;

    @Autowired
    private BaseProductReceiptExtendMapper baseProductReceiptExtendMapperMapper;

    @Autowired
    private BaseProductReceiptExtendService baseProductReceiptExtendService;

    @Autowired
    private ProjectBomService projectBomService;

    @Override
    public Page queryReceiptInfo(ReceiptDTO receiptDTO) {
        Page<BaseProductReceipt> page = new Page<>(receiptDTO.getPage(), receiptDTO.getLimit());
        LambdaQueryWrapper<BaseProductReceipt> baseProductReceiptLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //时间降序
        baseProductReceiptLambdaQueryWrapper.orderByDesc(BaseProductReceipt::getCreateDate);
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(receiptDTO.getConnectNo()), BaseProductReceipt::getConnectNo, receiptDTO.getConnectNo());
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(receiptDTO.getDriNo()), BaseProductReceipt::getDriNo, receiptDTO.getDriNo());
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(receiptDTO.getWorkNo()), BaseProductReceipt::getWorkNo, receiptDTO.getWorkNo());
        //车间查询
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(receiptDTO.getBranchCode()), BaseProductReceipt::getBranchCode, receiptDTO.getBranchCode());
        //产品编号查询
        baseProductReceiptLambdaQueryWrapper.like(StringUtils.isNotEmpty(receiptDTO.getProductNo()), BaseProductReceipt::getWorkNo, receiptDTO.getWorkNo());
        //时间区间查询
        baseProductReceiptLambdaQueryWrapper.ge(StringUtils.isNotEmpty(receiptDTO.getStartTime()), BaseProductReceipt::getCreateDate, receiptDTO.getStartTime());
        baseProductReceiptLambdaQueryWrapper.le(StringUtils.isNotEmpty(receiptDTO.getEndTime()), BaseProductReceipt::getCreateDate, receiptDTO.getEndTime());
        //产品名称查询
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(receiptDTO.getProdDesc()), BaseProductReceipt::getProdDesc, receiptDTO.getProdDesc());
        //验收单位；
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(receiptDTO.getReceiveUnit()), BaseProductReceipt::getReceiveUnit, receiptDTO.getReceiveUnit());
        return baseProductReceiptMapper.selectPage(page, baseProductReceiptLambdaQueryWrapper);
    }

    @Override
    public List queryReceiptDetailInfo(String connectId, Integer number, String workNo, String drawNo, String branchCode, String tenantId) {
        LambdaQueryWrapper<BaseProductReceiptExtend> baseProductReceiptExtendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductReceiptExtendLambdaQueryWrapper.eq(BaseProductReceiptExtend::getConnectId, connectId);
        List<BaseProductReceiptExtend> baseProductReceiptExtends = baseProductReceiptExtendMapperMapper.selectList(baseProductReceiptExtendLambdaQueryWrapper);
        //BOM零件数量计算
        //根据零件图号分组，查询交接单下的零部件数量；
        List<ProjectBom> projectBomPartByIdList = projectBomService.getProjectBomPartList(workNo, drawNo, tenantId, branchCode);
        Map<String, List<ProjectBom>> collect = projectBomPartByIdList.stream().collect(Collectors.groupingBy(item -> item.getDrawingNo()));
        //只将勾选的进行汇总计算；
        Map<String, List<BaseProductReceiptExtend>> collect1 = baseProductReceiptExtends.stream().filter(e -> e.getIsCheck() == 1).collect(Collectors.groupingBy(item -> item.getPartDrawingNo()));
        for (BaseProductReceiptExtend record : baseProductReceiptExtends) {
            //BOM零件数量计算
            //根据零件图号分组，查询交接单下的零部件数量；
            if (record.getIsCheck() == 1) {
                //之前有的图号数据；
                if (collect.containsKey(record.getPartDrawingNo())) {
                    //BOM零件数量；将相同图号数量进行累加；
                    int sum = collect.get(record.getPartDrawingNo()).stream().mapToInt(e -> e.getNumber()).sum();
                    record.setBomDemandNumber(sum);
                    //BOM需求数量计算，相同图号进行累加；
                    int sumByDrawNo = collect1.get(record.getPartDrawingNo()).stream().mapToInt(e -> e.getDemandNumber()).sum();
                    record.setDemandNumber(sumByDrawNo * number);
                    //新增进来的图号数据；
                } else {
                    //不存在的BOM，零件数量为0
                    record.setBomDemandNumber(0);
                    //BOM需求数量计算
                    record.setDemandNumber(record.getDemandNumber() * number);
                }
            }
        }
        return baseProductReceiptExtends;
    }

    @Override
    public CommonResult insertReceipt(ReceiptDTO receiptDTO) {
        //入参校验
        this.checkData(receiptDTO);
        //处理主表数据信息
        BaseProductReceipt baseProductReceipt = new BaseProductReceipt();
        BeanUtils.copyBeanProp(baseProductReceipt, receiptDTO);
        baseProductReceipt.setId(UUID.randomUUID().toString().replace("-", ""));
        baseProductReceipt.setCheckDate(new Date());
        baseProductReceipt.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        baseProductReceipt.setCreateDate(new Date());
        baseProductReceipt.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        //初始化状态为待交接
        baseProductReceipt.setStatus(ReceiptStatusEnum.W.getCode());
        baseProductReceiptMapper.insert(baseProductReceipt);
        //查询该BOM下的所有零件，用于新增时做校验；图号需唯一
        /*List<ProjectBom> projectBomPartByIdList = projectBomService.getProjectBomPartByIdList(connectDTO.getBomId());
        List<String> drawNoList = projectBomPartByIdList.stream().map(e -> e.getDrawingNo()).collect(Collectors.toList());*/
        //处理子表数据信息
        List<BaseProductReceiptExtend> baseProductReceiptExtends = new ArrayList<>();
        for (ReceiptExtendDTO receiptExtendDTO : receiptDTO.getReceiptExtendDTOList()) {
            //图号唯一性校验
            /*if (drawNoList.contains(connectExtendDTO.getPartDrawingNo())) {
                return CommonResult.failed("图号" + connectExtendDTO.getPartDrawingNo() + "已经存在，不能重复添加");
            }*/
            BaseProductReceiptExtend baseProductReceiptExtend = new BaseProductReceiptExtend();
            BeanUtils.copyBeanProp(baseProductReceiptExtend, receiptExtendDTO);
            baseProductReceiptExtend.setConnectId(baseProductReceipt.getId());
            baseProductReceiptExtend.setBomId(receiptDTO.getBomId());
            baseProductReceiptExtends.add(baseProductReceiptExtend);
        }
        //批量入库
        baseProductReceiptExtendService.saveBatch(baseProductReceiptExtends);

        return CommonResult.success(true);
    }

    @Override
    public CommonResult editReceipt(ReceiptDTO receiptDTO) {
        if (StringUtils.isBlank(receiptDTO.getConnectNo())) {
            CommonResult.failed("交接单据号不能为空");
        }
        //前置判断
        LambdaQueryWrapper<BaseProductReceipt> baseProductReceiptLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductReceiptLambdaQueryWrapper.eq(BaseProductReceipt::getConnectNo, receiptDTO.getConnectNo());
        BaseProductReceipt baseProductReceipt = baseProductReceiptMapper.selectOne(baseProductReceiptLambdaQueryWrapper);
        if (ReceiptStatusEnum.Y.getCode().equals(baseProductReceipt.getStatus())) {
            CommonResult.failed("已接收不能编辑");
        }
        //清空子表数据；
        LambdaQueryWrapper<BaseProductReceiptExtend> baseProductReceiptExtendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductReceiptExtendLambdaQueryWrapper.eq(BaseProductReceiptExtend::getConnectId, baseProductReceipt.getId());
        baseProductReceiptExtendService.remove(baseProductReceiptExtendLambdaQueryWrapper);
        //主表信息变更；
        LambdaUpdateWrapper<BaseProductReceipt> baseProductConnectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        baseProductConnectLambdaUpdateWrapper.eq(BaseProductReceipt::getId, receiptDTO.getId());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getDriNo, receiptDTO.getDriNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getWorkNo, receiptDTO.getWorkNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getDrawNo, receiptDTO.getDrawNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getDrawNo, receiptDTO.getDrawNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getBomId, receiptDTO.getBomId());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getProductNo, receiptDTO.getProductNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getNumber, receiptDTO.getNumber());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getCheckUser, receiptDTO.getCheckUser());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getCheckDate, receiptDTO.getCheckDate());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getModifyBy, SecurityUtils.getCurrentUser().getUsername());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getBranchCode, receiptDTO.getBranchCode());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getReceiveUser, receiptDTO.getReceiveUser());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductReceipt::getReceiveUnit, receiptDTO.getReceiveUnit());
        //查询该BOM下的所有零件，用于新增时做校验；图号需唯一
        /*List<ProjectBom> projectBomPartByIdList = projectBomService.getProjectBomPartByIdList(connectDTO.getBomId());
        List<String> drawNoList = projectBomPartByIdList.stream().map(e -> e.getDrawingNo()).collect(Collectors.toList());*/
        //处理子表数据信息
        //新增子表数据；
        List<BaseProductReceiptExtend> baseProductReceiptExtends = new ArrayList<>();
        for (ReceiptExtendDTO receiptExtendDTO : receiptDTO.getReceiptExtendDTOList()) {
            //图号唯一性校验
            /*if (drawNoList.contains(connectExtendDTO.getPartDrawingNo())) {
                return CommonResult.failed("图号" + connectExtendDTO.getPartDrawingNo() + "已经存在，不能重复添加");
            }*/
            BaseProductReceiptExtend baseProductReceiptExtend = new BaseProductReceiptExtend();
            BeanUtils.copyBeanProp(baseProductReceiptExtend, receiptExtendDTO);
            baseProductReceiptExtend.setConnectId(baseProductReceipt.getId());
            baseProductReceiptExtends.add(baseProductReceiptExtend);
        }
        //批量入库
        baseProductReceiptExtendService.saveBatch(baseProductReceiptExtends);

        return CommonResult.success(true);
    }

    private void checkData(ReceiptDTO receiptDTO) {
        if (CollectionUtils.isEmpty(receiptDTO.getReceiptExtendDTOList()) || Objects.isNull(receiptDTO.getReceiptExtendDTOList())) {
            CommonResult.failed("零件信息不能为空");
        }
        if (StringUtils.isBlank(receiptDTO.getConnectNo())) {
            CommonResult.failed("单据号不能为空");
        }
        if (StringUtils.isBlank(receiptDTO.getDriNo())) {
            CommonResult.failed("配套钻机不能为空");
        }
        if (StringUtils.isBlank(receiptDTO.getWorkNo())) {
            CommonResult.failed("工作号不能为空");
        }
        if (StringUtils.isBlank(receiptDTO.getProductNo())) {
            CommonResult.failed("产品编号不能为空");
        }
        if (receiptDTO.getNumber() == null) {
            CommonResult.failed("数量不能为空");
        }
    }
}




