package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.produce.entity.SjtjDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SjtjService extends IService<SjtjDto> {

    List<SjtjDto> query1(@Param("branchCode") String branchCode,@Param("createTime") String createTime, @Param("endTime") String endTime);


}
