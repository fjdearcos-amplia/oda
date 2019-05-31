package es.amplia.oda.core.commons.interfaces;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDatastreamsEventHandler implements DatastreamsEventHandler {

    private final EventPublisher eventPublisher;

    public AbstractDatastreamsEventHandler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public abstract void registerToEventSource() throws Exception;
    public abstract void unregisterFromEventSource() throws Exception;

    @Override
    public void publish(String deviceId, String datastreamId, List<String> path, Long at, Object value) {
        String[] pathArray = Optional.ofNullable(path).map(list -> list.toArray(new String[0])).orElse(null);
        eventPublisher.publishEvent(deviceId, datastreamId, pathArray, at, value);
    }
}
