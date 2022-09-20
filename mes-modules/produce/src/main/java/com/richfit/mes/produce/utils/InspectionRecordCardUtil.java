package com.richfit.mes.produce.utils;

import cn.hutool.poi.excel.ExcelWriter;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;
import com.richfit.mes.common.model.produce.TrackHead;

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
        writer.writeCellValue(11, 3, produceInspectionRecordCard.getSparePartsName());
        writer.writeCellValue(11, 4, produceInspectionRecordCard.getSparePartsDrawingNo());
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
    }

    /**
     * 功能描述: 质量检验卡通用封装excel方法
     *
     * @param writer                      excel写入工具
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public static void excel(ExcelWriter writer, ProduceInspectionRecordCard produceInspectionRecordCard) {
        InspectionRecordCardUtil inspectionRecordCardUtil = new InspectionRecordCardUtil();
        if (TrackHead.TRACKHEAD_CLASSES_JJ.equals(produceInspectionRecordCard.getClasses())) {
            //机加
            inspectionRecordCardUtil.jj(writer, produceInspectionRecordCard);
        } else if (TrackHead.TRACKHEAD_CLASSES_ZP.equals(produceInspectionRecordCard.getClasses())) {
            //装配
            inspectionRecordCardUtil.zp(writer, produceInspectionRecordCard);
        }
    }
}
