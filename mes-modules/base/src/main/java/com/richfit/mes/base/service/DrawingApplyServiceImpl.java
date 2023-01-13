package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.DrawingApplyMapper;
import com.richfit.mes.base.entity.DrawingApplyExcelEntity;
import com.richfit.mes.base.util.DrawingNoUtil;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DrawingApply;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author 王瑞
 * @Description 图纸申请服务
 */
@Service
public class DrawingApplyServiceImpl extends ServiceImpl<DrawingApplyMapper, DrawingApply> implements DrawingApplyService {

    public static String DRAWING_APPLY_NO_NULL_MESSAGE = "图号不能为空！";
    public static String DRAWING_APPLY_ID_NULL_MESSAGE = "图纸申请ID不能为空！";
    public static String DRAWING_APPLY_SUCCESS_MESSAGE = "操作成功!";
    public static String DRAWING_APPLY_FAILED_MESSAGE = "操作失败，请重试！";
    public static String DRAWING_APPLY_EXCEPTION_MESSAGE = "操作失败：";
    public static String DRAWING_APPLY_IMPORT_EXCEL_SUCCESS_MESSAGE = "导入成功!";
    @Autowired
    private DrawingApplyMapper drawingApplyMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult importExcelDrawingApply(MultipartFile file,String branchCode) {
        CommonResult result = null;
        //封装证件信息实体类
        java.lang.reflect.Field[] fields = DrawingApplyExcelEntity.class.getDeclaredFields();
        //封装证件信息实体类
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));

            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());

            //将导入的excel数据生成证件实体类list
        List<DrawingApplyExcelEntity> list = null;
        try {
            file.transferTo(excelFile);
            list = ExcelUtils.importExcel(excelFile, DrawingApplyExcelEntity.class, fieldNames, 1, 0, 0, tempName.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (DrawingApplyExcelEntity drawingApply : list) {
                if (StringUtils.isNullOrEmpty(drawingApply.getDrawingNo())) {
                    throw new RuntimeException(DRAWING_APPLY_NO_NULL_MESSAGE);
                } else {
                    QueryWrapper<DrawingApply> queryWrapper = new QueryWrapper<DrawingApply>();
                    DrawingNoUtil.queryEq(queryWrapper,"drawing_no",drawingApply.getDrawingNo());
                    queryWrapper.eq("branch_code", branchCode);
                    queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                    DrawingApply oldApply = this.getOne(queryWrapper);
                    if (oldApply != null && !StringUtils.isNullOrEmpty(oldApply.getId())) {
                        throw new GlobalException(drawingApply.getDrawingNo()+" :已有该图号的申请！", ResultCode.FAILED);
                    } else {
                        DrawingApply da=new DrawingApply();
                        da.setBranchCode(branchCode);
                        da.setDrawingDesc(drawingApply.getDrawingDesc());
                        da.setDrawingNo(drawingApply.getDrawingNo());
                        da.setPdmDrawingNo(drawingApply.getPdmDrawingNo());
                        da.setStatus("0");
                        da.setDataGroup(branchCode);
                        da.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                        da.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                        boolean bool = this.save(da);
                    }
                }
            }
        return CommonResult.success( DRAWING_APPLY_IMPORT_EXCEL_SUCCESS_MESSAGE);
    }

    @Override
    public List<DrawingApply> list(DrawingApply drawingApply) {
        return drawingApplyMapper.list(drawingApply);
    }
}
