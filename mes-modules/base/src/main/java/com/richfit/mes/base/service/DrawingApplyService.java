package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.DrawingApply;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 王瑞
 * @Description 图纸申请服务
 */
public interface DrawingApplyService extends IService<DrawingApply> {
    @Transactional
    public CommonResult importExcelDrawingApply(MultipartFile file, String branchCode);

    //新加入图纸个数量查询接口
    List<DrawingApply> list(@Param("param") DrawingApply drawingApply);
}
