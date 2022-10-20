package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.DrawingApply;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 王瑞
 * @Description 图纸申请服务
 */
public interface DrawingApplyService extends IService<DrawingApply> {
    @Transactional
    public CommonResult importExcelDrawingApply(MultipartFile file);
}
