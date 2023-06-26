package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ProduceDrillingRectification;
import com.richfit.mes.common.model.produce.ProduceDrillingRectificationFile;
import com.richfit.mes.produce.dao.ProduceDrillingRectificationFileMapper;
import com.richfit.mes.produce.dao.ProduceDrillingRectificationMapper;
import com.richfit.mes.produce.enmus.RectificationStatusEnum;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationDTO;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationFileDTO;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationFileVO;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationVO;
import com.richfit.mes.produce.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private ProduceDrillingRectificationFileMapper produceDrillingRectificationfileMapper;

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
        baseProductReceiptLambdaQueryWrapper.like(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getDutyUnit()), ProduceDrillingRectification::getDutyUnit, produceDrillingRectificationDTO.getDutyUnit());
        baseProductReceiptLambdaQueryWrapper.like(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getRectificationUnit()), ProduceDrillingRectification::getRectificationUnit, produceDrillingRectificationDTO.getRectificationUnit());
        Page<ProduceDrillingRectification> produceDrillingRectificationPage = produceDrillingRectificationMapper.selectPage(page, baseProductReceiptLambdaQueryWrapper);
        for (ProduceDrillingRectification record : produceDrillingRectificationPage.getRecords()) {
            record.setDutyUnitList(Arrays.asList(record.getDutyUnit()));
            record.setRectificationUnitList(Arrays.asList(record.getRectificationUnit()));
        }
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
                produceDrillingRectificationFile.setOrderNo(produceDrillingRectification.getId());
                produceDrillingRectificationFiles.add(produceDrillingRectificationFile);
            }
            produceDrillingRectificationFileService.saveBatch(produceDrillingRectificationFiles);
        }
        return CommonResult.success(true);
    }

    @Override
    public CommonResult editReceipt(ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        //主表信息更改
        LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ProduceDrillingRectification produceDrillingRectification = new ProduceDrillingRectification();
        BeanUtils.copyBeanProp(produceDrillingRectification, produceDrillingRectificationDTO);
        produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, produceDrillingRectificationDTO.getId());
        produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        //附件表数据删除
        LambdaQueryWrapper<ProduceDrillingRectificationFile> produceDrillingRectificationFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationFileLambdaQueryWrapper.eq(ProduceDrillingRectificationFile::getOrderNo, produceDrillingRectificationDTO.getId());
        produceDrillingRectificationfileMapper.delete(produceDrillingRectificationFileLambdaQueryWrapper);
        //更新数据
        //附件信息入库；
        ArrayList<ProduceDrillingRectificationFile> produceDrillingRectificationFiles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(produceDrillingRectificationDTO.getProduceDrillingRectificationFileDTOList())) {
            for (ProduceDrillingRectificationFileDTO produceDrillingRectificationFileDTO : produceDrillingRectificationDTO.getProduceDrillingRectificationFileDTOList()) {
                ProduceDrillingRectificationFile produceDrillingRectificationFile = new ProduceDrillingRectificationFile();
                BeanUtils.copyBeanProp(produceDrillingRectificationFile, produceDrillingRectificationFileDTO);
                produceDrillingRectificationFile.setOrderNo(produceDrillingRectification.getId());
                produceDrillingRectificationFiles.add(produceDrillingRectificationFile);
            }
            produceDrillingRectificationFileService.saveBatch(produceDrillingRectificationFiles);
        }
        return CommonResult.success(true);
    }

    @Override
    public CommonResult returnBack(String id) {
        LambdaQueryWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationLambdaQueryWrapper.eq(ProduceDrillingRectification::getId, id);
        ProduceDrillingRectification produceDrillingRectification = produceDrillingRectificationMapper.selectOne(produceDrillingRectificationLambdaQueryWrapper);
        if (produceDrillingRectification.getStatus().equals(RectificationStatusEnum.N.getCode())) {
            return CommonResult.failed("已关闭的单据不能撤回");
        }
        LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //修改状态为“未提交”
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.W.getCode());
        produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, id);
        produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        return CommonResult.success(true);
    }

    @Override
    public CommonResult commit(ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //修改状态为“已提交”
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.Y.getCode());
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getDutyUnit, produceDrillingRectificationDTO.getDutyUnit());
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRectificationUnit, produceDrillingRectificationDTO.getRectificationUnit());
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getMeasure, produceDrillingRectificationDTO.getMeasure());
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getOptId, produceDrillingRectificationDTO.getOptId());
        produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getOptName, produceDrillingRectificationDTO.getOptName());
        produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, produceDrillingRectificationDTO.getId());
        produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        return CommonResult.success(true);
    }

    @Override
    public ProduceDrillingRectificationVO queryDetail(String id) {
        //结果对象
        ProduceDrillingRectificationVO produceDrillingRectificationVO = new ProduceDrillingRectificationVO();
        ArrayList<ProduceDrillingRectificationFileVO> produceDrillingRectificationFileVOS = new ArrayList<>();
        LambdaQueryWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationLambdaQueryWrapper.eq(ProduceDrillingRectification::getId, id);
        //主表信息
        ProduceDrillingRectification produceDrillingRectification = produceDrillingRectificationMapper.selectOne(produceDrillingRectificationLambdaQueryWrapper);
        BeanUtils.copyBeanProp(produceDrillingRectificationVO, produceDrillingRectification);
        produceDrillingRectificationVO.setDutyUnit(Arrays.asList(produceDrillingRectification.getDutyUnit()));
        produceDrillingRectificationVO.setRectificationUnit(Arrays.asList(produceDrillingRectification.getRectificationUnit()));
        //附件信息
        LambdaQueryWrapper<ProduceDrillingRectificationFile> produceDrillingRectificationFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationFileLambdaQueryWrapper.eq(ProduceDrillingRectificationFile::getOrderNo, id);
        List<ProduceDrillingRectificationFile> produceDrillingRectificationFiles = produceDrillingRectificationfileMapper.selectList(produceDrillingRectificationFileLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(produceDrillingRectificationFiles)) {
            for (ProduceDrillingRectificationFile produceDrillingRectificationFile : produceDrillingRectificationFiles) {
                ProduceDrillingRectificationFileVO produceDrillingRectificationFileVO = new ProduceDrillingRectificationFileVO();
                BeanUtils.copyBeanProp(produceDrillingRectificationFileVO, produceDrillingRectificationFile);
                produceDrillingRectificationFileVOS.add(produceDrillingRectificationFileVO);
            }
        }
        produceDrillingRectificationVO.setProduceDrillingRectificationFileDTOList(produceDrillingRectificationFileVOS);
        return produceDrillingRectificationVO;
    }

    @Override
    public CommonResult delete(String id) {
        LambdaQueryWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationLambdaQueryWrapper.eq(ProduceDrillingRectification::getId, id);
        ProduceDrillingRectification produceDrillingRectification = produceDrillingRectificationMapper.selectOne(produceDrillingRectificationLambdaQueryWrapper);
        if (!produceDrillingRectification.getStatus().equals(RectificationStatusEnum.W.getCode())) {
            return CommonResult.failed("只能删除未提交的单据");
        }
        //删除主表数据
        produceDrillingRectificationMapper.delete(produceDrillingRectificationLambdaQueryWrapper);
        //删除附件表数据
        LambdaQueryWrapper<ProduceDrillingRectificationFile> produceDrillingRectificationFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationFileLambdaQueryWrapper.eq(ProduceDrillingRectificationFile::getOrderNo, id);
        produceDrillingRectificationfileMapper.delete(produceDrillingRectificationFileLambdaQueryWrapper);
        return CommonResult.success(true);
    }
}




