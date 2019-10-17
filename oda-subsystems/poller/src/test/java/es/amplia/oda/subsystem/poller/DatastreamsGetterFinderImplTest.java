package es.amplia.oda.subsystem.poller;

import es.amplia.oda.core.commons.interfaces.DatastreamsGetter;
import es.amplia.oda.core.commons.utils.DatastreamsGettersFinderImpl;
import es.amplia.oda.core.commons.utils.DevicePattern;
import es.amplia.oda.core.commons.utils.ServiceLocator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static es.amplia.oda.core.commons.utils.DatastreamsGettersFinder.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatastreamsGetterFinderImplTest {

	@Mock
	private ServiceLocator<DatastreamsGetter> mockedLocator;
	@InjectMocks
	private DatastreamsGettersFinderImpl datastreamsGetterFinder;

	@Mock
	private DatastreamsGetter mockedGetter;

	@Test
	public void testGetGettersSatisfying() {
		when(mockedLocator.findAll()).thenReturn(Collections.singletonList(mockedGetter));
		when(mockedGetter.getDatastreamIdSatisfied()).thenReturn("deviceId");
		when(mockedGetter.getDevicesIdManaged()).thenReturn(Collections.singletonList("deviceId"));

		Return result = datastreamsGetterFinder.getGettersSatisfying(DevicePattern.AllDevicePattern, Collections.singleton("deviceId"));

		assertTrue(result.getGetters().size() > 0);
		assertEquals(0, result.getNotFoundIds().size());
	}

	@Test
	public void testGetGettersSatisfyingWithException() {
		when(mockedLocator.findAll()).thenReturn(Collections.singletonList(mockedGetter));
		when(mockedGetter.getDatastreamIdSatisfied()).thenThrow(new NullPointerException());

		Return result = datastreamsGetterFinder.getGettersSatisfying(DevicePattern.AllDevicePattern, Collections.singleton("deviceId"));

		assertEquals(0, result.getGetters().size());
		assertTrue(result.getNotFoundIds().size() > 0);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetGettersSatisfyingThrowException() {
		when(mockedLocator.findAll()).thenReturn(Collections.singletonList(mockedGetter));

		datastreamsGetterFinder.getGettersSatisfying(null, Collections.singleton("deviceId"));
	}
}
