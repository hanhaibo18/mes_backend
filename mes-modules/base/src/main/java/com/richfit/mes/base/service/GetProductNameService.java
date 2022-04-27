package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gwb
 */
public interface GetProductNameService extends IService<Product> {

    List<Product> queryProductName(@Param(Constants.WRAPPER) QueryWrapper<List> wrapper);
}
