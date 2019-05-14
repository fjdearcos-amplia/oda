package es.amplia.oda.subsystem.realtimestatemanager;

import es.amplia.oda.core.commons.interfaces.DatastreamValue;
import es.amplia.oda.core.commons.interfaces.DatastreamsGetter;
import es.amplia.oda.core.commons.interfaces.StateManager;
import es.amplia.oda.core.commons.utils.DatastreamsGetterFinder;
import es.amplia.oda.core.commons.utils.DevicePattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

class RealTimeStateManager implements StateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeStateManager.class);

    private final DatastreamsGetterFinder datastreamsGetterFinder;

    RealTimeStateManager(DatastreamsGetterFinder datastreamsGetterFinder) {
        this.datastreamsGetterFinder = datastreamsGetterFinder;
    }

    @Override
    public CompletableFuture<DatastreamValue> getDatastreamInformation(String deviceId, String datastreamId) {
        return getDatastreamsInformation(deviceId, Collections.singleton(datastreamId))
                .thenApply(set -> set.toArray(new DatastreamValue[0])[0]);
    }

    @Override
    public CompletableFuture<Set<DatastreamValue>> getDatastreamsInformation(String deviceId, Set<String> datastreamIds) {
        LOGGER.debug("Getting values for device '{}': {}", deviceId, datastreamIds);

        DatastreamsGetterFinder.Return finderReturn =
                datastreamsGetterFinder.getGettersSatisfying(new DevicePattern(deviceId), datastreamIds);

        Set<CompletableFuture<DatastreamValue>> notFoundValues =
                getNotFoundIdsAsFutures(deviceId, finderReturn.getNotFoundIds());
        Set<CompletableFuture<DatastreamValue>> allRecollectedValuesFutures =
                getFoundIdsAsFutures(deviceId, finderReturn.getGetters());
        allRecollectedValuesFutures.addAll(notFoundValues);

        CompletableFuture<Set<DatastreamValue>> future = allOf(allRecollectedValuesFutures);

        LOGGER.debug("Wiring done. Waiting for all values to be complete.");
        return future;
    }

    private Set<CompletableFuture<DatastreamValue>> getNotFoundIdsAsFutures(String deviceId,
                                                                             Set<String> notFoundDatastreamIds) {
        return notFoundDatastreamIds.stream()
                .map(datastreamId -> createDatastreamValueNotFound(deviceId, datastreamId))
                .map(CompletableFuture::completedFuture)
                .collect(Collectors.toSet());
    }

    private DatastreamValue createDatastreamValueNotFound(String deviceId, String datastreamId) {
        return new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), null, DatastreamValue.Status.NOT_FOUND, null);
    }

    private Set<CompletableFuture<DatastreamValue>> getFoundIdsAsFutures(String deviceId,
                                                                          List<DatastreamsGetter> getters) {
        return getters.stream()
                .map(datastreamsGetter -> getValueFromFutureHandlingExceptions(deviceId, datastreamsGetter))
                .collect(Collectors.toSet());
    }

    private CompletableFuture<DatastreamValue> getValueFromFutureHandlingExceptions(String deviceId,
                                                                                    DatastreamsGetter datastreamsGetter) {
        String datastreamId = datastreamsGetter.getDatastreamIdSatisfied();
        try {
            CompletableFuture<DatastreamsGetter.CollectedValue> getFuture = datastreamsGetter.get(deviceId);
            return getFuture.handle((ok,error)-> {
                if (ok != null) {
                    return new DatastreamValue(deviceId, datastreamId, ok.getAt(), ok.getValue(), DatastreamValue.Status.OK, null);
                } else {
                    return new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), null, DatastreamValue.Status.PROCESSING_ERROR, error.getMessage());
                }
            });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), null, DatastreamValue.Status.PROCESSING_ERROR, e.getMessage()));
        }
    }

    private static <T> CompletableFuture<Set<T>> allOf(Set<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allDoneFuture.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public CompletableFuture<Set<DatastreamValue>> getDeviceInformation(String deviceId) {
        LOGGER.debug("Getting all values for device '{}'", deviceId);

        Set<CompletableFuture<DatastreamValue>> allRecollectedValuesFutures =
                datastreamsGetterFinder.getGettersOfDevice(deviceId).stream()
                        .map(datastreamsGetter -> getValueFromFutureHandlingExceptions(deviceId, datastreamsGetter))
                        .collect(Collectors.toSet());

        CompletableFuture<Set<DatastreamValue>> future = allOf(allRecollectedValuesFutures);

        LOGGER.debug("Wiring done. Waiting for all values to be complete.");
        return future;
    }


    @Override
    public CompletableFuture<DatastreamValue> setDatastreamValue(String deviceId, String datastreamId, Object value) {
        return null;
    }

    @Override
    public CompletableFuture<Set<DatastreamValue>> setDatastreamValues(String deviceId, Map<String, Object> datastreamValues) {
        return null;
    }
}
