package de.fhg.aisec.ids.idscp2.idscp_core;

/**
 * An interface for an IDSCP message listener
 */
public interface IdscpMessageListener {

    /*
     * notify the listener about new data
     */
    void onMessage(String type, byte[] data);
}
