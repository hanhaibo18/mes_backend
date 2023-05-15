package com.kld.mes.erp.entity.feeding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 订单投料结果集
 *
 * @author wcy
 * @date 2023/5/15 16:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedingResult {

    private String feedingCode;
    private String code;
    private String msg;
}
