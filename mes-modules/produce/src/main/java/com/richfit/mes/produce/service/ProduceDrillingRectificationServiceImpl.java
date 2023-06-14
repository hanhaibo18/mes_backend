package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ProduceDrillingRectification;
import com.richfit.mes.common.model.produce.ProduceDrillingRectificationFile;
import com.richfit.mes.produce.dao.ProduceDrillingRectificationMapper;
import com.richfit.mes.produce.enmus.RectificationStatusEnum;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationDTO;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationFileDTO;
import com.richfit.mes.produce.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @author llh
 * @description 针对表【produce_drilling_rectification(钻机整改单据)】的数据库操作Service实现
 * @createDate 2023-06-14 14:41:59
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProduceDrillingRectificationServiceImpl extends ServiceImpl<ProduceDrillingRectificationMapper, ProduceDrillingRectification> implements ProduceDrillingRectificationService {

    @Autowired
    private ProduceDrillingRectificationMapper produceDrillingRectificationMapper;

    @Autowired
    private ProduceDrillingRectificationFileService produceDrillingRectificationFileService;

    @Override
    public Page<ProduceDrillingRectification> queryPageInfo(ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        Page<ProduceDrillingRectification> page = new Page<>(produceDrillingRectificationDTO.getPage(), produceDrillingRectificationDTO.getSize());
        LambdaQueryWrapper<ProduceDrillingRectification> baseProductReceiptLambdaQueryWrapper = new LambdaQueryWrapper<>();
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getStatus()), ProduceDrillingRectification::getStatus, produceDrillingRectificationDTO.getStatus());
        //时间区间查询
        baseProductReceiptLambdaQueryWrapper.ge(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getStartTime()), ProduceDrillingRectification::getCreateDate, produceDrillingRectificationDTO.getStartTime());
        baseProductReceiptLambdaQueryWrapper.le(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getEndTime()), ProduceDrillingRectification::getCreateDate, produceDrillingRectificationDTO.getEndTime());
        //查询条件
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getOrderNo()), ProduceDrillingRectification::getOrderNo, produceDrillingRectificationDTO.getOrderNo());
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getProjectName()), ProduceDrillingRectification::getProjectName, produceDrillingRectificationDTO.getProjectName());
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getWorkNo()), ProduceDrillingRectification::getWorkNo, produceDrillingRectificationDTO.getWorkNo());
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getPartName()), ProduceDrillingRectification::getPartName, produceDrillingRectificationDTO.getPartName());
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getSource()), ProduceDrillingRectification::getSource, produceDrillingRectificationDTO.getSource());
        baseProductReceiptLambdaQueryWrapper.eq(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getType()), ProduceDrillingRectification::getType, produceDrillingRectificationDTO.getType());
        Page<ProduceDrillingRectification> produceDrillingRectificationPage = produceDrillingRectificationMapper.selectPage(page, baseProductReceiptLambdaQueryWrapper);
        return produceDrillingRectificationPage;
    }

    @Override
    public CommonResult insertRectification(ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        //主表信息入库；
        ProduceDrillingRectification produceDrillingRectification = new ProduceDrillingRectification();
        BeanUtils.copyBeanProp(produceDrillingRectification, produceDrillingRectificationDTO);
        produceDrillingRectification.setId(UUID.randomUUID().toString().replace("-", ""));
        produceDrillingRectification.setCreateDate(new Date());
        produceDrillingRectificationMapper.insert(produceDrillingRectification);
        //附件信息入库；
        ArrayList<ProduceDrillingRectificationFile> produceDrillingRectificationFiles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(produceDrillingRectificationDTO.getProduceDrillingRectificationFileDTOList())) {
            for (ProduceDrillingRectificationFileDTO produceDrillingRectificationFileDTO : produceDrillingRectificationDTO.getProduceDrillingRectificationFileDTOList()) {
                ProduceDrillingRectificationFile produceDrillingRectificationFile = new ProduceDrillingRectificationFile();
                BeanUtils.copyBeanProp(produceDrillingRectificationFile, produceDrillingRectificationFileDTO);
                produceDrillingRectificationFile.setId(UUID.randomUUID().toString().replace("-", ""));
                produceDrillingRectificationFiles.add(produceDrillingRectificationFile);
            }
            produceDrillingRectificationFileService.saveBatch(produceDrillingRectificationFiles);
        }
        return CommonResult.success(true);
    }

    @Override
    public CommonResult returnBack(String id) {
        LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.W.getCode());
        produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, id);
        produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        return CommonResult.success(true);
    }
}




