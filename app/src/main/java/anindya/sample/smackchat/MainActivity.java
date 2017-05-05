package anindya.sample.smackchat;

import android.accounts.Account;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text_view_message);
        Button button = (Button) findViewById(R.id.button_send_test);
        Button button1 = (Button) findViewById(R.id.button_create_room);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creataeRoom();
            }
        });
    }

    private void creataeRoom() {
    }


    public void connect() {
        AsyncTask<Void, Void, Void> connectionThread = new AsyncTask<Void, Void, Void>() {

            public String USERNAME = "anindya";
            public String PASSWORD = "anindya2012";

            public String DOMAIN = "123.200.14.11";
            public int PORT = 5222;

            @Override
            protected Void doInBackground(Void... arg0) {
                XMPPTCPConnection mConnection;
                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                config.setUsernameAndPassword(USERNAME + "@" + DOMAIN, PASSWORD);
                config.setServiceName(DOMAIN);
                config.setHost(DOMAIN);
                config.setPort(PORT);
                config.setDebuggerEnabled(true);

                mConnection = new XMPPTCPConnection(config.build());
                mConnection.setPacketReplyTimeout(10000);
                mConnection.addConnectionListener(new XMPPConnectionListener());

                try {
                    mConnection.connect();
                    mConnection.login();
                } catch (SmackException | IOException | XMPPException e) {
                    Log.d("AsyncTask", e.toString());
                }

                Presence presence = new Presence(Presence.Type.available);
                try {
                    mConnection.sendPacket(presence);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

                final ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                chatManager.addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean b) {
                        chat.addMessageListener(new ChatMessageListener() {
                            @Override
                            public void processMessage(Chat chat, Message message) {
                                Log.d("AsyncTask", message.toString());

                                final String msg = message.toString();

                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String text = textView.getText().toString();
                                        textView.setText(text + "\n" + msg);
                                    }
                                });
                            }
                        });
                    }
                });

                Chat chat2 = chatManager.createChat("ala_monsur" + "@" + DOMAIN);
                try {
                    chat2.sendMessage("This message is from Android");
                } catch (SmackException.NotConnectedException e) {
                    Log.d("AsyncTask", e.toString());
                }

                return null;
            }

        };
        connectionThread.execute();
    }

    private class XMPPConnectionListener implements ConnectionListener {

        @Override
        public void connected(XMPPConnection xmppConnection) {
            Log.i(TAG, "--connected--");
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Log.i(TAG, "--authenticated--");
        }

        @Override
        public void connectionClosed() {
            Log.i(TAG, "--connectionClosed--");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.e(TAG, "--connectionClosedOnError--");
        }

        @Override
        public void reconnectingIn(int i) {
            Log.i(TAG, "--reconnectingIn--");
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i(TAG, "--reconnectionSuccessful--");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.e(TAG, "--reconnectionFailed--");
        }
    }
}