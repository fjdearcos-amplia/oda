package es.amplia.oda.statemanager.api;

import es.amplia.oda.event.api.Event;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventConstants;

import java.util.Dictionary;
import java.util.Hashtable;

public class OsgiEventHandler implements EventHandler {

    static final String INTERNAL_EVENT_TOPIC = "oda/internal/event";
    static final String DATASTREAM_ID_PROPERTY_NAME = "datastreamId";
    static final String DEVICE_ID_PROPERTY_NAME = "deviceId";
    static final String PATH_PROPERTY_NAME = "path";
    static final String AT_PROPERTY_NAME = "at";
    static final String VALUE_PROPERTY_NAME = "value";

    private final ServiceRegistration<org.osgi.service.event.EventHandler> registration;

    private StateManager stateManager;

    public OsgiEventHandler(BundleContext bundleContext) {
        Dictionary<String, Object> props = new Hashtable<>();
        props.put(EventConstants.EVENT_TOPIC, new String[] { INTERNAL_EVENT_TOPIC });
        registration =
                bundleContext.registerService(org.osgi.service.event.EventHandler.class, new EventHandlerImpl(), props);
    }

    @Override
    public void registerStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void unregisterStateManager() {
        this.stateManager = null;
    }

    class EventHandlerImpl implements org.osgi.service.event.EventHandler {
        @Override
        public void handleEvent(org.osgi.service.event.Event osgiEvent) {
            String datastreamId = (String) osgiEvent.getProperty(DATASTREAM_ID_PROPERTY_NAME);
            String deviceId = (String) osgiEvent.getProperty(DEVICE_ID_PROPERTY_NAME);
            String[] path = (String[]) osgiEvent.getProperty(PATH_PROPERTY_NAME);
            Long at = (Long) osgiEvent.getProperty(AT_PROPERTY_NAME);
            Object value = osgiEvent.getProperty(VALUE_PROPERTY_NAME);
            Event event = new Event(datastreamId, deviceId, path, at, value);

            notifyStateManager(event);
        }

        private void notifyStateManager(es.amplia.oda.event.api.Event event) {
            if (stateManager != null) {
                stateManager.onReceivedEvent(event);
            }
        }
    }

    @Override
    public void close() {
        registration.unregister();
    }
}
