package es.amplia.oda.datastreams.deviceinfo.datastreams;

import es.amplia.oda.core.commons.interfaces.DatastreamsGetter;
import es.amplia.oda.datastreams.deviceinfo.DeviceInfoDatastreamsGetter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RamUsageDatastreamGetter implements DatastreamsGetter {

	private DeviceInfoDatastreamsGetter deviceInfo;

	public RamUsageDatastreamGetter(DeviceInfoDatastreamsGetter deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	@Override
	public String getDatastreamIdSatisfied() {
		return DeviceInfoDatastreamsGetter.RAM_USAGE_DATASTREAM_ID;
	}

	@Override
	public List<String> getDevicesIdManaged() {
		return Collections.singletonList("");
	}

	@Override
	public CompletableFuture<CollectedValue> get(String device) {
		try {
			return CompletableFuture.completedFuture(
					new CollectedValue(System.currentTimeMillis(), Optional.of(this.deviceInfo.getRamUsage()).orElse(0))
			);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
