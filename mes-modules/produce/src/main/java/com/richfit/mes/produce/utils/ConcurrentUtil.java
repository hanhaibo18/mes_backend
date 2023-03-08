package com.richfit.mes.produce.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 并发工具类
 *
 * @author hengyumo
 * @since 2021-07-13
 */
public class ConcurrentUtil {


    /**
     * 执行任务
     *
     * @param executorService ExecutorService
     * @param callable        回调
     * @param <T>             返回的结果集Future泛型
     * @return Future泛型
     */
    public static <T> Future<T> doJob(ExecutorService executorService, Callable<T> callable) {

        return executorService.submit(callable);
    }

    /**
     * 获取结果集，执行时会阻塞直到有结果，中间的异常不会被静默
     *
     * @param future Future
     * @param <T>    返回的结果集泛型
     * @return T
     */
    public static <T> T futureGet(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {

            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
