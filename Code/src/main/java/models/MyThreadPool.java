package models;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadPool {
    private static ExecutorService executorService;

    public static ExecutorService getExecutor() {
        if (executorService == null) {
            var cpuThreads = Runtime.getRuntime().availableProcessors();
            executorService = Executors.newFixedThreadPool(cpuThreads);
            return executorService;
        }

        return executorService;

    }

    public static void shutdown() {
        if (executorService != null)
            executorService.shutdown();
    }

    public static void shutdownNow() {
        if (executorService != null)
            executorService.shutdownNow();
    }
}
