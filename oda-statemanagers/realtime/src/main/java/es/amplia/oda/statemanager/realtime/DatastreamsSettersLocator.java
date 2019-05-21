package es.amplia.oda.statemanager.realtime;

import es.amplia.oda.core.commons.interfaces.DatastreamsSetter;

import java.util.List;

interface DatastreamsSettersLocator extends AutoCloseable {
    /**
     * This function must return all the DatastreamsSetter currently in the system.
     * In the future, maybe we can pass parameters so that the returned list is filtered of not useful elements.
     * But right now the DatastreamsSetters are registered in Osgi without filters, so it can not be done automatically.
     * @return The list of DatastreamsSetters currently in the system.
     */
    List<DatastreamsSetter> getDatastreamsSetters();
}
