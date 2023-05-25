package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.RecordsOfSteelmakingOperations;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 炼钢作业记录表(RecordsOfSteelmakingOperations)表服务接口
 *
 * @author makejava
 * @since 2023-05-12 15:54:38
 */
public interface RecordsOfSteelmakingOperationsService extends IService<RecordsOfSteelmakingOperations> {

    RecordsOfSteelmakingOperations getByPrechargeFurnaceId(Long prechargeFurnaceId);

    Boolean init(Long prechargeFurnaceId, String branchCode);

    Boolean update(RecordsOfSteelmakingOperations recordsOfSteelmakingOperations);

    Boolean check(List<String> ids, int state);

    IPage<RecordsOfSteelmakingOperations> bzzcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit);

    IPage<RecordsOfSteelmakingOperations> czgcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit);

    void export(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, HttpServletResponse response);

    Boolean delete(List<String> ids);
}

