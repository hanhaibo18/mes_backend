package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.base.entity.MaterialSyncDto;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;

import java.util.List;

/**
 * @ClassName: materialSyncService.java
 * @Author: Hou XinYu
 * @Description:
 * @CreateTime: 2022年02月10日 09:06:00
 */
public interface MaterialSyncService extends IService<Product> {

    /**
     * 功能描述: 从第三方获取列表数据
     *
     * @param materialSyncDto
     * @Author: xinYu.hou
     * @Date: 2022/2/10 16:14
     * @return: List<Product>
     **/
    List<Product> queryProductSync(MaterialSyncDto materialSyncDto);

    /**
     * 功能描述: 同步物料
     *
     * @param productList
     * @Author: xinYu.hou
     * @Date: 2022/2/10 15:48
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveProductSync(List<Product> productList);

    /**
     * 功能描述: 定时同步物料
     *
     * @Author: xinYu.hou
     * @Date: 2022/2/10 15:50
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveTimingProductSync();

}
