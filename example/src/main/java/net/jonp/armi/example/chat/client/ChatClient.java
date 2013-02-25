package net.jonp.armi.example.chat.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.NotBoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.jonp.armi.comm.DefaultClassRegistry;
import net.jonp.armi.comm.ResponseDispatcher;
import net.jonp.armi.comm.UnsolListener;
import net.jonp.armi.comm.client.ClientSideSocketCommunicator;
import net.jonp.armi.example.api.ChatMessage;
import net.jonp.armi.example.api.Chatter;
import net.jonp.armi.example.api.NameChange;
import net.jonp.armi.example.chat.Shared;

import org.apache.log4j.Logger;

/**
 * Connects to a chat server and allows communication.
 */
public class ChatClient
    extends JFrame
{
    static final Logger LOG = Logger.getLogger(ChatClient.class);

    final ClientSideSocketCommunicator _comm;
    private final ResponseDispatcher _dispatcher;
    final ClientAPI _api;

    public ChatClient(final InetAddress serverAddr, final int port)
        throws IOException
    {
        super("Chat Client");

        final Socket sock = new Socket(serverAddr, port);
        sock.setSoTimeout(3000);

        final DefaultClassRegistry classRegistry = new DefaultClassRegistry();
        classRegistry.put(ChatMessage.class.getSimpleName(), ChatMessage.class);
        classRegistry.put(NameChange.class.getSimpleName(), NameChange.class);
        classRegistry.put(Chatter.class.getSimpleName(), Chatter.class);

        _comm = new ClientSideSocketCommunicator(sock, classRegistry);

        _dispatcher = new ResponseDispatcher(_comm);
        _api = new ClientAPI(_dispatcher);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                try {
                    _comm.close();
                }
                catch (final IOException ioe) {
                    LOG.warn("Exception closing socket", ioe);
                }

                setVisible(false);
                dispose();
            }
        });

        final JTextArea chatHistory = new JTextArea(25, 80);
        final JTextArea message = new JTextArea(4, 80);
        final JButton send = new JButton("Send");
        final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatHistory, message);

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);

        final JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        add(sendPanel, BorderLayout.SOUTH);

        sendPanel.add(send);

        final UnsolListener listener = new UnsolListener() {
            @Override
            public void unsolReceived(final String type, final Object value)
            {
                processUnsol(type, value, chatHistory);
            }
        };

        _dispatcher.addUnsolListener("chat\\..*", listener);
        _dispatcher.addSuppressedException(SocketTimeoutException.class);
        _dispatcher.start();

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                sendMessage(message.getText());
                message.setText("");
            }
        });

        message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e)
            {
                if (!e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    sendMessage(message.getText());
                    message.setText("");
                }
            }
        });

        pack();

        message.requestFocusInWindow();
    }

    void sendMessage(final String text)
    {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Must be called on the event dispatch thread");
        }

        final String msg = text.trim();
        if (!msg.isEmpty()) {
            String err = null;
            try {
                if (msg.startsWith("\\rename")) {
                    final String newname = msg.substring(msg.indexOf(' ') + 1).trim();
                    _api.setName(newname);
                }
                else if (msg.startsWith("\\quit")) {
                    try {
                        _comm.close();
                    }
                    catch (final IOException ioe) {
                        LOG.warn("Exception closing socket", ioe);
                    }

                    setVisible(false);
                    dispose();
                }
                else {
                    _api.sendMessage(msg);
                }
            }
            catch (final IOException ioe) {
                err = "Error communicating with server: " + ioe.getMessage();
            }
            catch (final NotBoundException nbe) {
                err = "Client was improperly initialized: " + nbe.getMessage();
            }

            if (null != err) {
                JOptionPane.showMessageDialog(ChatClient.this, err, "Error Sending Message", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void processUnsol(final String type, final Object value, final JTextArea chatHistory)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                if (Shared.UNSOL_ADDCHATTER.equals(type)) {
                    chatHistory.append(String.format("New chatter: %s\n", value));
                }
                else if (Shared.UNSOL_DELCHATTER.equals(type)) {
                    chatHistory.append(String.format("Chatter left: %s\n", value));
                }
                else if (Shared.UNSOL_NAMECHANGE.equals(type)) {
                    final NameChange nc = (NameChange)value;
                    chatHistory.append(String.format("Chatter '%s' now goes by '%s'\n", nc.oldname, nc.newname));
                }
                else if (Shared.UNSOL_CHAT.equals(type)) {
                    final ChatMessage chat = (ChatMessage)value;
                    chatHistory.append(String.format("%s: %s\n", chat.from, chat.message));
                }
            }
        });
    }
}
