package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/8 15:38
 */

@Data
public class CertQueryDto {

    String certificateNo;

    String drawingNo;

    String branchCode;

    int page;

    int limit;
}
