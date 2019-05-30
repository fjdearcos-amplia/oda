package es.amplia.oda.core.commons.interfaces;

import java.util.List;
import java.util.Optional;

public abstract class DatastreamsEventHandler {

    private final EventPublisher eventPublisher;

    public DatastreamsEventHandler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public abstract void registerToEventSource() throws Exception;
    public abstract void unregisterFromEventSource() throws Exception;

    protected void publish(String deviceId, String datastreamId, List<String> path, Long at, Object value) {
        String[] pathArray = Optional.ofNullable(path).map(list -> list.toArray(new String[0])).orElse(null);
        eventPublisher.publishEvent(deviceId, datastreamId, pathArray, at, value);
    }
}
