package es.amplia.oda.core.commons.interfaces;

import java.util.List;

public interface DatastreamsEventHandler {
    void registerToEventSource() throws Exception;
    void unregisterFromEventSource() throws Exception;
    void publish(String deviceId, String datastreamId, List<String> path, Long at, Object value);
}
