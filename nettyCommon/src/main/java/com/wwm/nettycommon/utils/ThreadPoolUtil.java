package com.wwm.nettycommon.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolUtil {

    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2 * Runtime.getRuntime().availableProcessors() + 1,
            2 * Runtime.getRuntime().availableProcessors() + 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(500),
            new CustomizableThreadFactory("executor_")) {
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            if (t != null) {
                log.error("", t);
            }
        }
    };
}
