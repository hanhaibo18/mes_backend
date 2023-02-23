package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.ProductionProcessMapper;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.ProductionProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HanHaiBo
 * @date 2023/2/20 15:36
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ProductionProcessServiceImpl extends ServiceImpl<ProductionProcessMapper, ProductionProcess> implements ProductionProcessService {
    @Autowired
    private ProductionProcessMapper productionProcessMapper;
    @Autowired
    private ProductionProcessService productionProcessService;

    @Override
    public boolean updateBatch(ProductionProcess[] productionProcesses) {

        String productionRouteId = productionProcesses[0].getProductionRouteId();
        List<String> currentIdList = new ArrayList<>();
        for (ProductionProcess process : productionProcesses) {
            if (StringUtils.isNullOrEmpty(process.getProcessName())) {
                return false;
            }
            if (process.getId() != null) {
                currentIdList.add(process.getId());
            }
        }
        //获取当前所有idList
        QueryWrapper<ProductionProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("production_route_id", productionRouteId);
        List<ProductionProcess> allProcess = productionProcessMapper.selectList(queryWrapper);
        if (!allProcess.isEmpty()) {
            List<String> allIdList = new ArrayList<>();
            for (ProductionProcess process : allProcess) {
                allIdList.add(process.getId());
            }
            //当前所有idList中剔除传入的即为删除的idList
            allIdList.removeAll(currentIdList);
            if (!allIdList.isEmpty()) {
                if (productionProcessMapper.deleteBatchIds(allIdList) == 0) {
                    throw new GlobalException("删除时发生异常", ResultCode.FAILED);
                }
            }

        }
        //获取id为null的新增list
        List<ProductionProcess> addList = Arrays.stream(productionProcesses).filter(process -> process.getId() == null).collect(Collectors.toList());
        if (!addList.isEmpty()) {
            for (ProductionProcess process : addList) {
                if (productionProcessMapper.insert(process) == 0) {
                    throw new GlobalException("新增时发生异常", ResultCode.FAILED);
                }
            }
        }
        //获取修改list
        List<ProductionProcess> updateList = Arrays.stream(productionProcesses).filter(process -> process.getId() != null).collect(Collectors.toList());
        if (!updateList.isEmpty()) {
            for (ProductionProcess process : updateList) {
                if (productionProcessMapper.updateById(process) == 0) {
                    throw new GlobalException("修改时发生异常", ResultCode.FAILED);
                }
            }
        }
        return true;
    }
}
