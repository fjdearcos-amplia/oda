package es.amplia.oda.hardware.diozero;

import es.amplia.oda.core.commons.diozero.AdcService;
import es.amplia.oda.core.commons.utils.ConfigurableBundleImpl;
import es.amplia.oda.hardware.diozero.analog.DioZeroAdcService;
import es.amplia.oda.hardware.diozero.configuration.DioZeroConfigurationHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
public class ActivatorTest {

	private Activator testActivator = new Activator();
	@Mock
	DioZeroAdcService mockedService;
	@Mock
	DioZeroConfigurationHandler mockedHandler;
	@Mock
	ConfigurableBundleImpl mockedConfigurableBundle;
	@Mock
	BundleContext mockedContext;
	@Mock
	ServiceRegistration<AdcService> mockedRegistration;
	@Mock
	Bundle mockedBundle;

	@Test
	public void testStart() throws Exception {
		whenNew(DioZeroAdcService.class).withAnyArguments().thenReturn(mockedService);
		whenNew(DioZeroConfigurationHandler.class).withAnyArguments().thenReturn(mockedHandler);
		whenNew(ConfigurableBundleImpl.class).withAnyArguments().thenReturn(mockedConfigurableBundle);
		when(mockedContext.registerService(eq(AdcService.class), any(), any())).thenReturn(mockedRegistration);
		when(mockedContext.getBundle()).thenReturn(mockedBundle);
		when(mockedBundle.getSymbolicName()).thenReturn("");

		testActivator.start(mockedContext);
	}

	@Test
	public void testStop() {
		Whitebox.setInternalState(testActivator,"adcServiceRegistration", mockedRegistration);
		Whitebox.setInternalState(testActivator,"configurableBundle", mockedConfigurableBundle);
		Whitebox.setInternalState(testActivator,"adcService", mockedService);

		testActivator.stop(mockedContext);

		verify(mockedRegistration).unregister();
		verify(mockedConfigurableBundle).close();
		verify(mockedService).release();
	}
}
