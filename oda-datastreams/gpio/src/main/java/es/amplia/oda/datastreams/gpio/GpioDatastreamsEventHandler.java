package es.amplia.oda.datastreams.gpio;

import es.amplia.oda.core.commons.gpio.GpioDeviceException;
import es.amplia.oda.core.commons.gpio.GpioPin;
import es.amplia.oda.core.commons.gpio.GpioService;
import es.amplia.oda.core.commons.interfaces.DatastreamsEventHandler;
import es.amplia.oda.core.commons.interfaces.EventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GpioDatastreamsEventHandler extends DatastreamsEventHandler implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GpioDatastreamsEventHandler.class);


    private final String datastreamId;
    private final int pinIndex;
    private final GpioService gpioService;

    private GpioPin pin;


    GpioDatastreamsEventHandler(EventPublisher eventPublisher, String datastreamId, int pinIndex, GpioService gpioService) {
        super(eventPublisher);
        this.datastreamId = datastreamId;
        this.pinIndex = pinIndex;
        this.gpioService = gpioService;
    }

    @Override
    public void registerToEventSource() {
        try {
            pin = gpioService.getPinByIndex(pinIndex);

            if (!pin.isOpen()) {
                pin.open();
            }

            pin.addGpioPinListener(this::publishValue);
        } catch (GpioDeviceException gpioDeviceException) {
            LOGGER.warn("Error initializing datastreams {}: Exception adding listener to GPIO pin {}. {}", datastreamId,
                    pinIndex, gpioDeviceException);
        }
    }

    void publishValue(boolean value) {
        publish("", datastreamId, null, System.currentTimeMillis(), value);
    }

    @Override
    public void unregisterFromEventSource() {
        try {
            pin.removeGpioPinListener();
        } catch (GpioDeviceException gpioDeviceException) {
            LOGGER.warn("Error releasing datastreams {}: Exception removing listener from GPIO pin {}. {}",
                    datastreamId, pinIndex, gpioDeviceException.getMessage());
        } catch (Exception exception) {
            LOGGER.warn("Error releasing datastreams event {}: GPIO pin is not available", datastreamId);
        }
    }

    @Override
    public void close() {
        unregisterFromEventSource();
    }
}