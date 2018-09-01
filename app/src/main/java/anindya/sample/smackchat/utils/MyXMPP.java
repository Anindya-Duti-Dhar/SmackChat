package anindya.sample.smackchat.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import anindya.sample.smackchat.activity.Chat;
import anindya.sample.smackchat.activity.SplashActivity;
import anindya.sample.smackchat.model.ChatEvent;
import anindya.sample.smackchat.model.ChatItem;


import static anindya.sample.smackchat.utils.Const.ALTERNATE_CHAT_ROOM_REFERENCE;
import static anindya.sample.smackchat.utils.Const.CHAT_DEMO_OPPONENT_NAME;
import static anindya.sample.smackchat.utils.Const.CHAT_ROOM_SERVICE_NAME;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_ADDRESS;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_PORT;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_RESOURCE_NAME;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_SERVICE_NAME;
import static org.jivesoftware.smack.roster.packet.RosterPacket.ItemType.from;
import static org.jivesoftware.smack.roster.packet.RosterPacket.ItemType.to;

/**
 * Created by user on 7/20/2017.
 */

public class MyXMPP {

    private String userName;
    private String passWord;
    AbstractXMPPConnection connection;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    XMPPConnectionListener2 connectionListener2 = new XMPPConnectionListener2();
    private boolean connected;
    private boolean isToasted;
    private boolean chat_created;
    private boolean loggedin;

    MultiUserChat multiUserChat;
    MultiUserChatManager manager;
    private Context mContext;

    ChatManager chatManager;
    org.jivesoftware.smack.chat.Chat mChat;

    StanzaListener mStanzaListener;
    StanzaListener mStanzaListener2;
    StanzaFilter filter;
    StanzaFilter filter2;
    String mServiceName;


    public MyXMPP(Context context) {
        mContext = context;
    }

    //Initialize
    public void initForLogin(String userId, String pwd) {
        userName = userId;
        passWord = pwd;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource(CHAT_SERVER_RESOURCE_NAME);
        configBuilder.setServiceName(CHAT_SERVER_SERVICE_NAME);
        configBuilder.setHost(CHAT_SERVER_ADDRESS);
        configBuilder.setPort(CHAT_SERVER_PORT);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener);
        Log.d("xmpp: ", "Initializing For Login!");
    }

    //Initialize
    public void initForRegistration(String userId, String pwd) {
        userName = userId;
        passWord = pwd;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource(CHAT_SERVER_RESOURCE_NAME);
        configBuilder.setServiceName(CHAT_SERVER_SERVICE_NAME);
        configBuilder.setHost(CHAT_SERVER_ADDRESS);
        configBuilder.setPort(CHAT_SERVER_PORT);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener2);
        Log.d("xmpp: ", "Initializing For Registration!");
    }

    // Disconnect Function
    public void disconnectConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(connection.isConnected()){
                    connection.disconnect();
                }
            }
        }).start();
    }

    // Connection function
    public void connectConnection() {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {
                // Create a connection
                try {
                    connection.connect();
                    connected = true;
                    Log.d("xmpp: ", "Connection Success");
                } catch (IOException e) {
                    Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                    sendBroadCast("connectionerror", e.getMessage());
                } catch (SmackException e) {
                    Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                    sendBroadCast("connectionerror", e.getMessage());
                } catch (XMPPException e) {
                    Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                    sendBroadCast("connectionerror", e.getMessage());
                }
                return null;
            }
        };
        connectionThread.execute();
    }

    // registration
    public void registration() {
        // create the account:
        AccountManager accountManager = AccountManager.getInstance(connection);
        try {
            accountManager.createAccount(userName, passWord);
            Log.d("xmpp: ", "Registration Success");
            // call login method
            login();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure No Response: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XMPP error: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
            //XMPPError: conflict - cancel
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure not connected: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
        }
    }

    // Login function
    public void login() {
        try {
            connection.login(userName, passWord);
            Log.d("xmpp: ", "Login Success");
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);

            // set extra information
            setMyExtraInfo();

            sendBroadCast("signin", "done");

            // send ping
            sendPing();

            // Roster entry
            Roster roster = Roster.getInstanceFor(connection);
            try {
                roster.createEntry(userName + "@" + CHAT_SERVER_ADDRESS, userName, null);
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Login Failure: " + e.getMessage());
            sendBroadCast("signinerror", e.getMessage());
        } catch (Exception e) {
            Log.d("xmpp: ", "Login Failure: " + e.getMessage());
            sendBroadCast("signinerror", e.getMessage());
        }

    }

    // join chat room function
    public void joinChatRoom(String userName, String roomName) {
        try {
            Log.d("xmpp: ", "Service Name: " + connection.getServiceName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("xmpp: ", "service name error: "+e.getMessage());
        }
        manager = MultiUserChatManager.getInstanceFor(connection);
        multiUserChat = manager.getMultiUserChat(roomName+ "@" +CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
        try {
            multiUserChat.join(userName);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Chat room join Error: " + e.getMessage());
            sendBroadCast("joinerror", e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Chat room join Error: " + e.getMessage());
            sendBroadCast("joinerror", e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Chat room join Error: " + e.getMessage());
            sendBroadCast("joinerror", e.getMessage());
        }

        // if user joined successfully
        if (multiUserChat.isJoined()) {
            Log.d("xmpp: ", "user has Joined in the chat room");
            sendBroadCast("join", "done");
            //call method to configure room
            configRoom(roomName);

            // get Roster
            getBuddies();
            //get user info
            getUserInfo(userName);

        }
    }


    // set current user additional information
    public void setMyExtraInfo(){
        VCard vcard = new VCard();
        vcard.setFirstName(userName);
        vcard.setLastName("Sci");
        vcard.setEmailHome(userName+"@gmail.com");
        vcard.setMiddleName("Developer");
        vcard.setNickName("SCIBD");
        vcard.setPhoneHome("Voice", "12783849404");
        vcard.setOrganization("Save the Children");
        //vcard.setAvatar("" + image_path); //Image Path should be URL or Can be Byte Array etc.
        try {
            vcard.save(connection);
            // send success broadcast
            //sendBroadCast("connection");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    //get specific info
    public void getUserInfo(String userName){
        VCard card = new VCard();
        try {
            card.load(connection, userName+"@"+CHAT_SERVER_SERVICE_NAME);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        Log.d("xmpp: ", "Friend's Nick Name: "+card.getNickName()+"\nFriend's Email: "+card.getEmailHome());
    }


    // get user list
    public void getBuddies(){
        try  {
            UserSearchManager manager = new UserSearchManager(connection);
            String searchFormString = "search." + connection.getServiceName();
            Log.d("***", "SearchForm: " + searchFormString);
            Form searchForm = null;

            searchForm = manager.getSearchForm(searchFormString);

            Form answerForm = searchForm.createAnswerForm();

            UserSearch userSearch = new UserSearch();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", "*");

            ReportedData results = userSearch.sendSearchForm(connection, answerForm, searchFormString);
            if (results != null) {
                List<ReportedData.Row> rows = results.getRows();
                for (ReportedData.Row row : rows) {
                    Log.d("***", "xmpp:: row: " + row.getValues("Username").toString());  //***: xmpp:: row: [username]
                }
            } else {
                Log.d("***", "No result found");
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    // method for ping manager
    public void sendPing(){
        PingManager pm =  PingManager.getInstanceFor(connection) ;
        pm.setPingInterval(5) ;  // 5 sec
        try {
            pm.pingMyServer() ;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        pm.registerPingFailedListener(new PingFailedListener() {
            @Override
            public void pingFailed() {
                Log.e("xmpp: " , "Ping Failed") ;
            }
        });
    }

    // get room status
    public void getRoomStatus(String roomName){
        manager = MultiUserChatManager.getInstanceFor(connection);
        // Discover information about the room
        try {
            manager.getRoomInfo(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
            sendBroadCast("roominfo", "ok");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
            sendBroadCast("roominfo", "yes"); //XMPPError: item-not-found - cancel
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
            sendBroadCast("roominfo", e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
            sendBroadCast("roominfo", e.getMessage());
        }
    }

    // configure room for getting messages
    public void configRoom(String roomName){
        Log.d("xmpp: ", "ready to receive messages in the chat room");
        // add listener for receiving messages
        receiveGroupMessages(roomName);
        receiveStanza();
        try {
            //room info
            RoomInfo roomInfo = manager.getRoomInfo(roomName + "@" + CHAT_ROOM_SERVICE_NAME+ CHAT_SERVER_SERVICE_NAME);
            String ownerNick = multiUserChat.getOwners().get(0).getNick();
            String roomNameGetFromServer = roomInfo.getName();
            String roomDescriptionFromServer = roomInfo.getDescription();

            Log.d("xmpp: ", "Room Name: " + roomNameGetFromServer + " Room Description: " + roomInfo.getDescription() + " Room Owner Nick: " + ownerNick);
            // room list
            int roomListSize = manager.getHostedRooms(CHAT_ROOM_SERVICE_NAME+ CHAT_SERVER_SERVICE_NAME).size();
            if(roomListSize>0){
                for (int i = 0; i<roomListSize; i++){
                    Log.d("xmpp: ", "Room List Id: "+i+"\nRoom Name: "+manager.getHostedRooms(CHAT_ROOM_SERVICE_NAME+ CHAT_SERVER_SERVICE_NAME).get(i).getName()+"\nRoom JID: "+manager.getHostedRooms(CHAT_ROOM_SERVICE_NAME+ CHAT_SERVER_SERVICE_NAME).get(i).getJid());
                }
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Room Configuration Error: "+e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Room Configuration Error: "+e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Room Configuration Error: "+e.getMessage());
        }
    }

    // check current user status
    public void UserStatus(String userName){
        Roster roster = Roster.getInstanceFor(connection);
        Presence presence = roster.getPresence(userName + "@"+ CHAT_SERVER_SERVICE_NAME);
        if (presence.getType() == Presence.Type.available) {
            // User is online...
            Log.d("xmpp: ", "user Online");
            sendBroadCast("userstatus", "online");
        } else {
            // User is Offline...
            Log.d("xmpp: ", "user Offline");
            sendBroadCast("userstatus", "offline");
        }
    }

    // send message using multiUserChat to the room
    public void sendGroupChat(String chat, String subject) {
        try {
            Message newMessage = new Message();
            newMessage.setBody(chat);
            newMessage.setSubject(subject);
            multiUserChat.sendMessage(newMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: " + e.getMessage());
        }
    }

    // send message using Stanza of Smack
    public void sendStanza(String chat, String subject){
        Message message = new Message(CHAT_DEMO_OPPONENT_NAME+ "@"+ CHAT_SERVER_SERVICE_NAME, Message.Type.chat);
        message.setFrom(connection.getUser());
        message.setBody(chat);
        message.setSubject(subject);
        try {
            connection.sendStanza(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    // send message using Chat object of Smack
    public void sendChat(String chat, String subject){
        Message message = new Message("duti"+ "@"+ CHAT_SERVER_SERVICE_NAME, Message.Type.chat);
        message.setFrom(connection.getUser());
        message.setBody(chat);
        message.setSubject(subject);
        try {
            mChat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    // received Messages using multiUserChat from room
    public void receiveGroupMessages(final String roomName) {
        if (connected) {
            //ArrayList<ChatItem> chatItem = new ArrayList<ChatItem>();
            filter = MessageTypeFilter.GROUPCHAT;
            mStanzaListener = new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String from = message.getFrom();
                        String OnlyUserName = from.replace(roomName + "@"+ CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME+"\u002F", ""); //remove room name // (here \u002F is for forward slash)
                        Log.d("xmpp: ", "Original sender: " + from);
                        String body = message.getBody();
                        String messageID = message.getStanzaId();
                        String subject = message.getSubject();
                        Log.d("xmpp: ", "From: " + OnlyUserName + "\nSubject: " + subject + "\nMessage: " + body +"\nMessage ID: "+messageID);
                        EventBus.getDefault().postSticky(new ChatEvent(OnlyUserName, body, subject, messageID));
                    }
                }
            };
            connection.addSyncStanzaListener(mStanzaListener, filter);   // remove addAsyncStanzaListener to avoid duplicate messages
        }
    }

    // received Messages from individual user by Stanza
    public void receiveStanza(){
        if (connected) {
            filter2 = MessageTypeFilter.CHAT;
            mStanzaListener2 = new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String from = message.getFrom();
                        String body = message.getBody();
                        String messageID = message.getStanzaId();
                        String subject = message.getSubject();
                        Log.d("xmpp: ", "From 2: " + from + "\nSubject 2: " + subject + "\nMessage 2: " + body +"\nMessage ID 2: "+messageID);
                        if(NotificationUtils.isAppIsInBackground(mContext)){
                            Intent resultIntent = new Intent(mContext, SplashActivity.class);
                            resultIntent.putExtra("message", body);
                            NotificationUtils notificationUtils = new NotificationUtils(mContext);
                            notificationUtils.showNotificationMessage(subject, body, "", resultIntent);
                        }
                        //EventBus.getDefault().postSticky(new ChatEvent(OnlyUserName, body, subject, messageID));
                    }
                }
            };
            connection.addSyncStanzaListener(mStanzaListener2, filter2);   // remove addAsyncStanzaListener to avoid duplicate messages
        }
    }

    // received Messages from individual user by ChatManager
    public void receiveMessage(){
        if(connection.isAuthenticated())
        {
            Log.w("app", "Auth done");
            chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(org.jivesoftware.smack.chat.Chat chat, boolean createdLocally) {
                    mChat = chat;
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                            String from = message.getFrom();
                            String body = message.getBody();
                            String subject = message.getSubject();
                            String messageID = message.getStanzaId();
                            System.out.println("Received message: "
                                    + (message != null ? message.getBody() : "NULL"));
                            Log.d("xmpp: ", "From 3: " + from + "\nSubject 3: " + subject + "\nMessage 3: " + body +"\nMessage ID 3: "+messageID);

                        }
                    });
                    Log.w("app", chat.toString());
                }
            });
        }
    }

    // create persistent room
    public void createPersistentRoom(String userName, String roomName){
        if (connection.isConnected()== true) {
            String roomDescription = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()); // current time
            Log.d("xmpp: ", "Service Name: "+connection.getServiceName());
            manager = MultiUserChatManager.getInstanceFor(connection);
            multiUserChat = manager.getMultiUserChat(roomName+"@"+ CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
            // Create the room
            try {
                multiUserChat.create(userName);
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }

            // send configuration for persistent room
            Form form = null;
            try {
                form = multiUserChat.getConfigurationForm();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
            Form answerForm = form.createAnswerForm();
            answerForm.setAnswer("muc#roomconfig_publicroom", true);
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
            answerForm.setAnswer("muc#roomconfig_roomdesc", roomDescription);
            try {
                // Send room configuration form which indicates that we want
                multiUserChat.sendConfigurationForm(answerForm);
                sendBroadCast("create", "done");
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
        }
    }

    // destroy chat room
    // with broadcast
    public void destroyChatRoom(){
        try {
            multiUserChat.destroy("not interested in conversation", ALTERNATE_CHAT_ROOM_REFERENCE +"@"+ CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
            Log.d("xmpp: ", "Destroy Chat Room by button!");
            sendBroadCast("destroy", "done");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room by button Error: "+e.getMessage());
            sendBroadCast("destroyerror", e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room by button Error: "+e.getMessage());
            sendBroadCast("destroyerror", e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room by button Error: "+e.getMessage());
            sendBroadCast("destroyerror", e.getMessage());
        }
    }

    // send broadcast to the other activities
    public void sendBroadCast(String type, String message) {
        String mType = type;
        String intentText = null;
        if (mType.equals("join")) {
            intentText = "join";
        } else if (mType.equals("signin")) {
            intentText = "signin";
        } else if (mType.equals("signuperror")) {
            intentText = "signuperror";
        } else if (mType.equals("signinerror")) {
            intentText = "signinerror";
        }
        else if (mType.equals("connectionerror")){
            intentText = "connectionerror";
        }
        else if (mType.equals("create")){
            intentText = "create";
        }
        else if (mType.equals("roomcreateerror")){
            intentText = "roomcreateerror";
        }
        else if (mType.equals("destroyerror")){
            intentText = "destroyerror";
        }
        else if (mType.equals("destroy")){
            intentText = "destroy";
        }
        else if (mType.equals("connectionclosederror")){
            intentText = "connectionclosederror";
        }
        else if (mType.equals("connectionclosed")){
            intentText = "connectionclosed";
        }
        else if (mType.equals("roominfo")){
            intentText = "roominfo";
        }
        else if (mType.equals("destroyduplicate")){
            intentText = "destroyduplicate";
        }
        else if (mType.equals("destroyduplicateerror")){
            intentText = "destroyduplicateerror";
        }
        else if (mType.equals("userstatus")){
            intentText = "userstatus";
        }
        else if (mType.equals("joinerror")){
            intentText = "joinerror";
        }
        Intent broadCastIntent = new Intent(intentText);
        broadCastIntent.putExtra("action", message);
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
            sendBroadCast("connectionclosed", "done");
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
            // send broad cast when connection closed by error
            sendBroadCast("connectionclosederror", "done");
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

            Log.d("xmpp: ", "Connected2!");
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
            Log.d("xmpp: ", "ConnectionCLosed2!");
            sendBroadCast("connectionclosed", "done");
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
            Log.d("xmpp: ", "ConnectionClosedOn2 Error!");
            // send broad cast when connection closed by error
            sendBroadCast("connectionclosederror", "done");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d("xmpp: ", "Reconnectingin2 " + arg0);
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
            Log.d("xmpp: ", "ReconnectionFailed2!");
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
            Log.d("xmpp: ", "ReconnectionSuccessful2");
            connected = true;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp: ", "Authenticated2!");
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


    // the following three methods are not used by his app but they are important so that we have to keep this in code

    // exit from the room
    public void exitFromRoom() {
        try {
            //remove listener
            connection.removeAsyncStanzaListener(mStanzaListener);
            // leave room
            multiUserChat.leave();
            Log.d("xmpp: ", "Leave Chat Room!");
            sendBroadCast("exit", "done");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Leave Chat Room Error: " + e.getMessage());
            sendBroadCast("exiterror", e.getMessage());
        }
    }

    // create temporary chat room
    public void createChatRoom(String userName, String roomName) {
        if (connection.isConnected()== true) {
            manager = MultiUserChatManager.getInstanceFor(connection);
            multiUserChat = manager.getMultiUserChat(roomName+"@"+ CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
            // Create the room
            try {
                multiUserChat.create(userName);
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
            // Send an empty room configuration form which indicates that we want
            // an instant room
            try {
                multiUserChat.sendConfigurationForm(new Form(DataForm.Type.submit));
                sendBroadCast("create", "done");
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Configuration Error: "+e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
        }
    }

}
