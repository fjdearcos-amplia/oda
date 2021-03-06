package es.amplia.oda.datastreams.deviceinfo.datastreams;

import es.amplia.oda.core.commons.interfaces.DatastreamsGetter;
import es.amplia.oda.datastreams.deviceinfo.DeviceInfoDatastreamsGetter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DiskUsageDatastreamGetter  implements DatastreamsGetter {

	private DeviceInfoDatastreamsGetter deviceInfo;

	public DiskUsageDatastreamGetter(DeviceInfoDatastreamsGetter deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	@Override
	public String getDatastreamIdSatisfied() {
		return DeviceInfoDatastreamsGetter.DISK_USAGE_DATASTREAM_ID;
	}

	@Override
	public List<String> getDevicesIdManaged() {
		return Collections.singletonList("");
	}

	@Override
	public CompletableFuture<CollectedValue> get(String device) {
		try {
			return CompletableFuture.completedFuture(
					new CollectedValue(System.currentTimeMillis(), Optional.of(this.deviceInfo.getDiskUsage()).orElse(0))
			);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}