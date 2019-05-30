package es.amplia.oda.datastreams.gpio;

import es.amplia.oda.core.commons.interfaces.DatastreamsGetter;
import es.amplia.oda.core.commons.interfaces.DatastreamsSetter;
import es.amplia.oda.core.commons.utils.ServiceRegistrationManager;

import java.util.HashMap;
import java.util.Map;

public class GpioDatastreamsManager implements AutoCloseable {

    private final GpioDatastreamsFactory gpioDatastreamsFactory;
    private final ServiceRegistrationManager<DatastreamsGetter> datastreamsGetterRegistrationManager;
    private final ServiceRegistrationManager<DatastreamsSetter> datastreamsSetterRegistrationManager;
    private final Map<String, GpioDatastreamsEventHandler> datastreamsEvents = new HashMap<>();


    GpioDatastreamsManager(GpioDatastreamsFactory gpioDatastreamsFactory,
                           ServiceRegistrationManager<DatastreamsGetter> datastreamsGetterRegistrationManager,
                           ServiceRegistrationManager<DatastreamsSetter> datastreamsSetterRegistrationManager) {
        this.gpioDatastreamsFactory = gpioDatastreamsFactory;
        this.datastreamsGetterRegistrationManager = datastreamsGetterRegistrationManager;
        this.datastreamsSetterRegistrationManager = datastreamsSetterRegistrationManager;
    }

    public void addDatastreamGetter(int pinIndex, String datastreamId) {
        GpioDatastreamsGetter datastreamsGetter =
                gpioDatastreamsFactory.createGpioDatastreamsGetter(datastreamId, pinIndex);
        datastreamsGetterRegistrationManager.register(datastreamsGetter);
    }

    public void addDatastreamSetter(int pinIndex, String datastreamId) {
        GpioDatastreamsSetter datastreamsSetter =
                gpioDatastreamsFactory.createGpioDatastreamsSetter(datastreamId, pinIndex);
        datastreamsSetterRegistrationManager.register(datastreamsSetter);
    }

    public void addDatastreamEvent(int pinIndex, String datastreamId) {
        GpioDatastreamsEventHandler datastreamsEventSender;

        if (datastreamsEvents.containsKey(datastreamId)) {
            datastreamsEventSender = datastreamsEvents.get(datastreamId);
            datastreamsEventSender.close();
        }

        datastreamsEventSender = gpioDatastreamsFactory.createGpioDatastreamsEvent(datastreamId, pinIndex);
        datastreamsEvents.put(datastreamId, datastreamsEventSender);
    }

    public void close() {
        datastreamsGetterRegistrationManager.unregister();
        datastreamsSetterRegistrationManager.unregister();
        datastreamsEvents.values().forEach(GpioDatastreamsEventHandler::close);
        datastreamsEvents.clear();
    }
}
