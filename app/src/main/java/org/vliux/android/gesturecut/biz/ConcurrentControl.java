package org.vliux.android.gesturecut.biz;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by vliux on 4/9/14.
 */
public class ConcurrentControl {
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void closeExecutorService(){
        executorService.shutdown();
    }

    public static Future submitTask(Runnable runnable){
        return executorService.submit(runnable);
    }

    public static Future submitTask(Callable callable){
        return executorService.submit(callable);
    }
}
