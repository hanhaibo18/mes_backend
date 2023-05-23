package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ApplicationNumber;

/**
 * @ClassName: ApplicationNumberService.java
 * @Author: Hou XinYu
 * @Description: 申请单号相关
 * @CreateTime: 2022年11月17日 10:23:00
 */
public interface ApplicationNumberService extends IService<ApplicationNumber> {

    /**
     * 功能描述: 获取申请单号
     *
     * @param itemId
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/11/18 10:38
     * @return: int
     **/
    Long acquireApplicationNumber(String itemId, String branchCode);

    /**
     * 功能描述: 根据工序Id查询申请单号
     *
     * @param item
     * @Author: xinYu.hou
     * @Date: 2022/11/18 10:59
     * @return: int
     **/
    Long queryApplicationNumber(String item);

    /**
     * 功能描述:根据工序删除申请单号
     *
     * @param itemId
     * @Author: xinYu.hou
     * @Date: 2022/11/18 11:26
     * @return: boolean
     **/
    boolean deleteApplicationNumberByItemId(String itemId);
}
