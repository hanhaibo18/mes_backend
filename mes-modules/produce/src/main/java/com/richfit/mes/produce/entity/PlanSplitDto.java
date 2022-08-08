package com.richfit.mes.produce.entity;


import com.richfit.mes.common.model.produce.Plan;
import lombok.Data;


/**
 * @ClassName: planDto
 * @Author: renzewen
 * @Description: TODO
 * @CreateTime: 2022年08月08日 06:42:00
 */
@Data
public class PlanSplitDto {
   //原计划
   private Plan orldPlan;
   //拆分计划
   private Plan newPlan;
}
