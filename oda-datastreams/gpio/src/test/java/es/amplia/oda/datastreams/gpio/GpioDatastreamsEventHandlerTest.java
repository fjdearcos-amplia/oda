package es.amplia.oda.datastreams.gpio;

import es.amplia.oda.core.commons.gpio.GpioDeviceException;
import es.amplia.oda.core.commons.gpio.GpioPin;
import es.amplia.oda.core.commons.gpio.GpioPinListener;
import es.amplia.oda.core.commons.gpio.GpioService;
import es.amplia.oda.core.commons.interfaces.EventPublisher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GpioDatastreamsEventHandlerTest {

    private static final String TEST_DATASTREAM_ID = "testDatastream";
    private static final int TEST_PIN_INDEX = 1;

    private static final String PIN_FIELD_NAME = "pin";


    @Mock
    private GpioService mockedGpioService;
    @Mock
    private EventPublisher mockedEventPublisher;

    private GpioDatastreamsEventHandler testGpioDatastreamsEventHandler;

    @Mock
    private GpioPin mockedPin;


    @Before
    public void setUp() {
        testGpioDatastreamsEventHandler = new GpioDatastreamsEventHandler(mockedEventPublisher, TEST_DATASTREAM_ID,
                TEST_PIN_INDEX, mockedGpioService);
    }

    @Test
    public void testRegisterToEventSource() {
        when(mockedGpioService.getPinByIndex(anyInt())).thenReturn(mockedPin);
        when(mockedPin.isOpen()).thenReturn(false);

        testGpioDatastreamsEventHandler.registerToEventSource();

        verify(mockedGpioService).getPinByIndex(eq(TEST_PIN_INDEX));
        verify(mockedPin).isOpen();
        verify(mockedPin).open();
        verify(mockedPin).addGpioPinListener(any(GpioPinListener.class));
        assertEquals(mockedPin, Whitebox.getInternalState(testGpioDatastreamsEventHandler, PIN_FIELD_NAME));
    }

    @Test
    public void testRegisterToEventSourceAlreadyOpenPin()  {
        when(mockedGpioService.getPinByIndex(anyInt())).thenReturn(mockedPin);
        when(mockedPin.isOpen()).thenReturn(true);

        testGpioDatastreamsEventHandler.registerToEventSource();

        verify(mockedGpioService).getPinByIndex(eq(TEST_PIN_INDEX));
        verify(mockedPin).isOpen();
        verify(mockedPin, never()).open();
        verify(mockedPin).addGpioPinListener(any(GpioPinListener.class));
        assertEquals(mockedPin, Whitebox.getInternalState(testGpioDatastreamsEventHandler, PIN_FIELD_NAME));
    }

    @Test
    public void testRegisterToEventSourceGpioDeviceExceptionCaught() {
        when(mockedGpioService.getPinByIndex(anyInt())).thenReturn(mockedPin);
        when(mockedPin.isOpen()).thenReturn(false);
        doThrow(GpioDeviceException.class).when(mockedPin).addGpioPinListener(any(GpioPinListener.class));

        testGpioDatastreamsEventHandler.registerToEventSource();

        verify(mockedGpioService).getPinByIndex(eq(TEST_PIN_INDEX));
        verify(mockedPin).addGpioPinListener(any(GpioPinListener.class));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testPublishEvent() {
        boolean testValue = true;

        testGpioDatastreamsEventHandler.publishValue(testValue);

        verify(mockedEventPublisher).publishEvent(eq(""), eq(TEST_DATASTREAM_ID), eq(null), anyLong(), eq(testValue));
    }

    @Test
    public void testUnregisterFromEventSource() {
        Whitebox.setInternalState(testGpioDatastreamsEventHandler, PIN_FIELD_NAME, mockedPin);

        testGpioDatastreamsEventHandler.unregisterFromEventSource();

        verify(mockedPin).removeGpioPinListener();
    }

    @Test
    public void testUnregisterFromEventSourceGpioDeviceExceptionCaught() {
        Whitebox.setInternalState(testGpioDatastreamsEventHandler, PIN_FIELD_NAME, mockedPin);

        doThrow(new GpioDeviceException("")).when(mockedPin).removeGpioPinListener();

        testGpioDatastreamsEventHandler.unregisterFromEventSource();

        verify(mockedPin).removeGpioPinListener();
    }

    @Test
    public void testUnregisterFromEventSourceGeneralExceptionCaught() {
        Whitebox.setInternalState(testGpioDatastreamsEventHandler, PIN_FIELD_NAME, mockedPin);

        doThrow(new RuntimeException()).when(mockedPin).removeGpioPinListener();

        testGpioDatastreamsEventHandler.unregisterFromEventSource();

        verify(mockedPin).removeGpioPinListener();
    }

    @Test
    public void testClose() {
        Whitebox.setInternalState(testGpioDatastreamsEventHandler, PIN_FIELD_NAME, mockedPin);

        testGpioDatastreamsEventHandler.close();

        verify(mockedPin).removeGpioPinListener();
    }
}