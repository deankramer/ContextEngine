package uk.ac.tvu.mdse.contextengine;

/**
 * Example of a callback interface used by IContextDefinition to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
oneway interface IRemoteServiceCallback { 
    void valueChanged(int value);
}
