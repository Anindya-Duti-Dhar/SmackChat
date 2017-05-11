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
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;

import java.io.IOException;
import java.util.ArrayList;


public class MyXMPP {

    String  TAG = "MyXMPP";
    private static final String HOST = "123.200.14.11";
    private static final String SERVICE_NAME = "webhawksit";
    private static final int PORT = 5222;
    private String userName ="";
    private String passWord = "";
    AbstractXMPPConnection  connection ;
    ChatManager chatmanager ;
    MessageListener mMessageListener;
    Chat newChat;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    XMPPConnectionListener2 connectionListener2 = new XMPPConnectionListener2();
    private boolean connected;
    private boolean isToasted;
    private boolean chat_created;
    private boolean loggedin;

    MultiUserChat multiUserChat;
    MultiUserChatManager manager;
    private Context mContext;

    StanzaListener mStanzaListener;
    StanzaFilter filter;
    String mRoomName;
    String mServiceName;
    String mOwnerNick;
    String mRoomDescription;
    String mRoomNameGetFromServer;
    RoomInfo mRoomInfo;
    Message msg;
    String mRoomDescriptionFromServer;

    ArrayList<ChatItem> chatItem;

    public MyXMPP(Context context) {
        mContext=context;
    }

    public AbstractXMPPConnection  getXMPPTCPConnectionObject()    {
        return connection;
    }

    //Initialize
    public void initForLogin(String userId, String pwd ) {
        this.userName = userId;
        this.passWord = pwd;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource("Android");
        configBuilder.setServiceName(SERVICE_NAME);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener);
        Log.d("xmpp: ", "Initializing!");
    }

    //Initialize
    public void initForRegistration(String userId, String pwd ) {
        this.userName = userId;
        this.passWord = pwd;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource("Android");
        configBuilder.setServiceName(SERVICE_NAME);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener2);
        Log.d("xmpp: ", "Initializing!");
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
                    connected = true;
                    Log.d("xmpp: ", "Connection Success");
                } catch (IOException e) {
                    Log.d("xmpp: ", "Connection Error: "+e.getMessage());
                } catch (SmackException e) {
                    Log.d("xmpp: ", "Connection Error: "+e.getMessage());
                } catch (XMPPException e) {
                    Log.d("xmpp: ", "Connection Error: "+e.getMessage());
                                    }
                return null;
            }
        };
        connectionThread.execute();
    }

    // registration
    public void registration(){
        // create the account:
        AccountManager accountManager = AccountManager.getInstance(connection);
        try {
            accountManager.createAccount(userName, passWord);
            Log.d("xmpp: ", "Registration Success");
            // call login method
            login();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure No Response: "+e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XMPP error: "+e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure not connected: "+e.getMessage());
        }
    }

    // Login function
    public void login() {
        try {
            connection.login(userName, passWord);
            //Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");
            Log.d("xmpp: ", "Login Success");
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            sendBroadCast("connection");
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Login Failure: "+e.getMessage());
        } catch (Exception e) {
            Log.d("xmpp: ", "Login Failure: "+e.getMessage());
        }

    }

    // join chat room function
    public  void joinChatRoom(String userName){
        mRoomName = "livelive";
        mServiceName = connection.getServiceName();
        Log.d("xmpp: ", "Service Name: "+mServiceName);
        manager = MultiUserChatManager.getInstanceFor(connection);
        multiUserChat = manager.getMultiUserChat(mRoomName+"@conference.webhawksit");
        try {
            multiUserChat.join(userName);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Chat room join Error: "+e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Chat room join Error: "+e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Chat room join Error: "+e.getMessage());
        }

        // if user joined successfully
        if(multiUserChat.isJoined()) {
            Log.d("xmpp: ", "user has Joined in the chat room");
            // add listener for receiving messages
            receiveMessages();
            sendBroadCast("join");
            try {
                //room info
                mRoomInfo = manager.getRoomInfo(mRoomName+"@conference.webhawksit");
                mOwnerNick =  multiUserChat.getOwners().get(0).getNick();
                mRoomNameGetFromServer = mRoomInfo.getName();
                mRoomDescriptionFromServer = mRoomInfo.getDescription();

                Log.d("xmpp: ", "Room Name: "+mRoomNameGetFromServer+" Room Description: "+mRoomDescriptionFromServer+" Room Owner Nick: "+mOwnerNick);
                // room list
                /*for (int i = 0; i<manager.getHostedRooms("conference.webhawksit").size(); i++){
                    Log.d("xmpp: ", "Room List Id: "+i+"\nRoom Name: "+manager.getHostedRooms("conference.webhawksit").get(i).getName()+"\nRoom JID: "+manager.getHostedRooms("conference.webhawksit").get(i).getJid());
                }*/

            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
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
                        String OnlyUserName = from.replace(mRoomName+"@conference.webhawksit/",""); //remove room name
                        Log.d("xmpp: ", "Original sender: "+from);
                        String body = message.getBody();
                        String subject = message.getSubject();
                        Log.d("xmpp: ", "From: "+OnlyUserName+"\nSubject: "+subject+"\nMessage: "+body);
                        EventBus.getDefault().postSticky(new ChatEvent(OnlyUserName, body, subject));
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
            Log.d("xmpp: ", "Leave Chat Room!");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Leave Chat Room Error: "+e.getMessage());
        }
    }

    // send chat to the room
    public void sendChat(String chat, String subject){
        try {
            Message newMessage = new Message();
            newMessage.setBody(chat);
            newMessage.setSubject(subject);
            multiUserChat.sendMessage(newMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: "+e.getMessage());
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

            Log.d("xmpp: ", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
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
            Log.d("xmpp: ", "ConnectionCLosed!");
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
            Log.d("xmpp: ", "ConnectionClosedOn Error!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d("xmpp: ", "Reconnectingin " + arg0);
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
            Log.d("xmpp: ", "ReconnectionFailed!");
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
            Log.d("xmpp: ", "ReconnectionSuccessful");
            connected = true;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp: ", "Authenticated!");
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

    //Connection Listener to check connection state
    public class XMPPConnectionListener2 implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d("xmpp: ", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                registration();
            }
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
            Log.d("xmpp: ", "ConnectionCLosed!");
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
            Log.d("xmpp: ", "ConnectionClosedOn Error!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d("xmpp: ", "Reconnectingin " + arg0);
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
            Log.d("xmpp: ", "ReconnectionFailed!");
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
            Log.d("xmpp: ", "ReconnectionSuccessful");
            connected = true;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp: ", "Authenticated!");
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

    // create chat room
/*    public void createChatRoom() {
        if (connection.isConnected()== true) {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat("pushpita@conference.webhawksit");
            // Create the room
            try {
                muc.create(userName);
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: "+e.getMessage());
            } catch (SmackException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: "+e.getMessage());
            }
            // Send an empty room configuration form which indicates that we want
            // an instant room
            try {
                muc.sendConfigurationForm(new Form(DataForm.Type.submit));
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            }
        }
    }*/

    // destroy chat room
/*    public void destroyChatRoom(){
            // destroy room
        try {
            multiUserChat.destroy("live Streaming stopped", "demo@conference.webhawksit");
            Log.d("xmpp: ", "Destroy Chat Chat Room!");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room Error: "+e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room Error: "+e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room Error: "+e.getMessage());
        }
    }*/

    // create persistent room
   /* public void createPersistentRoom(String userName){
        if (connection.isConnected()== true) {
            mRoomName = "hello2";
            mRoomDescription = "Live Streaming";
            mServiceName = connection.getServiceName();
            Log.d("xmpp: ", "Service Name: "+mServiceName);
            manager = MultiUserChatManager.getInstanceFor(connection);
            multiUserChat = manager.getMultiUserChat(mRoomName+"@conference.webhawksit");
            // Create the room
            try {
                multiUserChat.create(userName);
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: "+e.getMessage());
            } catch (SmackException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: "+e.getMessage());
            }

            // send configuration for persistent room
            Form form = null;
            try {
                form = multiUserChat.getConfigurationForm();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            }
            Form answerForm = form.createAnswerForm();
            answerForm.setAnswer("muc#roomconfig_publicroom", true);
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
            answerForm.setAnswer("muc#roomconfig_roomdesc", mRoomDescription);
            try {
                multiUserChat.sendConfigurationForm(answerForm);
                // Send room configuration form which indicates that we want
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
            }
        }
    }*/

}
