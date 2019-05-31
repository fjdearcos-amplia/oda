package es.amplia.oda.subsystem.poller;

import es.amplia.oda.core.commons.interfaces.AbstractDatastreamsEventHandler;
import es.amplia.oda.core.commons.interfaces.EventPublisher;

class PollerDatastreamsEventHandler extends AbstractDatastreamsEventHandler {

    PollerDatastreamsEventHandler(EventPublisher eventPublisher) {
        super(eventPublisher);
    }

    @Override
    public void registerToEventSource() {
        // Nothing to do: There is no real event source
    }

    @Override
    public void unregisterFromEventSource() {
        // Nothing to do: There is no real event source
    }
}
