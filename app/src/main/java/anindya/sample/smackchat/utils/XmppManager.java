package anindya.sample.smackchat.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.model.ChatItem;
import anindya.sample.smackchat.model.MyFriend;
import anindya.sample.smackchat.model.RoomItem;
import base.droidtool.DroidTool;

import static anindya.sample.smackchat.utils.Const.CHAT_ROOM_SERVICE_NAME;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_ADDRESS;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_PORT;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_SERVICE_NAME;


public class XmppManager {

    public Context mContext;
    public String userName, passWord;
    public AbstractXMPPConnection connection;
    public ChatManager chatManager;
    public XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    public List<MyFriend> friendList = new ArrayList<MyFriend>();
    StanzaListener mStanzaListener;
    StanzaListener mChatStanzaListener;
    StanzaFilter filter;
    StanzaFilter mChatFilter;
    MultiUserChatManager manager;
    MultiUserChat multiUserChat;

    public onRegistrationResponse registrationResponse = null;

    public interface onRegistrationResponse {
        void onRegistered(boolean isRegistered);
    }

    public void setRegistrationResponseListener(onRegistrationResponse listener) {
        registrationResponse = listener;
    }

    public onLoginResponse loginResponse = null;

    public interface onLoginResponse {
        void onLoggedIn(boolean isLogged);
    }

    public void setLoginResponseListener(onLoginResponse listener) {
        loginResponse = listener;
    }

    public onFriendLoadResponse friendLoadResponse = null;

    public interface onFriendLoadResponse {
        void onLoaded(List<MyFriend> friendList);
    }

    public void setFriendLoadResponseListener(onFriendLoadResponse listener) {
        friendLoadResponse = listener;
    }

    public onConnectionResponse connectionResponse = null;

    public interface onConnectionResponse {
        void onConnected(boolean isConnected, XMPPConnection connection);
    }

    public void setConnectionResponseListener(onConnectionResponse listener) {
        connectionResponse = listener;
    }

    public onProfileSetupResponse profileSetupResponse = null;

    public interface onProfileSetupResponse {
        void onProfileSetup(boolean isSetup);
    }

    public void setProfileSetupResponseListener(onProfileSetupResponse listener) {
        profileSetupResponse = listener;
    }

    DroidTool dt;

    public XmppManager(Context context) {
        mContext = context;
        dt = new DroidTool(context);
    }

    // connection Initialize
    public void initConnectionBuilder(String username, String password) {
        userName = username;
        passWord = password;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        HostnameVerifier verifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return false;
            }
        };

        DomainBareJid serviceName = null;
        try {
            serviceName = JidCreate.domainBareFrom(CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "service name error: " + e.getMessage());
        }

        configBuilder.setHost(CHAT_SERVER_ADDRESS);
        configBuilder.setXmppDomain(serviceName);
        configBuilder.setHostnameVerifier(verifier);
        configBuilder.setPort(CHAT_SERVER_PORT);

        InetAddress address = null;
        if (!CHAT_SERVER_ADDRESS.matches((".*[a-zA-Z]+.*"))) {
            try {
                address = InetAddress.getByName(CHAT_SERVER_ADDRESS);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Internet Address error: " + e.getMessage());
            }
            configBuilder.setHostAddress(address);
        } else {
            configBuilder.setHost(CHAT_SERVER_ADDRESS);
        }

        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener);
    }

    // Connection function
    public void connectConnection() {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {
                if (!connection.isConnected()) {
                    try {
                        connection.connect();
                    } catch (SmackException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if (connectionResponse != null) connectionResponse.onConnected(false, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if (connectionResponse != null) connectionResponse.onConnected(false, null);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if (connectionResponse != null) connectionResponse.onConnected(false, null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if (connectionResponse != null) connectionResponse.onConnected(false, null);
                    }
                } else if (connectionResponse != null)
                    connectionResponse.onConnected(true, listenerConnection);
                return null;
            }
        };
        connectionThread.execute();
    }

    public boolean isAuthenticated() {
        if (connection.isAuthenticated()) return true;
        else return false;
    }

    // Disconnect Function
    public void disconnectConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (connection.isConnected()) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    // registration
    public void registration(String userName, String passWord) {
        try {
            Localpart lp = Localpart.from(userName);
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(lp, passWord);
            Log.d("xmpp: ", "Account create Requested");
            if (registrationResponse != null) registrationResponse.onRegistered(true);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XmppStringprepException: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure No Response: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XMPP error: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure not connected: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure InterruptedException: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        }
    }

    // set current user additional information
    public void setProfileInfo(String userName, String email) {
        VCard vcard = new VCard();
        vcard.setFirstName(userName);
        vcard.setEmailHome(email);
        vcard.setEmailWork(email);
        vcard.setNickName(userName);
        vcard.setField("Designation", "Developer");
        vcard.setPhoneHome("Voice", "12783849404");
        vcard.setOrganization("Save the Children");
        try {
            vcard.save(connection);
            Log.d("xmpp: ", "Profile Info Requested");
            if (profileSetupResponse != null) profileSetupResponse.onProfileSetup(true);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if (profileSetupResponse != null) profileSetupResponse.onProfileSetup(false);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if (registrationResponse != null) registrationResponse.onRegistered(false);
        }
    }

    // Login function
    public void login(final String userName, final String passWord) {
        try {
            connection.login(userName, passWord);
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("I am Available from Now");
            connection.sendStanza(presence);
            Log.d("xmpp: ", "Login Requested");
            EventBus.getDefault().postSticky(new BroadcastEvent("login", "", "done"));
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Login Failure: " + e.getMessage());
            if (loginResponse != null) loginResponse.onLoggedIn(false);
        } catch (Exception e) {
            Log.d("xmpp: ", "Login Failure: " + e.getMessage());
            if (loginResponse != null) loginResponse.onLoggedIn(false);
        }
    }

    // Get Friend List function
    public void getFriendList() {
        Roster roster = Roster.getInstanceFor(connection);
        if (roster != null && !roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xmpp::::::: ", "All User failed: " + e.getMessage());
                if (friendLoadResponse != null) friendLoadResponse.onLoaded(friendList);
            }
        }

        if (roster != null) {
            getFriendList(roster);
        }

        roster.addRosterLoadedListener(new RosterLoadedListener() {
            @Override
            public void onRosterLoaded(Roster roster) {
                if (roster != null) {
                    getFriendList(roster);
                }
            }

            @Override
            public void onRosterLoadingFailed(Exception e) {
                Log.d("xmpp:::::::: ", "All User failed: " + e.getMessage());
                if (friendLoadResponse != null) friendLoadResponse.onLoaded(friendList);
            }
        });
    }

    public void getFriendList(Roster roster) {
        Collection<RosterEntry> entries = roster.getEntries();
        Presence presence;
        friendList.clear();
        for (RosterEntry entry : entries) {
            presence = roster.getPresence(entry.getJid());
            MyFriend friend = new MyFriend();
            friend.setjID(String.valueOf(entry.getJid()));
            friend.setName(entry.getName());
            friend.setStatus(presence.getType().name());
            friend.setStatus(presence.getStatus());
            friendList.add(friend);
        }
        if (friendLoadResponse != null) friendLoadResponse.onLoaded(friendList);
    }

    // send message using Stanza of Smack
    public void sendStanza(String username, Message.Type type, String subject, String chat) {
        Jid jid = null;
        try {
            jid = JidCreate.bareFrom(username + "@" + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "BareJid create Failure: " + e.getMessage());
        }

        Message message = new Message(jid, type);
        message.setFrom(dt.pref.getString("username"));
        message.setSubject(subject);
        message.setBody(chat);

        //Creating Standard packet extension with name as 'timestamp' and urn as 'urn:xmpp:timestamp'
        StandardExtensionElement messageTimeStamp = StandardExtensionElement.builder(
                "timestamp", "urn:xmpp:timestamp")
                .addAttribute("timestamp", String.valueOf(System.currentTimeMillis()))  //Setting value in extension
                .build();

        //Add extension to message tag
        message.addExtension(messageTimeStamp);

        try {
            connection.sendStanza(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: " + e.getMessage());
        }
    }

    // received Messages from individual user by Stanza
    public void receiveStanza() {
        mChatFilter = MessageTypeFilter.CHAT;
        mChatStanzaListener = new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                Message message = (Message) packet;
                if (message.getBody() != null) {
                    String sender = String.valueOf(message.getFrom());
                    String from = sender.substring(0, sender.indexOf("@"));
                    //Get the extension from message
                    StandardExtensionElement messageTimeStamp = (StandardExtensionElement) message
                            .getExtension("urn:xmpp:timestamp");

                    String timestamp = "";
                    //Get the value from extension
                    if (messageTimeStamp != null) {
                        long timestampOriginal = Long.parseLong(messageTimeStamp.getAttributeValue("timestamp"));
                        timestamp = convertDate(timestampOriginal, "dd-MMM-yyyy h:mm a");
                    }
                    /*if (NotificationUtils.isAppIsInBackground(mContext)) {
                        Intent resultIntent = new Intent(mContext, SplashActivity.class);
                        resultIntent.putExtra("message", body);
                        NotificationUtils notificationUtils = new NotificationUtils(mContext);
                        notificationUtils.showNotificationMessage(subject, body, "", resultIntent);
                    }*/
                    EventBus.getDefault().postSticky(new BroadcastEvent("chat", new ChatItem(message.getType(), message.getSubject(), message.getBody(), message.getStanzaId(), timestamp, from, false)));
                }
            }
        };
        connection.addSyncStanzaListener(mChatStanzaListener, mChatFilter);   // remove addAsyncStanzaListener to avoid duplicate messages
    }

    public onOldMessagesResponse oldMessagesResponse = null;

    public interface onOldMessagesResponse {
        void onReceived(List<Message> message);
    }

    public void setOldMessagesResponseListener(onOldMessagesResponse listener) {
        oldMessagesResponse = listener;
    }


    public void getOldMessages(String username) {
        MamManager mamManager = MamManager.getInstanceFor(connection);
        try {
            boolean isSupported = mamManager.isSupported(); // it's true
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "MamManager isSupported error: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "MamManager isSupported error: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "MamManager isSupported error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "MamManager isSupported error: " + e.getMessage());
        }

        Jid jid = null;
        try {
            jid = JidCreate.bareFrom(username + "@" + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "BareJid create Failure: " + e.getMessage());
        }

        MamManager.MamQuery mamQueryResult = null;
        try {
            mamQueryResult = mamManager.queryMostRecentPage(jid, 10000);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Old Messages Error: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Old Messages Error: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Old Messages Error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Old Messages Error: " + e.getMessage());
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Old Messages Error: " + e.getMessage());
        }

        List<Message> forwardedMessages = mamQueryResult.getMessages();
        if (forwardedMessages != null) {
            if (forwardedMessages.size() > 0) {
                for (Message message: forwardedMessages) {
                    String sender = String.valueOf(message.getFrom());
                    String from = sender.substring(0, sender.indexOf("@"));
                    //Get the extension from message
                    StandardExtensionElement messageTimeStamp = (StandardExtensionElement) message.getExtension("urn:xmpp:timestamp");
                    String timestamp = "";
                    //Get the value from extension
                    if (messageTimeStamp != null) {
                        long timestampOriginal = Long.parseLong(messageTimeStamp.getAttributeValue("timestamp"));
                        timestamp = convertDate(timestampOriginal, "dd-MMM-yyyy h:mm a");
                    }
                    EventBus.getDefault().postSticky(new BroadcastEvent("chat", new ChatItem(message.getType(), message.getSubject(), message.getBody(), message.getStanzaId(), timestamp, from, false)));
                }
            }
        }
        if(oldMessagesResponse!=null)oldMessagesResponse.onReceived(forwardedMessages);
    }

    public onRoomLoadResponse roomLoadResponse = null;

    public interface onRoomLoadResponse {
        void onLoad(List<HostedRoom> hostedRoomList);
    }

    public void setRoomLoadResponseListener(onRoomLoadResponse listener) {
        roomLoadResponse = listener;
    }

    public void getRoomList(){
        manager = MultiUserChatManager.getInstanceFor(connection);
        List<HostedRoom> hostedRoomList = null;

        DomainBareJid serviceName = null;
        try {
            serviceName = JidCreate.domainBareFrom(CHAT_ROOM_SERVICE_NAME+CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "service name error: " + e.getMessage());
            if(roomLoadResponse!=null)roomLoadResponse.onLoad(hostedRoomList);
        }

        try {
            hostedRoomList = manager.getHostedRooms(serviceName);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
            if(roomLoadResponse!=null)roomLoadResponse.onLoad(hostedRoomList);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
            if(roomLoadResponse!=null)roomLoadResponse.onLoad(hostedRoomList);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
            if(roomLoadResponse!=null)roomLoadResponse.onLoad(hostedRoomList);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
            if(roomLoadResponse!=null)roomLoadResponse.onLoad(hostedRoomList);
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
            if(roomLoadResponse!=null)roomLoadResponse.onLoad(hostedRoomList);
        }

        if (hostedRoomList != null) {
            for (HostedRoom room: hostedRoomList) {
                Log.d("xmpp: ", "Room Name: " + room.getName() + "\nRoom JID: " + room.getJid());
            }
            if(roomLoadResponse!=null)roomLoadResponse.onLoad(hostedRoomList);
        }
    }

    public RoomItem getRoomInfo(String roomName) {
        manager = MultiUserChatManager.getInstanceFor(connection);
        RoomItem roomItem = null;
        EntityBareJid mucJid = null;
        try {
            mucJid = (EntityBareJid) JidCreate.bareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "EntityBareJid error: " + e.getMessage());
        }
        multiUserChat = manager.getMultiUserChat(mucJid);
        try {
            RoomInfo roomInfo = manager.getRoomInfo(mucJid);
            roomItem = new RoomItem();
            //roomItem.setOwner(String.valueOf(multiUserChat.getOwners().get(0).getNick()));
            roomItem.setSubject(multiUserChat.getSubject());
            roomItem.setDescription(roomInfo.getDescription());
            roomItem.setNick(String.valueOf(multiUserChat.getNickname()));
            roomItem.setOccupantsCount(multiUserChat.getOccupantsCount());
            List<EntityFullJid> fullJidList = multiUserChat.getOccupants();
            List<String> occupants = new ArrayList<String>();
            for (EntityFullJid jid: fullJidList) {
                occupants.add(String.valueOf(jid));
            }
            roomItem.setOccupants(occupants);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
        }

        return roomItem;
    }


    public static String convertDate(Long dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
    }

    public XMPPConnection listenerConnection = null;

    // Connection Listener to check connection state
    public class XMPPConnectionListener implements ConnectionListener {

        @Override
        public void connected(final XMPPConnection connection) {
            Log.d("xmpp: ", "Connected!");
            listenerConnection = connection;
            if (connectionResponse != null) connectionResponse.onConnected(true, connection);
        }

        @Override
        public void connectionClosed() {
            Log.d("xmpp: ", "ConnectionCLosed!");
            if (connectionResponse != null) connectionResponse.onConnected(false, null);
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            Log.d("xmpp: ", "ConnectionClosedOn Error: " + arg0.getMessage());
            if (connectionResponse != null) connectionResponse.onConnected(false, null);
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean authenticated) {
            Log.d("xmpp: ", "Authenticated : " + authenticated);
            if (authenticated) if (loginResponse != null) loginResponse.onLoggedIn(true);
        }
    }

    public void setUpReceiver() {
        // send ping
        sendPing(connection);
        // init message delivery reports
        initMessageDeliveryStatus();
        // init receive friend Request
        receiveFriendRequest();
    }

    // method for ping manager
    public void sendPing(XMPPConnection connection) {
        PingManager pm = PingManager.getInstanceFor(connection);
        pm.setPingInterval(5);  // 5 sec
        pm.registerPingFailedListener(new PingFailedListener() {
            @Override
            public void pingFailed() {
                Log.d("xmpp: ", "Ping Failed");
            }
        });
        try {
            pm.pingMyServer();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Ping Failed: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Ping Failed: " + e.getMessage());
        }
    }

    // method to init Message Delivery Status
    public void initMessageDeliveryStatus() {
        DeliveryReceiptManager dm = DeliveryReceiptManager
                .getInstanceFor(connection);
        dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        dm.addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
                Log.d("xmpp: ", "Delivery Report: " + toJid);
            }
        });
    }

    // method to receive Friend Request
    public void receiveFriendRequest() {
        Roster roster = Roster.getInstanceFor(connection);
        roster.addSubscribeListener(new SubscribeListener() {
            @Override
            public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
                if (subscribeRequest.getType() == Presence.Type.subscribe) {
                    String username = String.valueOf(from);
                    Log.d("xmpp: ", "Friend request from: " + username);
                    toast("Friend request from: " + username);
                    //sendFriendRequest(username, Presence.Type.subscribed);
                    //sendFriendRequest(username, Presence.Type.subscribe);
                }
                return null;
            }
        });
    }

    public void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
