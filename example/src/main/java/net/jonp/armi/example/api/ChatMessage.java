package net.jonp.armi.example.api;

/**
 * Represents a message sent from one chatter to the rest.
 */
public class ChatMessage
{
    /**
     * Timestamp of message, in format accepted by
     * {@link java.util.Date#Date(long)}.
     */
    public long timestamp;

    /** Name of the chatter that sent this message. */
    public String from;

    /** Content of the message. */
    public String message;
}
