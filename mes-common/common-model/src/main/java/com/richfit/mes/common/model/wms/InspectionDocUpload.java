package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 *  inspection_doc_upload  MES报检单质检结果上传WMS
 */
@Data
public class InspectionDocUpload implements Serializable {
    /**
     * 报检单ID 唯一
     */
    private String id;

    /**
     * 综合判定 外购
     * 合格/不合格/部分合格/退货
     */
    private Integer finalResult;

    /**
     * 合格数量 外购/外协
     */
    private Integer qualifiedQuantity;

    /**
     * 不合格数量 外购/外协
     */
    private Integer unqualifiedQuantity;

    /**
     * 资料是否齐全 外购
     */
    private String dataFlag;

    /**
     * 外观检验 外购
     */
    private String appearanceFlag;

    /**
     * 尺寸检验 外购
     */
    private String sizeFlag;

    /**
     * 其他检验 外购
     */
    private String otherFlag;

    /**
     * 质量资料（份） 外协
     */
    private String qualityFileCount;

    /**
     * 外观及尺寸检查结果 外协
     */
    private String acceptanceResult;

    /**
     * 质量证明文件审核项目 外协，化学成分/力学性能/硬度等
     */
    private String qualityFileItem;

    /**
     * 审核结果  外协，合格/不合格
     */
    private String qualityFileResult;

    /**
     * 质量证明文件资料 外协，压缩包路径
     */
    private String qualityFileUrl;

    /**
     * 复验结果 外协
     */
    private String reinsResult;

    /**
     * 不合格品处理单号 外协
     */
    private String nonHandlingNo;

    /**
     * 抽检样品数量 外协
     */
    private String samplingQuantity;

    /**
     * 质检员
     */
    private String qualityInspector;

    /**
     * 同步日期
     */
    private Date inspectionDate;

    /**
     * 产品编号列表
     */
    @TableField(exist = false)
    private List<ProductNoList> lineList;

    /**
     * 外协-入场验收情况列表
     */
    @TableField(exist = false)
    private List<AdmissionAcceptanceList> reinsList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}