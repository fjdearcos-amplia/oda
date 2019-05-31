package es.amplia.oda.subsystem.poller;

import java.util.concurrent.TimeUnit;

interface Scheduler extends AutoCloseable {
    void schedule(Runnable command, long initialDelay, long delay, TimeUnit unit);
    void clear();
    @Override
    void close();
}
