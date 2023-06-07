package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.BaseProductConnectExtendMapper;
import com.richfit.mes.base.dao.BaseProductConnectMapper;
import com.richfit.mes.base.enmus.ConnectStatusEnum;
import com.richfit.mes.base.entity.ConnectDTO;
import com.richfit.mes.base.entity.ConnectExtendDTO;
import com.richfit.mes.base.util.BeanUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.BaseProductConnect;
import com.richfit.mes.common.model.base.BaseProductConnectExtend;
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
 * @description 针对表【base_product_connect(产品交接单据)】的数据库操作Service实现
 * @createDate 2023-06-05 09:23:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseProductConnectServiceImpl extends ServiceImpl<BaseProductConnectMapper, BaseProductConnect> implements BaseProductConnectService {

    @Autowired
    private BaseProductConnectMapper baseProductConnectMapper;

    @Autowired
    private BaseProductConnectExtendMapper baseProductConnectExtendMapperMapper;

    @Autowired
    private BaseProductConnectExtendService baseProductConnectExtendService;

    @Autowired
    private ProjectBomService projectBomService;

    @Override
    public Page queryConnectInfo(ConnectDTO connectDTO) {
        Page<BaseProductConnect> page = new Page<>(connectDTO.getPage(), connectDTO.getLimit());
        LambdaQueryWrapper<BaseProductConnect> baseProductConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //时间降序
        baseProductConnectLambdaQueryWrapper.orderByDesc(BaseProductConnect::getCreateDate);
        baseProductConnectLambdaQueryWrapper.eq(StringUtils.isNotEmpty(connectDTO.getConnectNo()), BaseProductConnect::getConnectNo, connectDTO.getConnectNo());
        baseProductConnectLambdaQueryWrapper.eq(StringUtils.isNotEmpty(connectDTO.getDriNo()), BaseProductConnect::getDriNo, connectDTO.getDriNo());
        baseProductConnectLambdaQueryWrapper.eq(StringUtils.isNotEmpty(connectDTO.getWorkNo()), BaseProductConnect::getWorkNo, connectDTO.getWorkNo());
        //车间查询
        baseProductConnectLambdaQueryWrapper.eq(StringUtils.isNotEmpty(connectDTO.getBranchCode()), BaseProductConnect::getBranchCode, connectDTO.getBranchCode());
        //产品编号查询
        baseProductConnectLambdaQueryWrapper.like(StringUtils.isNotEmpty(connectDTO.getProductNo()), BaseProductConnect::getWorkNo, connectDTO.getWorkNo());
        //时间区间查询
        baseProductConnectLambdaQueryWrapper.ge(StringUtils.isNotEmpty(connectDTO.getStartTime()), BaseProductConnect::getCreateDate, connectDTO.getStartTime());
        baseProductConnectLambdaQueryWrapper.le(StringUtils.isNotEmpty(connectDTO.getEndTime()), BaseProductConnect::getCreateDate, connectDTO.getEndTime());
        //产品名称查询
        baseProductConnectLambdaQueryWrapper.eq(StringUtils.isNotEmpty(connectDTO.getProdDesc()), BaseProductConnect::getProdDesc, connectDTO.getProdDesc());

        return baseProductConnectMapper.selectPage(page, baseProductConnectLambdaQueryWrapper);
    }

    @Override
    public List queryConnectDetailInfo(String connectId, Integer number) {
        LambdaQueryWrapper<BaseProductConnectExtend> baseProductConnectExtendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductConnectExtendLambdaQueryWrapper.eq(BaseProductConnectExtend::getConnectId, connectId);
        List<BaseProductConnectExtend> baseProductConnectExtends = baseProductConnectExtendMapperMapper.selectList(baseProductConnectExtendLambdaQueryWrapper);
        for (BaseProductConnectExtend record : baseProductConnectExtends) {
            //BOM零件数量计算
            //根据零件图号分组，查询交接单下的零部件数量；
            List<ProjectBom> projectBomPartByIdList = projectBomService.getProjectBomPartByIdList(record.getBomId());
            Map<String, List<ProjectBom>> collect = projectBomPartByIdList.stream().collect(Collectors.groupingBy(item -> item.getDrawingNo()));
            if (collect.containsKey(record.getPartDrawingNo())) {
                int sum = collect.get(record.getPartDrawingNo()).stream().mapToInt(e -> e.getNumber()).sum();
                record.setBomDemandNumber(sum);
                record.setDemandNumber(sum * number);
            } else {
                record.setBomDemandNumber(0);
                record.setDemandNumber(record.getDemandNumber() * number);
            }
            //BOM需求数量计算

        }
        return baseProductConnectExtends;
    }

    @Override
    public CommonResult insertConnect(ConnectDTO connectDTO) {
        //入参校验
        this.checkData(connectDTO);
        //处理主表数据信息
        BaseProductConnect baseProductConnect = new BaseProductConnect();
        BeanUtils.copyBeanProp(baseProductConnect, connectDTO);
        baseProductConnect.setId(UUID.randomUUID().toString().replace("-", ""));
        baseProductConnect.setCheckDate(new Date());
        baseProductConnect.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        baseProductConnect.setCreateDate(new Date());
        //初始化状态为待交接
        baseProductConnect.setStatus(ConnectStatusEnum.W.getCode());
        baseProductConnectMapper.insert(baseProductConnect);
        //查询该BOM下的所有零件，用于新增时做校验；图号需唯一
        /*List<ProjectBom> projectBomPartByIdList = projectBomService.getProjectBomPartByIdList(connectDTO.getBomId());
        List<String> drawNoList = projectBomPartByIdList.stream().map(e -> e.getDrawingNo()).collect(Collectors.toList());*/
        //处理子表数据信息
        List<BaseProductConnectExtend> baseProductConnectExtends = new ArrayList<>();
        for (ConnectExtendDTO connectExtendDTO : connectDTO.getConnectExtendDTOList()) {
            //图号唯一性校验
            /*if (drawNoList.contains(connectExtendDTO.getPartDrawingNo())) {
                return CommonResult.failed("图号" + connectExtendDTO.getPartDrawingNo() + "已经存在，不能重复添加");
            }*/
            BaseProductConnectExtend baseProductConnectExtend = new BaseProductConnectExtend();
            BeanUtils.copyBeanProp(baseProductConnectExtend, connectExtendDTO);
            baseProductConnectExtend.setConnectId(baseProductConnect.getId());
            baseProductConnectExtend.setBomId(connectDTO.getBomId());
            baseProductConnectExtends.add(baseProductConnectExtend);
        }
        //批量入库
        baseProductConnectExtendService.saveBatch(baseProductConnectExtends);

        return CommonResult.success(true);
    }

    @Override
    public CommonResult editConnect(ConnectDTO connectDTO) {
        if (StringUtils.isBlank(connectDTO.getConnectNo())) {
            CommonResult.failed("交接单据号不能为空");
        }
        //前置判断
        LambdaQueryWrapper<BaseProductConnect> baseProductConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductConnectLambdaQueryWrapper.eq(BaseProductConnect::getConnectNo, connectDTO.getConnectNo());
        BaseProductConnect baseProductConnect = baseProductConnectMapper.selectOne(baseProductConnectLambdaQueryWrapper);
        if (ConnectStatusEnum.Y.getCode().equals(baseProductConnect.getStatus())) {
            CommonResult.failed("已接收不能编辑");
        }
        //清空子表数据；
        LambdaQueryWrapper<BaseProductConnectExtend> baseProductConnectExtendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductConnectExtendLambdaQueryWrapper.eq(BaseProductConnectExtend::getConnectId, baseProductConnect.getId());
        baseProductConnectExtendService.remove(baseProductConnectExtendLambdaQueryWrapper);
        //主表信息变更；
        LambdaUpdateWrapper<BaseProductConnect> baseProductConnectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        baseProductConnectLambdaUpdateWrapper.eq(BaseProductConnect::getId, connectDTO.getId());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getDriNo, connectDTO.getDriNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getWorkNo, connectDTO.getWorkNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getDrawNo, connectDTO.getDrawNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getDrawNo, connectDTO.getDrawNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getBomId, connectDTO.getBomId());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getProductNo, connectDTO.getProductNo());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getNumber, connectDTO.getNumber());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getCheckUser, connectDTO.getCheckUser());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getCheckDate, connectDTO.getCheckDate());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getModifyBy, SecurityUtils.getCurrentUser().getUsername());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getBranchCode, connectDTO.getBranchCode());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getReceiveUser, connectDTO.getReceiveUser());
        baseProductConnectLambdaUpdateWrapper.set(BaseProductConnect::getReceiveUnit, connectDTO.getReceiveUnit());
        //查询该BOM下的所有零件，用于新增时做校验；图号需唯一
        /*List<ProjectBom> projectBomPartByIdList = projectBomService.getProjectBomPartByIdList(connectDTO.getBomId());
        List<String> drawNoList = projectBomPartByIdList.stream().map(e -> e.getDrawingNo()).collect(Collectors.toList());*/
        //处理子表数据信息
        //新增子表数据；
        List<BaseProductConnectExtend> baseProductConnectExtends = new ArrayList<>();
        for (ConnectExtendDTO connectExtendDTO : connectDTO.getConnectExtendDTOList()) {
            //图号唯一性校验
            /*if (drawNoList.contains(connectExtendDTO.getPartDrawingNo())) {
                return CommonResult.failed("图号" + connectExtendDTO.getPartDrawingNo() + "已经存在，不能重复添加");
            }*/
            BaseProductConnectExtend baseProductConnectExtend = new BaseProductConnectExtend();
            BeanUtils.copyBeanProp(baseProductConnectExtend, connectExtendDTO);
            baseProductConnectExtend.setConnectId(baseProductConnect.getId());
            baseProductConnectExtends.add(baseProductConnectExtend);
        }
        //批量入库
        baseProductConnectExtendService.saveBatch(baseProductConnectExtends);

        return CommonResult.success(true);
    }

    private void checkData(ConnectDTO connectDTO) {
        if (CollectionUtils.isEmpty(connectDTO.getConnectExtendDTOList())) {
            CommonResult.failed("零件信息不能为空");
        }
        if (StringUtils.isBlank(connectDTO.getConnectNo())) {
            CommonResult.failed("单据号不能为空");
        }
        if (StringUtils.isBlank(connectDTO.getDriNo())) {
            CommonResult.failed("配套钻机不能为空");
        }
        if (StringUtils.isBlank(connectDTO.getWorkNo())) {
            CommonResult.failed("工作好不能为空");
        }
        if (StringUtils.isBlank(connectDTO.getProductNo())) {
            CommonResult.failed("产品编号不能为空");
        }
        if (connectDTO.getNumber() == null) {
            CommonResult.failed("数量不能为空");
        }
    }
}




