package net.jonp.armi.comm;

/**
 * Objects interested in receiving unsolicited messages from the server should
 * implement this class and register themselves.
 */
public interface UnsolListener
{
    /**
     * An unsolicited message was received by the server.
     * 
     * @param type The message type string.
     * @param value The value received in the message.
     */
    public void unsolReceived(String type, Object value);
}
