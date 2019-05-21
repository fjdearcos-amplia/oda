package es.amplia.oda.subsystem.realtimestatemanager;

import es.amplia.oda.core.commons.interfaces.DatastreamValue;
import es.amplia.oda.core.commons.interfaces.DatastreamsGetter;
import es.amplia.oda.core.commons.interfaces.DatastreamsSetter;
import es.amplia.oda.core.commons.interfaces.StateManager;
import es.amplia.oda.core.commons.utils.DatastreamsGetterFinder;
import es.amplia.oda.core.commons.utils.DatastreamsSettersFinder;
import es.amplia.oda.core.commons.utils.DevicePattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

class RealTimeStateManager implements StateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeStateManager.class);

    private final DatastreamsGetterFinder datastreamsGetterFinder;
    private final DatastreamsSettersFinder datastreamsSettersFinder;

    RealTimeStateManager(DatastreamsGetterFinder datastreamsGetterFinder,
                         DatastreamsSettersFinder datastreamsSettersFinder) {
        this.datastreamsGetterFinder = datastreamsGetterFinder;
        this.datastreamsSettersFinder = datastreamsSettersFinder;
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

        Set<CompletableFuture<DatastreamValue>> values =
                getNotFoundIdsAsFutures(deviceId, finderReturn.getNotFoundIds());
        values.addAll(getValues(deviceId, finderReturn.getGetters()));

        return allOf(values);
    }

    private Set<CompletableFuture<DatastreamValue>> getNotFoundIdsAsFutures(String deviceId,
                                                                             Set<String> notFoundDatastreamIds) {
        return notFoundDatastreamIds.stream()
                .map(datastreamId -> createDatastreamNotFound(deviceId, datastreamId))
                .map(CompletableFuture::completedFuture)
                .collect(Collectors.toSet());
    }

    private DatastreamValue createDatastreamNotFound(String deviceId, String datastreamId) {
        return new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), null,
                DatastreamValue.Status.NOT_FOUND, null);
    }

    private Set<CompletableFuture<DatastreamValue>> getValues(String deviceId, List<DatastreamsGetter> getters) {
        return getters.stream()
                .map(datastreamsGetter -> getValueFromGetFutureHandlingExceptions(deviceId, datastreamsGetter))
                .collect(Collectors.toSet());
    }

    private CompletableFuture<DatastreamValue> getValueFromGetFutureHandlingExceptions(String deviceId,
                                                                                       DatastreamsGetter datastreamsGetter) {
        String datastreamId = datastreamsGetter.getDatastreamIdSatisfied();
        try {
            CompletableFuture<DatastreamsGetter.CollectedValue> getFuture = datastreamsGetter.get(deviceId);
            return getFuture.handle((ok,error)-> {
                if (ok != null) {
                    return new DatastreamValue(deviceId, datastreamId, ok.getAt(), ok.getValue(),
                            DatastreamValue.Status.OK, null);
                } else {
                    return new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), null,
                            DatastreamValue.Status.PROCESSING_ERROR, error.getMessage());
                }
            });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new DatastreamValue(deviceId, datastreamId,
                    System.currentTimeMillis(), null, DatastreamValue.Status.PROCESSING_ERROR, e.getMessage()));
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

        Set<CompletableFuture<DatastreamValue>> values =
                datastreamsGetterFinder.getGettersOfDevice(deviceId).stream()
                        .map(datastreamsGetter -> getValueFromGetFutureHandlingExceptions(deviceId, datastreamsGetter))
                        .collect(Collectors.toSet());

        return allOf(values);
    }


    @Override
    public CompletableFuture<DatastreamValue> setDatastreamValue(String deviceId, String datastreamId, Object value) {
        return setDatastreamValues(deviceId, Collections.singletonMap(datastreamId, value))
                .thenApply(set -> set.toArray(new DatastreamValue[0])[0]);
    }

    @Override
    public CompletableFuture<Set<DatastreamValue>> setDatastreamValues(String deviceId, Map<String, Object> datastreamValues) {
        LOGGER.info("Setting for the device '{}' the values: {}", deviceId, datastreamValues);

        DatastreamsSettersFinder.Return satisfyingSetters =
                datastreamsSettersFinder.getSettersSatisfying(deviceId, datastreamValues.keySet());

        Set<CompletableFuture<DatastreamValue>> values =
                getNotFoundIdsAsFutures(deviceId, satisfyingSetters.getNotFoundIds());
        values.addAll(getNotFoundValues(deviceId, datastreamValues));
        values.addAll(setValues(deviceId, datastreamValues, satisfyingSetters.getSetters()));

        return allOf(values);
    }

    private Set<CompletableFuture<DatastreamValue>> getNotFoundValues(String deviceId, Map<String, Object> datastreamValues) {
        return datastreamValues.entrySet().stream()
                .filter(entry -> Objects.isNull(entry.getValue()))
                .map(entry -> createValueNotFound(deviceId, entry.getKey()))
                .map(CompletableFuture::completedFuture)
                .collect(Collectors.toSet());
    }

    private DatastreamValue createValueNotFound(String deviceId, String datastreamId) {
        return new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), null,
                DatastreamValue.Status.PROCESSING_ERROR, "Datastream has not value to set");
    }

    private Set<CompletableFuture<DatastreamValue>> setValues(String deviceId, Map<String, Object> datastreamValues,
                                                              Map<String, DatastreamsSetter> setters) {
        return datastreamValues.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry ->
                        getValueFromSetFutureHandlingExceptions(deviceId, entry.getKey(),
                                setters.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private CompletableFuture<DatastreamValue> getValueFromSetFutureHandlingExceptions(String deviceId,
                                                                                       String datastreamId,
                                                                                       DatastreamsSetter datastreamsSetter,
                                                                                       Object value) {
        try {
            CompletableFuture<Void> setFuture = datastreamsSetter.set(deviceId, value);
            return setFuture.handle((ok,error)-> {
                if (error != null) {
                    return new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), null,
                            DatastreamValue.Status.PROCESSING_ERROR, error.getMessage());
                } else {
                    return new DatastreamValue(deviceId, datastreamId, System.currentTimeMillis(), value,
                            DatastreamValue.Status.OK, null);
                }
            });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new DatastreamValue(deviceId, datastreamId,
                    System.currentTimeMillis(), null, DatastreamValue.Status.PROCESSING_ERROR, e.getMessage()));
        }
    }
}
