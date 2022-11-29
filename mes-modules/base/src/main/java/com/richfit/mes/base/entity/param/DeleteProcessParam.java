package com.richfit.mes.base.entity.param;

import lombok.Data;

import java.util.List;

@Data
public class DeleteProcessParam {
    private List<String> drawIdGroup;
    private String dataGroup;
}
