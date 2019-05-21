package es.amplia.oda.subsystem.realtimestatemanager;

import es.amplia.oda.core.commons.interfaces.StateManager;
import es.amplia.oda.core.commons.utils.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<StateManager> registration;

    @Override
    public void start(BundleContext bundleContext) {
        DatastreamsGettersLocator datastreamsGettersLocator = new DatastreamsGettersLocatorOsgi(bundleContext);
        DatastreamsGetterFinder datastreamsGetterFinder = new DatastreamsGetterFinderImpl(datastreamsGettersLocator);
        DatastreamsSettersLocator datastreamsSettersLocator = new DatastreamsSettersLocatorOsgi(bundleContext);
        DatastreamsSettersFinder datastreamsSetterFinder = new DatastreamsSettersFinderImpl(datastreamsSettersLocator);
        StateManager stateManager = new RealTimeStateManager(datastreamsGetterFinder, datastreamsSetterFinder);
        registration = bundleContext.registerService(StateManager.class, stateManager, null);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        registration.unregister();
    }
}
