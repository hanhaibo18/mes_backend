package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.richfit.mes.common.security.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        baseProductConnectLambdaQueryWrapper.like(StringUtils.isNotEmpty(connectDTO.getProductNo()), BaseProductConnect::getWorkNo, connectDTO.getWorkNo());
        //时间区间查询
        baseProductConnectLambdaQueryWrapper.ge(StringUtils.isNotEmpty(connectDTO.getStartTime()), BaseProductConnect::getCreateDate, connectDTO.getStartTime());
        baseProductConnectLambdaQueryWrapper.le(StringUtils.isNotEmpty(connectDTO.getEndTime()), BaseProductConnect::getCreateDate, connectDTO.getEndTime());
        return baseProductConnectMapper.selectPage(page, baseProductConnectLambdaQueryWrapper);
    }

    @Override
    public Page queryConnectDetailInfo(String connectId, int currentPage, int limit) {
        Page<BaseProductConnectExtend> page = new Page<>(currentPage, limit);
        LambdaQueryWrapper<BaseProductConnectExtend> baseProductConnectExtendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductConnectExtendLambdaQueryWrapper.eq(BaseProductConnectExtend::getConnectId, connectId);
        Page<BaseProductConnectExtend> baseProductConnectExtendPage = baseProductConnectExtendMapperMapper.selectPage(page, baseProductConnectExtendLambdaQueryWrapper);
        //todo 数量信息计算
        return baseProductConnectExtendPage;
    }

    @Override
    public void insertConnect(ConnectDTO connectDTO) {
        //入参校验
        this.checkData(connectDTO);
        //处理主表数据信息
        BaseProductConnect baseProductConnect = new BaseProductConnect();
        BeanUtils.copyBeanProp(baseProductConnect, connectDTO);
        baseProductConnect.setId(UUID.randomUUID().toString().replace("-", ""));
        baseProductConnect.setCheckDate(new Date());
        baseProductConnect.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        baseProductConnect.setCreateDate(new Date());
        baseProductConnectMapper.insert(baseProductConnect);
        //处理子表数据信息
        List<BaseProductConnectExtend> baseProductConnectExtends = new ArrayList<>();
        for (ConnectExtendDTO connectExtendDTO : connectDTO.getConnectExtendDTOList()) {
            BaseProductConnectExtend baseProductConnectExtend = new BaseProductConnectExtend();
            BeanUtils.copyBeanProp(baseProductConnectExtend, connectExtendDTO);
            baseProductConnectExtend.setConnectId(baseProductConnect.getId());
            baseProductConnectExtends.add(baseProductConnectExtend);
        }
        //批量入库
        baseProductConnectExtendService.saveBatch(baseProductConnectExtends);
    }

    @Override
    public void editConnect(ConnectDTO connectDTO) {
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
        //新增子表数据；
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




