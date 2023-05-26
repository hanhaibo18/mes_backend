package com.richfit.mes.produce.service.erp;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.*;

/**
 * 功能描述:工时管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
public interface WorkHoursService extends IService<Certificate> {
    /**
     * 功能描述:工时推送
     *
     * @param certificate 合格证信息
     * @throws Exception 合格证号码:异常信息
     * @Author: zhiqiang.lu
     * @Date: 2023/05/26 16:27
     **/
    void push(Certificate certificate) throws Exception;
}
