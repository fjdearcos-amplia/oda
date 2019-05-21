package es.amplia.oda.statemanager.realtime;

import es.amplia.oda.core.commons.interfaces.DatastreamsSetter;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class DatastreamsSettersLocatorOsgi implements DatastreamsSettersLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatastreamsSettersLocatorOsgi.class);


    private final ServiceTracker<DatastreamsSetter, DatastreamsSetter> datastreamsSetterTracker;

    DatastreamsSettersLocatorOsgi(BundleContext bundleContext) {
        datastreamsSetterTracker = new ServiceTracker<>(bundleContext, DatastreamsSetter.class, null);
        datastreamsSetterTracker.open();
    }


    @Override
    public List<DatastreamsSetter> getDatastreamsSetters() {
        List<DatastreamsSetter> returned = new ArrayList<>();
        Object[] providers = datastreamsSetterTracker.getServices();
        if(providers==null) {
            LOGGER.error("There are no OSGi bundles for DatastreamsSetter");
            return returned;
        }

        for(Object obj: providers) {
            if(!(obj instanceof DatastreamsSetter)) {
                LOGGER.error("DatastreamsSetter found is not a subclass of DatastreamsSetter");
                continue;
            }
            DatastreamsSetter provider = (DatastreamsSetter) obj;
            returned.add(provider);
        }
        LOGGER.debug("{} DatastreamsSetters currently registered in the system", returned.size());
        return returned;
    }

    @Override
    public void close() {
        datastreamsSetterTracker.close();
    }
}
