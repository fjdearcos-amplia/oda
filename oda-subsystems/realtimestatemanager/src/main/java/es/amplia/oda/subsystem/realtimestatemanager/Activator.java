package es.amplia.oda.subsystem.realtimestatemanager;

import es.amplia.oda.core.commons.interfaces.StateManager;
import es.amplia.oda.core.commons.utils.DatastreamsGetterFinder;
import es.amplia.oda.core.commons.utils.DatastreamsGetterFinderImpl;
import es.amplia.oda.core.commons.utils.DatastreamsGettersLocator;
import es.amplia.oda.core.commons.utils.DatastreamsGettersLocatorOsgi;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<StateManager> registration;

    @Override
    public void start(BundleContext bundleContext) {
        DatastreamsGettersLocator datastreamsGettersLocator = new DatastreamsGettersLocatorOsgi(bundleContext);
        DatastreamsGetterFinder datastreamsGetterFinder = new DatastreamsGetterFinderImpl(datastreamsGettersLocator);
        StateManager stateManager = new RealTimeStateManager(datastreamsGetterFinder);
        registration = bundleContext.registerService(StateManager.class, stateManager, null);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        registration.unregister();
    }
}
