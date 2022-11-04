package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.common.model.produce.DisqualificationUserOpinion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: SignedRecordsVo.java
 * @Author: Hou XinYu
 * @Description: 签核记录
 * @CreateTime: 2022年10月17日 16:31:00
 */
@Data
public class SignedRecordsVo {
    @ApiModelProperty(value = "意见Id", dataType = "String")
    private String id;
    @ApiModelProperty(value = "单位名称", dataType = "String")
    private String branchCodeName;
    @ApiModelProperty(value = "办理人", dataType = "String")
    private String userName;
    @ApiModelProperty(value = "办理时间", dataType = "String")
    private Date handlingTime;
    @ApiModelProperty(value = "处理意见")
    private String opinion;
    @ApiModelProperty(value = "最终结果")
    private String finalResult;

    public static List<SignedRecordsVo> list(List<DisqualificationUserOpinion> userOpinions) {
        List<SignedRecordsVo> list = new ArrayList<>();
        for (DisqualificationUserOpinion opinion : userOpinions) {
            SignedRecordsVo signedRecordsVo = new SignedRecordsVo();
            signedRecordsVo.setId(opinion.getId());
            signedRecordsVo.setUserName(opinion.getUserName());
            signedRecordsVo.setBranchCodeName(opinion.getUserBranchName());
            signedRecordsVo.setHandlingTime(opinion.getCreateTime());
            signedRecordsVo.setOpinion(opinion.getOpinion());
            list.add(signedRecordsVo);
        }
        return list;
    }

}
