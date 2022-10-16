package com.richfit.mes.produce.utils;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.produce.provider.SystemServiceClient;

import javax.annotation.Resource;

/**
 * @ClassName: itemUtil.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年10月14日 14:58:00
 */
public class ItemUtil {

    @Resource
    private SystemServiceClient systemServiceClient;

    private String value(String code) {
        try {
            CommonResult<ItemParam> result = systemServiceClient.findItemParamByCode(code);
            ItemParam itemParam = result.getData();
            return itemParam.getValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("获取检测部门错误", ResultCode.FAILED);
        }
    }
}
