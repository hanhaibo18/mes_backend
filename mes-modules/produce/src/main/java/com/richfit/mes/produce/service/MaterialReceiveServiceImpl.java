package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.RequestNote;
import com.richfit.mes.common.model.produce.RequestNoteDetail;
import com.richfit.mes.common.model.produce.dto.MaterialReceiveDto;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.dao.MaterialReceiveMapper;
import com.richfit.mes.produce.provider.SystemServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Description TODO
 * @Author ang
 * @Date 2022/7/29 17:55
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialReceiveServiceImpl extends ServiceImpl<MaterialReceiveMapper, MaterialReceive> implements MaterialReceiveService {

    @Autowired
    MaterialReceiveMapper materialReceiveMapper;

    @Autowired
    RequestNoteService requestNoteService;

    @Resource
    SystemServiceClient systemServiceClient;

    @Autowired
    MaterialReceiveDetailService materialReceiveDetailService;


    @Resource
    private RequestNoteDetailService requestService;

    @Override
    public String getlastTime(String tenantId) {
        Date date = materialReceiveMapper.getlastTime(tenantId);
        if (null != date) {
            return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
        }
        return null;
    }

    @Override
    public Page<MaterialReceive> getPage(Page<MaterialReceive> materialReceivePage, QueryWrapper<MaterialReceive> queryWrapper) {
        return materialReceiveMapper.getPage(materialReceivePage, queryWrapper);
    }


    @Override
    public Boolean saveMaterialReceiveList(List<MaterialReceive> materialReceiveList) {
        String deliveryNo = materialReceiveList.get(0).getDeliveryNo();
        String aplyNum = materialReceiveList.get(0).getAplyNum();

        QueryWrapper<MaterialReceive> queryWrapper = new QueryWrapper();
        queryWrapper.eq("delivery_no", deliveryNo);
        List<MaterialReceive> list = this.list(queryWrapper);
        if (list.size() > 0) {
            materialReceiveList = null;
        } else {
            QueryWrapper<RequestNote> wrapper = new QueryWrapper();
            wrapper.eq("request_note_number", aplyNum);
            List<RequestNote> requestNotes = requestNoteService.list(wrapper);
            if (!requestNotes.isEmpty()) {
                for (MaterialReceive materialReceive : list) {
                    materialReceive.setBranchCode(requestNotes.get(0).getBranchCode());
                    materialReceive.setTenantId(requestNotes.get(0).getTenantId());
                }
            }
        }
        return this.saveBatch(materialReceiveList);
    }

    @Override
    public CommonResult materialReceiveSaveBatchList(MaterialReceiveDto material) {
        boolean flag = true;
        String message = "成功";
        try {
            if (CollectionUtils.isEmpty(material.getReceived())) {
                throw new GlobalException("物料主数据为空", ResultCode.FAILED);
            }
            if (CollectionUtils.isEmpty(material.getDetailList())) {
                throw new GlobalException("物料明细数据为空", ResultCode.FAILED);
            }
            Map<String, Tenant> collect = systemServiceClient.queryTenantList(SecurityConstants.FROM_INNER).getData().stream().collect(Collectors.toMap(Tenant::getTenantErpCode, x -> x, (value1, value2) -> value2));
            material.getReceived().forEach(materialReceive -> {
                if (collect.get(materialReceive.getErpCode()) == null) {
                    throw new GlobalException("ERPCODE没有找到租户信息", ResultCode.FAILED);
                }
                materialReceive.setTenantId(collect.get(materialReceive.getErpCode()).getId());
                materialReceive.setBranchCode(collect.get(materialReceive.getErpCode()).getTenantCode());
            });
            this.saveMaterialReceiveList(material.getReceived());
            //获取所有申请单号
            List<String> aplyNumList = material.getDetailList().stream().map(MaterialReceiveDetail::getAplyNum).collect(Collectors.toList());
            //获取物料号
            List<String> materialNumList = material.getDetailList().stream().map(MaterialReceiveDetail::getMaterialNum).collect(Collectors.toList());
            List<RequestNoteDetail> noteDetailList = requestService.queryDetailList(materialNumList, aplyNumList);
            //根据申请单号分组
            Map<String, List<RequestNoteDetail>> map = noteDetailList.stream().collect(Collectors.groupingBy(RequestNoteDetail::getRequestNoteNumber));
            material.getDetailList().forEach(detail -> {
                for (RequestNoteDetail requestNoteDetail : map.get(detail.getAplyNum())) {
                    if (StrUtil.isNotEmpty(requestNoteDetail.getDrawingNo())) {
                        detail.setDrawingNo(requestNoteDetail.getDrawingNo());
                    }
                }
            });
            materialReceiveDetailService.saveDetailList(material.getDetailList());
        } catch (GlobalException e) {
            //既能实现回滚也能返回结果
            flag = false;
            message = e.getMessage();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        } finally {
            if (flag) {
                return CommonResult.success(message);
            } else {
                return CommonResult.failed(message);
            }
        }
    }
}
