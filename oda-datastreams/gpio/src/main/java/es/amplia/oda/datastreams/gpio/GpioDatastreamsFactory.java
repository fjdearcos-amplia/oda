package es.amplia.oda.datastreams.gpio;

import es.amplia.oda.core.commons.gpio.GpioService;
import es.amplia.oda.core.commons.interfaces.EventPublisher;

import java.util.concurrent.Executor;

class GpioDatastreamsFactory {

    private final GpioService gpioService;
    private final EventPublisher eventPublisher;
    private final Executor executor;

    GpioDatastreamsFactory(GpioService gpioService, EventPublisher eventPublisher, Executor executor) {
        this.gpioService = gpioService;
        this.eventPublisher = eventPublisher;
        this.executor = executor;
    }

    GpioDatastreamsGetter createGpioDatastreamsGetter(String datastreamId, int pinIndex) {
        return new GpioDatastreamsGetter(datastreamId, pinIndex, gpioService, executor);
    }

    GpioDatastreamsSetter createGpioDatastreamsSetter(String datastreamId, int pinIndex) {
        return new GpioDatastreamsSetter(datastreamId, pinIndex, gpioService, executor);
    }

    GpioDatastreamsEventHandler createGpioDatastreamsEvent(String datastreamId, int pinIndex) {
        return new GpioDatastreamsEventHandler(eventPublisher, datastreamId, pinIndex, gpioService);
    }
}
