package es.amplia.oda.core.commons.interfaces;

import lombok.Value;

@Value
public class DatastreamValue {
    public enum Status {
        OK,
        NOT_FOUND,
        PROCESSING_ERROR
    }

    private String deviceId;
    private String datastreamId;
    private long at;
    private Object value;
    private Status status;
    private String error;
}
