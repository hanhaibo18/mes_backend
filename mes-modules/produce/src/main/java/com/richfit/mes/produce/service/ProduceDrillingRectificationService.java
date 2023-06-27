package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ProduceDrillingRectification;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationDTO;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationVO;

/**
 * @author llh
 * @description 针对表【produce_drilling_rectification(钻机整改单据)】的数据库操作Service
 * @createDate 2023-06-14 14:41:59
 */
public interface ProduceDrillingRectificationService extends IService<ProduceDrillingRectification> {


    /**
     * 整改单据列表
     *
     * @param produceDrillingRectificationDTO insertRectification
     * @return
     */
    Page<ProduceDrillingRectification> queryPageInfo(ProduceDrillingRectificationDTO produceDrillingRectificationDTO);

    /**
     * 新增整改单据
     *
     * @param produceDrillingRectificationDTO
     * @return
     */
    CommonResult insertRectification(ProduceDrillingRectificationDTO produceDrillingRectificationDTO);

    /**
     * 编辑整改单据
     *
     * @param produceDrillingRectificationDTO
     * @return
     */
    CommonResult editReceipt(ProduceDrillingRectificationDTO produceDrillingRectificationDTO);

    /**
     * 撤回整改单据
     *
     * @param id
     * @param menuType
     * @return
     */
    CommonResult returnBack(String id, String menuType);

    /**
     * 提交整改单据
     *
     * @param produceDrillingRectificationDTO
     * @return
     */
    CommonResult commit(ProduceDrillingRectificationDTO produceDrillingRectificationDTO);


    /**
     * 查询单据详情
     *
     * @param id
     * @return
     */
    ProduceDrillingRectificationVO queryDetail(String id);

    /**
     * 删除未提交的单据
     *
     * @param id
     * @return
     */
    CommonResult deleteInfo(String id);


}
