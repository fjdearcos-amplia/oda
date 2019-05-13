package es.amplia.oda.core.commons.interfaces;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface StateManager {
    CompletableFuture<DatastreamValue> getDatastreamInformation(String deviceId, String datastreamId);
    CompletableFuture<Set<DatastreamValue>> getDatastreamsInformation(String deviceId, Set<String> datastreamIds);
    CompletableFuture<Set<DatastreamValue>> getDeviceInformation(String deviceId);
}
