package es.amplia.oda.subsystem.poller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class SchedulerImpl implements Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerImpl.class);
    private static final long STOP_OPERATIONS_TIMEOUT = 10;


    private final ScheduledExecutorService executorService;
    private final List<ScheduledFuture> tasks = new ArrayList<>();


    SchedulerImpl(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public void schedule(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ScheduledFuture scheduledFuture =
                executorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        tasks.add(scheduledFuture);
    }

    public void clear() {
        tasks.forEach(task -> task.cancel(false));
    }

    public void close() {
        clear();
        stopPendingOperations();
    }

    private void stopPendingOperations() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(STOP_OPERATIONS_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("The shutdown of the pool of threads its taking more than {} seconds. Will not wait longer.",
                    STOP_OPERATIONS_TIMEOUT);
            Thread.currentThread().interrupt();
        }
    }
}
