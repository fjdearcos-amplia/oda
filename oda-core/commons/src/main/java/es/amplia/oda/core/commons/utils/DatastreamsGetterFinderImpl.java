package es.amplia.oda.core.commons.utils;

import es.amplia.oda.core.commons.interfaces.DatastreamsGetter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Collections;
import java.util.stream.Collectors;

public class DatastreamsGetterFinderImpl implements DatastreamsGetterFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatastreamsGetterFinderImpl.class);

    private final DatastreamsGettersLocator datastreamsGettersLocator;

    public DatastreamsGetterFinderImpl(DatastreamsGettersLocator datastreamsGettersLocator) {
        this.datastreamsGettersLocator = datastreamsGettersLocator;
    }

    @Override
    public Return getGettersSatisfying(DevicePattern deviceIdPattern, Set<String> datastreamIdentifiers) {
        if (deviceIdPattern == null)
            throw new IllegalArgumentException("DevicePattern for getGettersSatisfying must be not null");
        try {
            final Set<String> notFoundIds = new HashSet<>(datastreamIdentifiers);
            List<DatastreamsGetter> providers = datastreamsGettersLocator.getDatastreamsGetters().stream().
                    filter(dsp-> !java.util.Collections.disjoint(java.util.Collections.singleton(dsp.getDatastreamIdSatisfied()),
                                     datastreamIdentifiers)).
                    filter(dsp-> dsp.getDevicesIdManaged().stream().anyMatch(deviceIdPattern::match)).
                    peek(dsp-> notFoundIds.remove(dsp.getDatastreamIdSatisfied())).
                    collect(Collectors.toList());
            return new Return(providers, notFoundIds);
        } catch (Exception e) {
            LOGGER.error("Exception when trying to determine providers satisfying {}/{}: {}", deviceIdPattern,
                    datastreamIdentifiers, e);
            return new Return(Collections.emptyList(), new HashSet<>(datastreamIdentifiers));
        }
    }

    @Override
    public List<DatastreamsGetter> getGettersOfDevice(String deviceId) {
        return datastreamsGettersLocator.getDatastreamsGetters().stream()
                .filter(datastreamsGetter -> datastreamsGetter.getDevicesIdManaged().contains(deviceId))
                .collect(Collectors.toList());
    }
}
