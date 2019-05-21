package es.amplia.oda.statemanager.realtime;

import es.amplia.oda.core.commons.interfaces.DatastreamsSetter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class DatastreamsSettersFinderImpl implements DatastreamsSettersFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatastreamsSettersFinderImpl.class);

    private final DatastreamsSettersLocator datastreamsSettersLocator;

    DatastreamsSettersFinderImpl(DatastreamsSettersLocator datastreamsSettersLocator) {
        this.datastreamsSettersLocator = datastreamsSettersLocator;
    }

    @Override
    public Return getSettersSatisfying(String deviceId, Set<String> datastreamIdentifiers) {
        try {
            final Set<String> notFoundIds = new HashSet<>(datastreamIdentifiers);
            Map<String, DatastreamsSetter> providers = datastreamsSettersLocator.getDatastreamsSetters().stream()
                    .filter(dsp-> datastreamIdentifiers.contains(dsp.getDatastreamIdSatisfied()))
                    .filter(dsp-> dsp.getDevicesIdManaged().contains(deviceId))
                    .peek(dsp-> notFoundIds.remove(dsp.getDatastreamIdSatisfied()))
                    .collect(Collectors.toMap(DatastreamsSetter::getDatastreamIdSatisfied, dsp -> dsp));
            return new Return(providers, notFoundIds);
        } catch (Exception e) {
            LOGGER.error("Exception when trying to determine providers satisfying {}/{}: {}", deviceId,
                    datastreamIdentifiers, e);
            return new Return(new HashMap<>(), datastreamIdentifiers);
        }
    }

    @Override
    public void close() {
        try {
            datastreamsSettersLocator.close();
        } catch (Exception e) {
            LOGGER.warn("Error releasing Datastreams Setters Locator resource: ", e);
        }
    }
}
