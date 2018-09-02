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
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

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

        InetAddress address = null;
        try {
            address = InetAddress.getByName(CHAT_SERVER_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Internet Address error: " + e.getMessage());
        }

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
        configBuilder.setHostAddress(address);
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

        InetAddress address = null;
        try {
            address = InetAddress.getByName(CHAT_SERVER_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Internet Address error: " + e.getMessage());
        }

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
        configBuilder.setHostAddress(address);
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
                if (connection.isConnected()) {
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
                try {
                    connection.connect();
                    initMessageDeliveryStatus();
                    connected = true;
                    Log.d("xmpp: ", "Connection Success");
                } catch (SmackException e) {
                    e.printStackTrace();
                    Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                    sendBroadCast("connectionerror", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                    sendBroadCast("connectionerror", e.getMessage());
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                    sendBroadCast("connectionerror", e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                    sendBroadCast("connectionerror", e.getMessage());
                }
                return null;
            }
        };
        connectionThread.execute();
    }

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

    // registration
    public void registration() {
        try {
            // create the account:
            Localpart lp = Localpart.from(userName);
            // Registering the user
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(lp, passWord);
            Log.d("xmpp: ", "Registration Success");
            // call login method
            login();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XmppStringprepException: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure No Response: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XMPP error: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure not connected: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure InterruptedException: " + e.getMessage());
            sendBroadCast("signuperror", e.getMessage());
        }
    }

    // Login function
    public void login() {
        try {
            connection.login(userName, passWord);
            Log.d("xmpp: ", "Login Success");
            // Create a new presence. Pass in false to indicate we're unavailable
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("Love To Code");
            // Send the stanza
            connection.sendStanza(presence);
            // set extra information
            setMyExtraInfo();
            sendBroadCast("signin", "done");
            // send ping
            sendPing();
            // Roster entry
            Roster roster = Roster.getInstanceFor(connection);
            BareJid jid = null;
            try {
                jid = JidCreate.bareFrom(userName + "@" + CHAT_SERVER_SERVICE_NAME);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "BareJid create Failure: " + e.getMessage());
            }
            try {
                roster.createEntry(jid, userName, null);
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Roster Entry Failure: " + e.getMessage());
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Roster Entry Failure: " + e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Roster Entry Failure: " + e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Roster Entry Failure: " + e.getMessage());
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

    // set current user additional information
    public void setMyExtraInfo() {
        VCard vcard = new VCard();
        vcard.setFirstName(userName);
        vcard.setLastName("Sci");
        vcard.setEmailHome(userName + "@gmail.com");
        vcard.setMiddleName("Developer");
        vcard.setNickName("SCIBD");
        vcard.setPhoneHome("Voice", "12783849404");
        vcard.setOrganization("Save the Children");
        //vcard.setAvatar("" + image_path); //Image Path should be URL or Can be Byte Array etc.
        try {
            vcard.save(connection);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Extra Info Failed: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Extra Info Failed: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Extra Info Failed: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Extra Info Failed: " + e.getMessage());
        }
    }

    // method for ping manager
    public void sendPing() {
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

    //get specific info
    public void getUserInfo(String userName) {
        VCard card = new VCard();
        EntityBareJid entityBareJid = null;
        try {
            entityBareJid = (EntityBareJid) JidCreate.bareFrom(userName + "@" + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "EntityBareJid create Failure: " + e.getMessage());
        }
        try {
            card.load(connection, entityBareJid);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get User Info Failure: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get User Info Failure: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get User Info Failure: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get User Info Failure: " + e.getMessage());
        }
        Log.d("xmpp: ", "Friend's Nick Name: " + card.getNickName() + "\nFriend's Email: " + card.getEmailHome());
    }

    public void getCurrentRoster() {
        Roster roster = Roster.getInstanceFor(connection);
        roster.addRosterLoadedListener(new RosterLoadedListener() {
            @Override
            public void onRosterLoaded(Roster roster) {
                if (roster != null){
                    Collection<RosterEntry> entries = roster.getEntries();
                    for (RosterEntry entry : entries) {
                        System.out.println(entry);
                        Log.d("xmpp::::::::: ", "All User: username=== " + entry.getName());
                    }
                }
            }

            @Override
            public void onRosterLoadingFailed(Exception e) {
                Log.d("xmpp:::::::: ", "All User failed: " + e.getMessage());
            }
        });

        if (roster != null && !roster.isLoaded())
            try{
                roster.reloadAndWait();
                Log.d("xmpp:::::: ", "All User reloaded");
            }catch (Exception e){
                e.printStackTrace();
                Log.d("xmpp::::::: ", "All User failed: " + e.getMessage());
            }

        if (roster != null){
            Collection<RosterEntry> entries = roster.getEntries();
            for (RosterEntry entry : entries) {
                System.out.println(entry);
                Log.d("xmpp::::::: ", "All User: username=== " + entry.getName());
            }
        }
    }

    // get user list
    public void getBuddies() {
        DomainBareJid serviceName = null;
        try {
            serviceName = JidCreate.domainBareFrom(CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "service name error: " + e.getMessage());
        }

        UserSearchManager manager = new UserSearchManager(connection);
        try {
            Form searchForm = null;
            List<DomainBareJid> list = (List<DomainBareJid>) manager.getSearchServices();
            serviceName = list.get(0);
            searchForm = manager.getSearchForm(serviceName);
            Form answerForm = searchForm.createAnswerForm();
            UserSearch userSearch = new UserSearch();
            //answerForm.setAnswer("username", true);
            //answerForm.setAnswer("search", "*");

            ReportedData results = userSearch.sendSearchForm(connection, answerForm, serviceName);
            if (results != null) {
                List<ReportedData.Row> rows = results.getRows();
                for (ReportedData.Row row : rows) {
                    Log.d("xmpp: ", "user list: " + row.getValues("Username").toString());
                }
            } else {
                Log.d("xmpp: ", "user list:  No result found");
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Buddies Failure: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Buddies Failure: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Buddies Failure: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Buddies Failure: " + e.getMessage());
        }
    }

    // join chat room function
    public void joinChatRoom(String userName, String roomName) {
        manager = MultiUserChatManager.getInstanceFor(connection);

        EntityBareJid mucJid = null;
        try {
            mucJid = (EntityBareJid) JidCreate.bareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "EntityBareJid error: " + e.getMessage());
        }

        Resourcepart nickname = null;
        try {
            nickname = Resourcepart.from(userName);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Resourcepart error: " + e.getMessage());
        }

        multiUserChat = manager.getMultiUserChat(mucJid);
        try {
            multiUserChat.join(nickname);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Chat room join Error: " + e.getMessage());
            sendBroadCast("joinerror", e.getMessage());
        } catch (MultiUserChatException.NotAMucServiceException e) {
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
        }
    }

    // get room status
    public void getRoomStatus(String roomName) {
        manager = MultiUserChatManager.getInstanceFor(connection);

        EntityBareJid mucJid = null;
        try {
            mucJid = (EntityBareJid) JidCreate.bareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "EntityBareJid error: " + e.getMessage());
        }

        // Discover information about the room
        try {
            manager.getRoomInfo(mucJid);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Room Info Error: " + e.getMessage());
            sendBroadCast("roominfo", e.getMessage());
        }
        sendBroadCast("roominfo", "ok");
    }

    // configure room for getting messages
    public void configRoom(String roomName) {
        Log.d("xmpp: ", "ready to receive messages in the chat room");
        // add listener for receiving messages
        receiveGroupMessages(roomName);
        receiveStanza();
        getOldMessages();
        EntityBareJid mucJid = null;
        try {
            mucJid = (EntityBareJid) JidCreate.bareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "EntityBareJid error: " + e.getMessage());
        }

        //room info
        try {
            RoomInfo roomInfo = manager.getRoomInfo(mucJid);
            Resourcepart ownerNick = multiUserChat.getOwners().get(0).getNick();
            String roomNameGetFromServer = roomInfo.getName();
            String roomDescriptionFromServer = roomInfo.getDescription();
            //Log.d("xmpp: ", "Room Name: " + roomNameGetFromServer + " Room Description: " + roomInfo.getDescription() + " Room Owner Nick: " + ownerNick);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Room Configuration Error: " + e.getMessage());
        }

        // room list

        DomainBareJid serviceName = null;
        try {
            serviceName = JidCreate.domainBareFrom(CHAT_ROOM_SERVICE_NAME+CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "service name error: " + e.getMessage());
        }
        List<HostedRoom> hostedRooms = null;
        try {
            hostedRooms = manager.getHostedRooms(serviceName);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Get Hosted Rooms list error: " + e.getMessage());
        }
        if (hostedRooms != null) {
            int roomListSize = hostedRooms.size();
            if (roomListSize > 0) {
                for (int i = 0; i < roomListSize; i++) {
                    Log.d("xmpp: ", "Room List Id: " + i + "\nRoom Name: " + hostedRooms.get(i).getName() + "\nRoom JID: " + hostedRooms.get(i).getJid());
                }
            }
        }
        // get current roster
        getCurrentRoster();
        // get Roster
        getBuddies();
        //get user info
        getUserInfo(userName);
    }

    // check current user status
    public void UserStatus(String userName) {
        Roster roster = Roster.getInstanceFor(connection);
        BareJid jid = null;
        try {
            jid = JidCreate.bareFrom(userName + "@" + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "BareJid create Failure: " + e.getMessage());
        }
        Presence presence = roster.getPresence(jid);
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
        Message newMessage = new Message();
        newMessage.setBody(chat);
        newMessage.setSubject(subject);
        try {
            multiUserChat.sendMessage(newMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: " + e.getMessage());
        }
    }

    // send message using Stanza of Smack
    public void sendStanza(String chat, String subject) {
        Jid jid = null;
        try {
            jid = JidCreate.bareFrom(CHAT_DEMO_OPPONENT_NAME + "@" + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "BareJid create Failure: " + e.getMessage());
        }
        Message message = new Message(jid, Message.Type.chat);
        message.setFrom(connection.getUser());
        message.setBody(chat);
        message.setSubject(subject);
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

    // send message using Chat object of Smack
    public void sendChat(String chat, String subject) {
        Jid jid = null;
        try {
            jid = JidCreate.bareFrom(CHAT_DEMO_OPPONENT_NAME + "@" + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "BareJid create Failure: " + e.getMessage());
        }
        Message message = new Message(jid, Message.Type.chat);
        message.setFrom(connection.getUser());
        message.setBody(chat);
        message.setSubject(subject);
        try {
            mChat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Message send Error: " + e.getMessage());
        }
    }

    // received Messages using multiUserChat from room
    public void receiveGroupMessages(final String roomName) {
        if (connected) {
            //ArrayList<ChatItem> chatItem = new ArrayList<ChatItem>();
            filter = MessageTypeFilter.GROUPCHAT;
            mStanzaListener = new StanzaListener() {
                @Override
                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String from = String.valueOf(message.getFrom());
                        String OnlyUserName = from.replace(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME + "\u002F", ""); //remove room name // (here \u002F is for forward slash)
                        Log.d("xmpp: ", "Original sender: " + from);
                        String body = message.getBody();
                        String messageID = message.getStanzaId();
                        String subject = message.getSubject();
                        Log.d("xmpp: ", "From: " + OnlyUserName + "\nSubject: " + subject + "\nMessage: " + body + "\nMessage ID: " + messageID);
                        EventBus.getDefault().postSticky(new ChatEvent(OnlyUserName, body, subject, messageID));
                    }
                }
            };
            connection.addSyncStanzaListener(mStanzaListener, filter);   // remove addAsyncStanzaListener to avoid duplicate messages
        }
    }

    // received Messages from individual user by Stanza
    public void receiveStanza() {
        if (connected) {
            filter2 = MessageTypeFilter.CHAT;
            mStanzaListener2 = new StanzaListener() {
                @Override
                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String from = String.valueOf(message.getFrom());
                        String body = message.getBody();
                        String messageID = message.getStanzaId();
                        String subject = message.getSubject();
                        Log.d("xmpp: ", "From 2: " + from + "\nSubject 2: " + subject + "\nMessage 2: " + body + "\nMessage ID 2: " + messageID);
                        if (NotificationUtils.isAppIsInBackground(mContext)) {
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

    public void getOldMessages() {
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
            jid = JidCreate.bareFrom("duti" + "@" + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "BareJid create Failure: " + e.getMessage());
        }

        MamManager.MamQuery mamQueryResult = null;
        try {
            mamQueryResult = mamManager.queryMostRecentPage(jid, 10);
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
                for (int i = 0; i < forwardedMessages.size(); i++) {
                    Log.d("xmpp: ", "Message Archive: " + "\n" +
                            "Message From: " + forwardedMessages.get(i).getFrom() +
                            "Message Body: " + forwardedMessages.get(i).getBody());
                }
            }
        }
        getOfflineMessage();
    }

    public void getOfflineMessage() {
        List<Message> msgList = null;
        OfflineMessageManager omm = new OfflineMessageManager(connection);
        try {
            msgList = omm.getMessages();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Offline Message error: " + e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Offline Message error: " + e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Offline Message error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Offline Message error: " + e.getMessage());
        }

        if (msgList != null) {
            if (msgList.size() > 0) {
                for (int i = 0; i < msgList.size(); i++) {
                    Log.d("xmpp: ", "Offline Message: " + "\n" +
                            "Message From: " + msgList.get(i).getFrom() +
                            "Message Body: " + msgList.get(i).getBody());
                }
            }
        }

    }

    // received Messages from individual user by ChatManager
    public void receiveMessage() {
        if (connection.isAuthenticated()) {
            Log.w("app", "Auth done");
            chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(org.jivesoftware.smack.chat.Chat chat, boolean createdLocally) {
                    mChat = chat;
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                            String from = String.valueOf(message.getFrom());
                            String body = message.getBody();
                            String subject = message.getSubject();
                            String messageID = message.getStanzaId();
                            System.out.println("Received message: "
                                    + (message != null ? message.getBody() : "NULL"));
                            Log.d("xmpp: ", "From 3: " + from + "\nSubject 3: " + subject + "\nMessage 3: " + body + "\nMessage ID 3: " + messageID);

                        }
                    });
                    Log.w("app", chat.toString());
                }
            });
        }
    }

    // create persistent room
    public void createPersistentRoom(String userName, String roomName) {
        if (connection.isConnected() == true) {
            String roomDescription = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()); // current time
            manager = MultiUserChatManager.getInstanceFor(connection);

            EntityBareJid mucJid = null;
            try {
                mucJid = (EntityBareJid) JidCreate.bareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "EntityBareJid error: " + e.getMessage());
            }

            Resourcepart nickname = null;
            try {
                nickname = Resourcepart.from(userName);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Resourcepart error: " + e.getMessage());
            }

            multiUserChat = manager.getMultiUserChat(mucJid);

            // Create the room
            try {
                multiUserChat.create(nickname);
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (MultiUserChatException.MucAlreadyJoinedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (MultiUserChatException.MissingMucCreationAcknowledgeException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (MultiUserChatException.NotAMucServiceException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }

            // send configuration for persistent room
            Form form = null;
            try {
                form = multiUserChat.getConfigurationForm();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
            Form answerForm = form.createAnswerForm();
            answerForm.setAnswer("muc#roomconfig_publicroom", true);
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
            answerForm.setAnswer("muc#roomconfig_roomdesc", roomDescription);
            // Send room configuration form which indicates that we want
            try {
                multiUserChat.sendConfigurationForm(answerForm);
                sendBroadCast("create", "done");
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
        }
    }

    // destroy chat room
    // with broadcast
    public void destroyChatRoom() {

        EntityBareJid mucJid = null;
        try {
            mucJid = (EntityBareJid) JidCreate.bareFrom(ALTERNATE_CHAT_ROOM_REFERENCE + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "EntityBareJid error: " + e.getMessage());
        }

        try {
            multiUserChat.destroy("not interested in conversation", mucJid);
            Log.d("xmpp: ", "Destroy Chat Room by button!");
            sendBroadCast("destroy", "done");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room by button Error: " + e.getMessage());
            sendBroadCast("destroyerror", e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room by button Error: " + e.getMessage());
            sendBroadCast("destroyerror", e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room by button Error: " + e.getMessage());
            sendBroadCast("destroyerror", e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Destroy Room by button Error: " + e.getMessage());
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
        } else if (mType.equals("connectionerror")) {
            intentText = "connectionerror";
        } else if (mType.equals("create")) {
            intentText = "create";
        } else if (mType.equals("roomcreateerror")) {
            intentText = "roomcreateerror";
        } else if (mType.equals("destroyerror")) {
            intentText = "destroyerror";
        } else if (mType.equals("destroy")) {
            intentText = "destroy";
        } else if (mType.equals("connectionclosederror")) {
            intentText = "connectionclosederror";
        } else if (mType.equals("connectionclosed")) {
            intentText = "connectionclosed";
        } else if (mType.equals("roominfo")) {
            intentText = "roominfo";
        } else if (mType.equals("destroyduplicate")) {
            intentText = "destroyduplicate";
        } else if (mType.equals("destroyduplicateerror")) {
            intentText = "destroyduplicateerror";
        } else if (mType.equals("userstatus")) {
            intentText = "userstatus";
        } else if (mType.equals("joinerror")) {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Leave Chat Room Error: " + e.getMessage());
            sendBroadCast("exiterror", e.getMessage());
        }
    }

    // create temporary chat room
    public void createChatRoom(String userName, String roomName) {
        if (connection.isConnected() == true) {

            manager = MultiUserChatManager.getInstanceFor(connection);

            EntityBareJid mucJid = null;
            try {
                mucJid = (EntityBareJid) JidCreate.bareFrom(roomName + "@" + CHAT_ROOM_SERVICE_NAME + CHAT_SERVER_SERVICE_NAME);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "EntityBareJid error: " + e.getMessage());
            }

            Resourcepart nickname = null;
            try {
                nickname = Resourcepart.from(userName);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Resourcepart error: " + e.getMessage());
            }

            multiUserChat = manager.getMultiUserChat(mucJid);
            // Create the room
            try {
                multiUserChat.create(nickname);
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (MultiUserChatException.MucAlreadyJoinedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (MultiUserChatException.MissingMucCreationAcknowledgeException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (MultiUserChatException.NotAMucServiceException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Create Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
            // Send an empty room configuration form which indicates that we want
            // an instant room
            try {
                multiUserChat.sendConfigurationForm(new Form(DataForm.Type.submit));
                sendBroadCast("create", "done");
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("xmpp: ", "Temporary Chat Room Configuration Error: " + e.getMessage());
                sendBroadCast("roomcreateerror", e.getMessage());
            }
        }
    }

}
