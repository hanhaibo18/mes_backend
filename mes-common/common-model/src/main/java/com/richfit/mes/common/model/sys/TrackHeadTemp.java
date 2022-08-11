package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 跟单模板
 */
@Data
public class TrackHeadTemp extends BaseEntity<TrackHeadTemp> {

    /**
     * 模板编号
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 文件ID
     */
    private String fileId;
    
    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 组织机构编号
     */
    private String branchCode;

    /**
     * 类别
     */
    private String type;
    private String sheet1;
    private String sheet2;
    private String sheet3;
    private String sheet4;

}
