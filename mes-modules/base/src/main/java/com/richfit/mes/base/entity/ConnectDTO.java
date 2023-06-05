package com.richfit.mes.base.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wcy
 * @date 2023/6/5 10:07
 */

@Data
public class ConnectDTO extends QueryPageDto implements Serializable {

    /**
     * 交接单号
     */
    private String connectNo;

    /**
     * 配套钻机
     */
    private String driNo;

    /**
     * 工作号
     */
    private String workNo;

    /**
     * 产品图号
     */
    private String drawNo;

    /**
     * 项目bom
     */
    private String bomId;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 校验员
     */
    private String checkUser;

    /**
     * 校验日期
     */
    private Date checkDate;

    /**
     * 提交人
     */
    private String createBy;

    /**
     * 车间编码
     */
    private String branchCode;

    /**
     * 验收人
     */
    private String receiveUser;

    /**
     * 验收单位：默认钻机分公司
     */
    private String receiveUnit;

    /**
     * 子bom信息
     */
    private List<ConnectExtendDTO> connectExtendDTOList;
}
