package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.base.BaseProductReceipt;
import com.richfit.mes.common.model.produce.SkillNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author wangchenyu
 * @description 针对表【base_product_connect(产品交接单据)】的数据库操作Mapper
 * @createDate 2023-06-05 09:23:01
 * @Entity generator.domain.BaseProductConnect
 */
@Mapper
public interface BaseProductReceiptMapper extends BaseMapper<BaseProductReceipt> {

    /**
     * @param page
     * @param wrapper
     * @Author: xinYu.hou
     * @Date: 2023/5/31 10:20
     * @return: IPage<Notice>
     **/
    @Select("SELECT * from  base_product_receipt ${ew.customSqlSegment} ")
    IPage<BaseProductReceipt> queryPage(@Param("page") Page<BaseProductReceipt> page, @Param(Constants.WRAPPER) Wrapper<BaseProductReceipt> wrapper);

}




