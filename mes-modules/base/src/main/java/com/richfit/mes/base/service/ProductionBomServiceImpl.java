package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.base.dao.ProductionBomMapper;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sun
 * @Description 产品BOM服务
 */
@Service
public class ProductionBomServiceImpl extends ServiceImpl<ProductionBomMapper, ProductionBom> implements ProductionBomService{

    @Autowired
    private ProductionBomMapper productionBomMapper;

    @Override
    public IPage<ProductionBom> getProductionBomByPage(Page<ProductionBom> page, QueryWrapper<ProductionBom> query){
        return productionBomMapper.getProductionBomByPage(page, query);
    }

    @Override
    public IPage<ProductionBom> getProductionBomHistory(Page<ProductionBom> page, QueryWrapper<ProductionBom> query) {
        return productionBomMapper.getProductionBomHistory(page, query);
    }

    @Override
    public boolean saveByList(List<ProductionBom> list){

        List<ProductionBom> addList = new ArrayList<>();

        for (ProductionBom bom : list) {
            for (ProductionBom bom2 : list) {
                if(bom.getMainDrawingNo() != null && bom.getMainDrawingNo() != ""){
                    if(bom2.getDrawingNo().equals(bom.getMainDrawingNo())){
                        bom.setMainDrawingNo(bom2.getId());
                        break;
                    }
                }
            }
            int count = productionBomMapper.insert(bom);
            if(count > 0){
                addList.add(bom);
            }
        }

        if(addList.size() == list.size()){
            return true;
        }
        return false;
    }

    @Override
    public List<ProductionBom> getProductionBomList(@Param(Constants.WRAPPER) Wrapper<ProductionBom> query){
        return productionBomMapper.getProductionBomList(query);
    }

    @Override
    @Transactional
    public boolean updateStatus(ProductionBom bom) {
        UpdateWrapper<ProductionBom> update = new UpdateWrapper<ProductionBom>();
        update.apply("(drawing_no = {0} or main_drawing_no = {0} ) and bom_key = {1}", bom.getDrawingNo(), bom.getBomKey());
        update.set("status", bom.getStatus());
        // 是否为发布产品BOM
        if(bom.getStatus().equals("1")){
            update.set("publish_by", SecurityUtils.getCurrentUser().getUsername());
            update.set("publish_time", new Date());

            // 发布新BOM要将之前的BOM停用
            String drawingNo = bom.getDrawingNo();
            UpdateWrapper<ProductionBom> stopWrapper = new UpdateWrapper<>();
            stopWrapper.apply("(drawing_no = {0} or main_drawing_no = {0})", drawingNo);
            stopWrapper.set("status", "2");
            productionBomMapper.update(null, stopWrapper);
        }
        return SqlHelper.retBool(productionBomMapper.update(null, update));
    }


}
