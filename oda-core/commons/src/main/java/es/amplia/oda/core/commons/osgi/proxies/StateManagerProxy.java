package es.amplia.oda.core.commons.osgi.proxies;

import es.amplia.oda.core.commons.interfaces.DatastreamValue;
import es.amplia.oda.core.commons.interfaces.StateManager;

import org.osgi.framework.BundleContext;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StateManagerProxy implements StateManager, AutoCloseable {

    private final OsgiServiceProxy<StateManager> proxy;

    public StateManagerProxy(BundleContext bundleContext) {
        this.proxy = new OsgiServiceProxy<>(StateManager.class, bundleContext);
    }

    @Override
    public CompletableFuture<DatastreamValue> getDatastreamInformation(String deviceId, String datastreamId) {
        return proxy.callFirst(stateManager -> stateManager.getDatastreamInformation(deviceId, datastreamId));
    }

    @Override
    public CompletableFuture<Set<DatastreamValue>> getDatastreamsInformation(String deviceId, Set<String> datastreamIds) {
        return proxy.callFirst(stateManager -> stateManager.getDatastreamsInformation(deviceId, datastreamIds));
    }

    @Override
    public CompletableFuture<Set<DatastreamValue>> getDeviceInformation(String deviceId) {
        return proxy.callFirst(stateManager -> stateManager.getDeviceInformation(deviceId));
    }

    @Override
    public void close() {
        proxy.close();
    }
}
