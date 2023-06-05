package com.richfit.mes.base.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author HanHaiBo
 * @date 2023/6/5 12:32
 */
@Data
public class PdmDto {
    //图号
    private String drawNo;
    //版本号
    private String ver;
    //类型
    private String type;
    //名称
    private String name;
    //文件类型
    private String fileType;
    //发布人
    private String publisher;
    //发布时间
    private Date publishTime;
    //文件地址
    private String fileUrl;
}
