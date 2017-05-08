package anindya.sample.smackchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.ArrayList;


public class MyXMPP {

    String  TAG = "MyXMPP";
    private static final String HOST = "123.200.14.11";
    private static final int PORT = 5222;
    private String userName ="";
    private String passWord = "";
    AbstractXMPPConnection  connection ;
    ChatManager chatmanager ;
    MessageListener mMessageListener;
    Chat newChat;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    private boolean connected;
    private boolean isToasted;
    private boolean chat_created;
    private boolean loggedin;

    MultiUserChat multiUserChat;
    MultiUserChatManager manager;
    private Context mContext;

    StanzaListener mStanzaListener;
    StanzaFilter filter;

    ArrayList<ChatItem> chatItem;

    public MyXMPP(Context context) {
        mContext=context;
    }

    public AbstractXMPPConnection  getXMPPTCPConnectionObject()    {
        return connection;
    }

    //Initialize
    public void init(String userId, String pwd ) {
        Log.i("XMPP", "Initializing!");
        this.userName = userId;
        this.passWord = pwd;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource("Android");
        configBuilder.setServiceName(HOST);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener);

    }

    // Disconnect Function
    public void disconnectConnection(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    // Connection function
    public void connectConnection()
    {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {


                // Create a connection
                try {
                    connection.connect();
                    login();
                    connected = true;

                } catch (IOException e) {
                } catch (SmackException e) {

                } catch (XMPPException e) {
                                    }
                return null;
            }
        };
        connectionThread.execute();
    }


    // Login function
    public void login() {

        try {
            connection.login(userName, passWord);
            //Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

    }

    // join chat room function
    public  void joinChatRoom(String userName){
        manager = MultiUserChatManager.getInstanceFor(connection);
        multiUserChat = manager.getMultiUserChat("livelive@conference.webhawksit");
        try {
            multiUserChat.join(userName);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        // if user joined successfully
        if(multiUserChat.isJoined()) {
            Log.d("xmpp", "user has Joined");
            sendBroadCast("join");
        }
        // add listener for receiving messages
        receiveMessages();
    }

    // received Messages from room
    public void receiveMessages(){
        if(connected) {
            chatItem = new ArrayList<ChatItem>();
            filter = MessageTypeFilter.GROUPCHAT;
            mStanzaListener = new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String from = message.getFrom();
                        String OnlyUserName = from.replace("livelive@conference.webhawksit/",""); //remove room name
                        Log.d("xmpp who:: ", from);
                        String body = message.getBody();
                        Log.d("xmpp body:: ", body);
                        EventBus.getDefault().postSticky(new ChatEvent(OnlyUserName, body));
                    }
                    sendBroadCast("newChat");
                }
            };
            connection.addAsyncStanzaListener(mStanzaListener, filter);
        }
    }

    // exit from the room
    public void exitFromRoom(){
        try {
            //remove listener
            connection.removeAsyncStanzaListener(mStanzaListener);
            // leave room
            multiUserChat.leave();
            Log.d("xmpp", "Leave Chat Room!");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    // send chat to the room
    public void sendChat(String chat){
        try {
            multiUserChat.sendMessage(chat);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    // send broadcast to the other activities
    public void  sendBroadCast(String type){
        String mType = type;
        String intentText = null;
        if(mType.equals("join")){
            intentText = "join";
        }
        else if (mType.equals("connection")){
            intentText = "connection";
        }
        Intent broadCastIntent = new Intent(intentText);
        broadCastIntent.putExtra("action", "Done");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadCastIntent);
    }

    //Connection Listener to check connection state
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d("xmpp", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }

           sendBroadCast("connection");
        }

        @Override
        public void connectionClosed() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                    }
                });
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d("xmpp", "Reconnectingin " + arg0);
            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;
            chat_created = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
        }
    }

/*    public void createChatRoom() {
        if (connection.isConnected()== true) {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat("pushpita@conference.webhawksit");
            // Create the room
            try {
                muc.create(userName);
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            }
            // Send an empty room configuration form which indicates that we want
            // an instant room
            try {
                muc.sendConfigurationForm(new Form(DataForm.Type.submit));
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }*/

}
