package es.amplia.oda.datastreams.gpio;

import es.amplia.oda.core.commons.gpio.GpioService;
import es.amplia.oda.core.commons.interfaces.EventPublisher;

import java.util.concurrent.Executor;

class GpioDatastreamsFactory {

    // Hide public constructor to avoid instantiation of this class
    private GpioDatastreamsFactory() {}

    static GpioDatastreamsGetter createGpioDatastreamsGetter(String datastreamId, int pinIndex, GpioService gpioService,
                                                             Executor executor) {
        return new GpioDatastreamsGetter(datastreamId, pinIndex, gpioService, executor);
    }

    static GpioDatastreamsSetter createGpioDatastreamsSetter(String datastreamId, int pinIndex, GpioService gpioService,
                                                             Executor executor) {
        return new GpioDatastreamsSetter(datastreamId, pinIndex, gpioService, executor);
    }

    static GpioDatastreamsEventHandler createGpioDatastreamsEvent(EventPublisher eventPublisher, String datastreamId,
                                                                  int pinIndex, GpioService gpioService) {
        return new GpioDatastreamsEventHandler(eventPublisher, datastreamId, pinIndex, gpioService);
    }
}
