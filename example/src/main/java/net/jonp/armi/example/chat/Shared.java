package net.jonp.armi.example.chat;

/**
 * Shared info between client and server.
 */
public class Shared
{
    /** Default port for communications. */
    public static final int PORT = 43982;

    /** The type field for unsolicited chat messages. */
    public static final String UNSOL_CHAT = "chat.chatmessage";

    /**
     * The type field for unsolicited messages indicating that a new chatter
     * joined.
     */
    public static final String UNSOL_ADDCHATTER = "chat.addchatter";

    /** A chatter left. */
    public static final String UNSOL_DELCHATTER = "chat.delchatter";

    /** A chatter changed his name. */
    public static final String UNSOL_NAMECHANGE = "chat.namechange";
}
