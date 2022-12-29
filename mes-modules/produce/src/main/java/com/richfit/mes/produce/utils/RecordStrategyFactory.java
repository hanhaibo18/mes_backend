package com.richfit.mes.produce.utils;

import com.richfit.mes.produce.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 探伤记录模板策略工厂  根据模板 走不同的修改方法
 * @author rzw
 * @date 2022-12-28 18:42
 */
@Service
public class RecordStrategyFactory {

    private final List<RecordStragegy> recordStragegy;

    private static final Map<String, RecordStragegy> strategies = new HashMap<>();

    public RecordStrategyFactory(List<RecordStragegy> recordStragegy) {
        this.recordStragegy = recordStragegy;
    }


    @PostConstruct
    public void initStrategy() {
        for (RecordStragegy stragegy : recordStragegy) {
            strategies.put(stragegy.getType(),stragegy);
        }
    }


    public static RecordStragegy getRecordStragegy(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type should not be empty.");
        }
        return strategies.get(type);
    }
}
