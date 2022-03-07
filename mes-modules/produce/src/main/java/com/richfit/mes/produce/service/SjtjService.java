package com.richfit.mes.produce.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface SjtjService {
    List query1( @Param("createTime") String createTime,@Param("endTime") String endTime,@Param("branch_code") String branchCode);

}
