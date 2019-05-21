package es.amplia.oda.statemanager.api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface StateManager {
    CompletableFuture<DatastreamValue> getDatastreamInformation(String deviceId, String datastreamId);
    CompletableFuture<Set<DatastreamValue>> getDatastreamsInformation(String deviceId, Set<String> datastreamIds);
    CompletableFuture<Set<DatastreamValue>> getDeviceInformation(String deviceId);
    CompletableFuture<DatastreamValue> setDatastreamValue(String deviceId, String datastreamId, Object value);
    CompletableFuture<Set<DatastreamValue>> setDatastreamValues(String deviceId, Map<String, Object> datastreamValues);
}
