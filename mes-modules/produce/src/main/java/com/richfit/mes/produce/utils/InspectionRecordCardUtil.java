package com.richfit.mes.produce.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCardContent;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.service.CodeRuleService;

import java.util.List;

/**
 * 质量检测卡excel封装工具类
 *
 * @author zhiqiang.lu
 * @date 2022.9.20
 */
public class InspectionRecordCardUtil {

    /**
     * 功能描述: 质量检验卡机加excel封装方法
     *
     * @param writer                      excel写入工具
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public void jj(ExcelWriter writer, ProduceInspectionRecordCard produceInspectionRecordCard) {
        writer.writeCellValue(0, 2, produceInspectionRecordCard.getCardNo());
        writer.writeCellValue(11, 3, produceInspectionRecordCard.getSparePartsName());
        writer.writeCellValue(11, 4, produceInspectionRecordCard.getSparePartsDrawingNo());
        writer.writeCellValue(14, 3, produceInspectionRecordCard.getSparePartsNo());
        writer.writeCellValue(14, 4, produceInspectionRecordCard.getTexture());
    }

    /**
     * 功能描述: 质量检验卡装配excel封装方法
     *
     * @param writer                      excel写入工具
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public void zp(ExcelWriter writer, ProduceInspectionRecordCard produceInspectionRecordCard) {
        writer.writeCellValue(0, 2, produceInspectionRecordCard.getCardNo());
        writer.writeCellValue(14, 3, produceInspectionRecordCard.getSparePartsNo());
    }


    /**
     * 功能描述: 质量检验卡机加内容明细excel封装方法
     *
     * @param writer                      excel写入工具
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public void content(ExcelWriter writer, ProduceInspectionRecordCard produceInspectionRecordCard) {
        List<ProduceInspectionRecordCardContent> inspectionRecordCardContentList = produceInspectionRecordCard.getProduceInspectionRecordCardContentList();
        int y = 7;
        for (ProduceInspectionRecordCardContent inspectionRecordCardContent : inspectionRecordCardContentList) {
            writer.writeCellValue(0, y, y - 6);
            writer.writeCellValue(2, y, inspectionRecordCardContent.getInspectionItemNo());
            writer.writeCellValue(3, y, inspectionRecordCardContent.getInspectionItemName());
            writer.writeCellValue(4, y, inspectionRecordCardContent.getInspectionContent());
            writer.writeCellValue(6, y, inspectionRecordCardContent.getInspectionRequirement());
            writer.writeCellValue(8, y, inspectionRecordCardContent.getInspectionTesting());
            writer.writeCellValue(10, y, inspectionRecordCardContent.getInspectionResult());
            writer.writeCellValue(11, y, inspectionRecordCardContent.getInspectionQualified());
            writer.writeCellValue(12, y, inspectionRecordCardContent.getInspectionUserName());
            writer.writeCellValue(13, y, inspectionRecordCardContent.getInspectionDate());
            y++;
        }
    }

    /**
     * 功能描述: 质量检验卡通用封装excel方法
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public static ExcelWriter excel(ProduceInspectionRecordCard produceInspectionRecordCard) throws Exception {
        InspectionRecordCardUtil inspectionRecordCardUtil = new InspectionRecordCardUtil();
        String excelName = inspectionRecordCardUtil.excelName(produceInspectionRecordCard);
        ExcelWriter writer = ExcelUtil.getReader(ResourceUtil.getStream("excel/" + excelName)).getWriter();
        if (TrackHead.TRACKHEAD_CLASSES_JJ.equals(produceInspectionRecordCard.getClasses())) {
            //机加
            inspectionRecordCardUtil.jj(writer, produceInspectionRecordCard);
        } else if (TrackHead.TRACKHEAD_CLASSES_ZP.equals(produceInspectionRecordCard.getClasses())) {
            //装配
            inspectionRecordCardUtil.zp(writer, produceInspectionRecordCard);
        }
        inspectionRecordCardUtil.content(writer, produceInspectionRecordCard);
        return writer;
    }


    /**
     * 功能描述: 获取excel文件名
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public String excelName(ProduceInspectionRecordCard produceInspectionRecordCard) throws Exception {
        String excelName = "";
        if (TrackHead.TRACKHEAD_CLASSES_JJ.equals(produceInspectionRecordCard.getClasses())) {
            //机加模板名称
            excelName = "检验记录卡机加.xlsx";
        } else if (TrackHead.TRACKHEAD_CLASSES_ZP.equals(produceInspectionRecordCard.getClasses())) {
            //装配模板名称
            excelName = "检验记录卡装配.xlsx";
        }
        if (StrUtil.isBlank(excelName)) {
            throw new Exception("获取excel文件名失败");
        }
        return excelName;
    }

    /**
     * 功能描述: 获取excel文件名
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public static void cardNo(ProduceInspectionRecordCard produceInspectionRecordCard, CodeRuleService codeRuleService) throws Exception {
        String code = Code.code("inspection_card", produceInspectionRecordCard.getTenantId(), produceInspectionRecordCard.getBranchCode(), codeRuleService);
        produceInspectionRecordCard.setCardNo(code);
        Code.codeUpdate("inspection_card", produceInspectionRecordCard.getCardNo(), produceInspectionRecordCard.getTenantId(), produceInspectionRecordCard.getBranchCode(), codeRuleService);
    }
}
