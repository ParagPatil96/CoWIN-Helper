package com.patilparag96.cowinhelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class BackgroundTask {
    private static final int POOL_SIZE = 2;
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(POOL_SIZE);
    private static final Map<String, Future> FUTURES = new HashMap<>();

    public static <T> void submit(String id, Callable<T> task){
        FUTURES.put(id, EXECUTOR_SERVICE.submit(task));
    }

    public static <T> T get(String id){
        try {
            return (T) FUTURES.get(id).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new CustomException("Failed while getting results from future " + id , e );
        }
    }
}
