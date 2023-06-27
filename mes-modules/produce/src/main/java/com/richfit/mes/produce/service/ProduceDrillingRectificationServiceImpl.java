package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ProduceDrillingRectification;
import com.richfit.mes.common.model.produce.ProduceDrillingRectificationFile;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ProduceDrillingRectificationFileMapper;
import com.richfit.mes.produce.dao.ProduceDrillingRectificationMapper;
import com.richfit.mes.produce.enmus.CommitStatusEnum;
import com.richfit.mes.produce.enmus.RectificationStatusEnum;
import com.richfit.mes.produce.enmus.RectificationUnitEnum;
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
        baseProductReceiptLambdaQueryWrapper.like(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getSource()), ProduceDrillingRectification::getSource, produceDrillingRectificationDTO.getSource());
        baseProductReceiptLambdaQueryWrapper.like(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getType()), ProduceDrillingRectification::getType, produceDrillingRectificationDTO.getType());
        baseProductReceiptLambdaQueryWrapper.like(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getDutyUnit()), ProduceDrillingRectification::getDutyUnit, produceDrillingRectificationDTO.getDutyUnit());
        baseProductReceiptLambdaQueryWrapper.like(StringUtils.isNotEmpty(produceDrillingRectificationDTO.getRectificationUnit()), ProduceDrillingRectification::getRectificationUnit, produceDrillingRectificationDTO.getRectificationUnit());
        Page<ProduceDrillingRectification> produceDrillingRectificationPage = produceDrillingRectificationMapper.selectPage(page, baseProductReceiptLambdaQueryWrapper);
        for (ProduceDrillingRectification record : produceDrillingRectificationPage.getRecords()) {
            record.setDutyUnitList(StringUtils.isNotBlank(record.getDutyUnit()) ? Arrays.asList(record.getDutyUnit().split(",")) : new ArrayList<>());
            record.setRectificationUnitList(StringUtils.isNotBlank(record.getRectificationUnit()) ? Arrays.asList(record.getRectificationUnit().split(",")) : new ArrayList<>());
            record.setSourceList(StringUtils.isNotBlank(record.getSource()) ? Arrays.asList(record.getSource().split(",")) : new ArrayList<>());
            record.setTypeList(StringUtils.isNotBlank(record.getType()) ? Arrays.asList(record.getType().split(",")) : new ArrayList<>());
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
        produceDrillingRectification.setStatus(RectificationStatusEnum.MANAGE_NOT.getCode());
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
        produceDrillingRectificationMapper.update(produceDrillingRectification, produceDrillingRectificationLambdaUpdateWrapper);
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
    public CommonResult returnBack(String id, String menuType) {
        LambdaQueryWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationLambdaQueryWrapper.eq(ProduceDrillingRectification::getId, id);
        ProduceDrillingRectification produceDrillingRectification = produceDrillingRectificationMapper.selectOne(produceDrillingRectificationLambdaQueryWrapper);
        //办理单位撤回逻辑；
        if (RectificationUnitEnum.E.getCode().equals(menuType)) {
            if (produceDrillingRectification.getStatus().equals(RectificationStatusEnum.N.getCode())) {
                return CommonResult.failed("已关单不能撤回");
            }
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //修改状态为“未提交”
            //数据清除
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.MANAGE_NOT.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsSendCommit, CommitStatusEnum.NOT.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getDutyUnit, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRectificationUnit, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getMeasure, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getOptName, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getCheckUser, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getResult, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRebackUser, null);
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, id);
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
        //办理单位撤回逻辑；
        if (RectificationUnitEnum.A.getCode().equals(menuType)) {
            if (produceDrillingRectification.getStatus().equals(RectificationStatusEnum.RECTIFICATION_UNIT_DONE.getCode())) {
                return CommonResult.failed("整改单位已经处理，不能提交");
            }
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //修改状态为“未提交”
            //数据清除
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.MANAGE_HAVE.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsUnitCommit, CommitStatusEnum.NOT.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getDutyUnit, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRectificationUnit, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getMeasure, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getOptName, null);
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, id);
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
        //整改单位撤回；
        if (RectificationUnitEnum.C.getCode().equals(menuType)) {
            if (produceDrillingRectification.getStatus().equals(RectificationStatusEnum.HAVE_CHECK.getCode())) {
                return CommonResult.failed("已整改检验，无法撤回");
            }
            //修改状态
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.Y.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsUpdateCommit, CommitStatusEnum.NOT.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRebackUser, null);
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, id);
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
        //整改检验撤回
        if (RectificationUnitEnum.D.getCode().equals(menuType)) {
            if (produceDrillingRectification.getStatus().equals(RectificationStatusEnum.N.getCode())) {
                return CommonResult.failed("提单人已关单，无法撤回");
            }
            //修改状态
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.RECTIFICATION_UNIT_DONE.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsCheckCommit, CommitStatusEnum.NOT.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getCheckUser, null);
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getResult, null);
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, id);
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
        return CommonResult.success(true);
    }

    @Override
    public CommonResult commit(ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        //开具人提交
        if (RectificationUnitEnum.E.getCode().equals(produceDrillingRectificationDTO.getMenuType())) {
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //修改状态为“开具人已提报”
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.MANAGE_HAVE.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getManageUnit, produceDrillingRectificationDTO.getManageUnit());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsSendCommit, CommitStatusEnum.YES.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, produceDrillingRectificationDTO.getId());
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
        //办理单位提交
        if (RectificationUnitEnum.A.getCode().equals(produceDrillingRectificationDTO.getMenuType())) {
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //修改状态为“已提交”
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.Y.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsUnitCommit, CommitStatusEnum.YES.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getDutyUnit, produceDrillingRectificationDTO.getDutyUnit());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRectificationUnit, produceDrillingRectificationDTO.getRectificationUnit());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getMeasure, produceDrillingRectificationDTO.getMeasure());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getOptId, produceDrillingRectificationDTO.getOptId());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getOptName, produceDrillingRectificationDTO.getOptName());
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, produceDrillingRectificationDTO.getId());
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
        //整改单位提交
        if (RectificationUnitEnum.C.getCode().equals(produceDrillingRectificationDTO.getMenuType())) {
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //修改状态为“已提交”
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRebackUser, produceDrillingRectificationDTO.getRebackUser());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.RECTIFICATION_UNIT_DONE.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsUpdateCommit, CommitStatusEnum.YES.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, produceDrillingRectificationDTO.getId());
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
        //整改质检提交
        if (RectificationUnitEnum.D.getCode().equals(produceDrillingRectificationDTO.getMenuType())) {
            LambdaUpdateWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //修改状态为“已提交”
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getRebackUser, produceDrillingRectificationDTO.getRebackUser());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getStatus, RectificationStatusEnum.HAVE_CHECK.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getIsCheckCommit, CommitStatusEnum.YES.getCode());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getCheckUser, produceDrillingRectificationDTO.getCheckUser());
            produceDrillingRectificationLambdaUpdateWrapper.set(ProduceDrillingRectification::getResult, produceDrillingRectificationDTO.getResult());
            produceDrillingRectificationLambdaUpdateWrapper.eq(ProduceDrillingRectification::getId, produceDrillingRectificationDTO.getId());
            produceDrillingRectificationMapper.update(null, produceDrillingRectificationLambdaUpdateWrapper);
        }
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
        produceDrillingRectificationVO.setDutyUnit(StringUtils.isNotBlank(produceDrillingRectification.getDutyUnit()) ? Arrays.asList(produceDrillingRectification.getDutyUnit().split(",")) : new ArrayList<>());
        produceDrillingRectificationVO.setRectificationUnit(StringUtils.isNotBlank(produceDrillingRectification.getRectificationUnit()) ? Arrays.asList(produceDrillingRectification.getRectificationUnit().split(",")) : new ArrayList<>());
        produceDrillingRectificationVO.setSource(StringUtils.isNotBlank(produceDrillingRectification.getSource()) ? Arrays.asList(produceDrillingRectification.getSource().split(",")) : new ArrayList<>());
        produceDrillingRectificationVO.setType(StringUtils.isNotBlank(produceDrillingRectification.getType()) ? Arrays.asList(produceDrillingRectification.getType().split(",")) : new ArrayList<>());
        produceDrillingRectificationVO.setOptName(StringUtils.isNotBlank(produceDrillingRectification.getOptName()) ? Arrays.asList(produceDrillingRectification.getOptName().split(",")) : new ArrayList<>());
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
    public CommonResult deleteInfo(String id) {
        LambdaQueryWrapper<ProduceDrillingRectification> produceDrillingRectificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        produceDrillingRectificationLambdaQueryWrapper.eq(ProduceDrillingRectification::getId, id);
        ProduceDrillingRectification produceDrillingRectification = produceDrillingRectificationMapper.selectOne(produceDrillingRectificationLambdaQueryWrapper);
        if (!produceDrillingRectification.getStatus().equals(RectificationStatusEnum.MANAGE_NOT.getCode())) {
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




