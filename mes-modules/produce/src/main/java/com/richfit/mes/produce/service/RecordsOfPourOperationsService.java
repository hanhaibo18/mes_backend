package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.RecordsOfPourOperations;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * (RecordsOfPourOperations)表服务接口
 *
 * @author makejava
 * @since 2023-05-12 15:53:49
 */
public interface RecordsOfPourOperationsService extends IService<RecordsOfPourOperations> {

    RecordsOfPourOperations getByPrechargeFurnaceId(Long prechargeFurnaceId);

    Boolean init(Long prechargeFurnaceId, String branchCode);

    Boolean update(RecordsOfPourOperations recordsOfPourOperations);

    Boolean check(List<String> ids, int state);

    IPage<RecordsOfPourOperations> bzzcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, int page, int limit);

    IPage<RecordsOfPourOperations> czgcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, int page, int limit);

    void export(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, HttpServletResponse response);

    boolean isBzz();
}

