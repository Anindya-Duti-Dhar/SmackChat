package anindya.sample.smackchat.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.DomainBareJid;
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

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.model.ChatEvent;
import anindya.sample.smackchat.model.MyFriend;
import anindya.sample.smackchat.model.Users;

import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_ADDRESS;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_PORT;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_SERVICE_NAME;


public class XmppManager {

    public Context mContext;
    public String userName, passWord;
    public AbstractXMPPConnection connection;
    public XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    public List<MyFriend> friendList = new ArrayList<MyFriend>();

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

    public XmppManager(Context context) {
        mContext = context;
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
                if(!connection.isConnected()){
                    try {
                        connection.connect();
                    } catch (SmackException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if(connectionResponse!=null)connectionResponse.onConnected(false, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if(connectionResponse!=null)connectionResponse.onConnected(false, null);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if(connectionResponse!=null)connectionResponse.onConnected(false, null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d("xmpp: ", "Connection Error: " + e.getMessage());
                        if(connectionResponse!=null)connectionResponse.onConnected(false, null);
                    }
                } else if(connectionResponse!=null)connectionResponse.onConnected(true, listenerConnection);
                return null;
            }
        };
        connectionThread.execute();
    }

    public boolean isAuthenticated(){
        if(connection.isAuthenticated())return true;
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
            if(registrationResponse!=null)registrationResponse.onRegistered(true);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XmppStringprepException: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure No Response: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure XMPP error: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure not connected: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Registration Failure InterruptedException: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
        }
    }

    // set current user additional information
    public void setProfileInfo(String userName, String email) {
        VCard vcard = new VCard();
        vcard.setFirstName(userName);
        vcard.setEmailHome(email);
        vcard.setNickName(userName);
        vcard.setField("Designation", "Developer");
        vcard.setPhoneHome("Voice", "12783849404");
        vcard.setOrganization("Save the Children");
        try {
            vcard.save(connection);
            Log.d("xmpp: ", "Profile Info Requested");
            if(profileSetupResponse!=null)profileSetupResponse.onProfileSetup(true);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if(profileSetupResponse!=null)profileSetupResponse.onProfileSetup(false);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("xmpp: ", "Profile Info Failed: " + e.getMessage());
            if(registrationResponse!=null)registrationResponse.onRegistered(false);
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
            if(loginResponse!=null)loginResponse.onLoggedIn(false);
        } catch (Exception e) {
            Log.d("xmpp: ", "Login Failure: " + e.getMessage());
            if(loginResponse!=null)loginResponse.onLoggedIn(false);
        }
    }

    // Get Friend List function
    public void getFriendList(){
        Roster roster = Roster.getInstanceFor(connection);
        if (roster != null && !roster.isLoaded()) {
            try{
                roster.reloadAndWait();
            }catch (Exception e){
                e.printStackTrace();
                Log.d("xmpp::::::: ", "All User failed: " + e.getMessage());
                if(friendLoadResponse!=null)friendLoadResponse.onLoaded(friendList);
            }
        }

        if (roster != null){
            getFriendList(roster);
        }

        roster.addRosterLoadedListener(new RosterLoadedListener() {
            @Override
            public void onRosterLoaded(Roster roster) {
                if (roster != null){
                    getFriendList(roster);
                }
            }

            @Override
            public void onRosterLoadingFailed(Exception e) {
                Log.d("xmpp:::::::: ", "All User failed: " + e.getMessage());
                if(friendLoadResponse!=null)friendLoadResponse.onLoaded(friendList);
            }
        });
    }

    public void getFriendList(Roster roster){
        Collection<RosterEntry> entries = roster.getEntries();
        Presence presence;
        friendList.clear();
        for(RosterEntry entry : entries) {
            presence = roster.getPresence(entry.getJid());
            MyFriend friend = new MyFriend();
            friend.setjID(String.valueOf(entry.getJid()));
            friend.setName(entry.getName());
            friend.setStatus(presence.getType().name());
            friend.setStatus(presence.getStatus());
            friendList.add(friend);
        }
        if(friendLoadResponse!=null)friendLoadResponse.onLoaded(friendList);
    }

    public XMPPConnection listenerConnection = null;

    // Connection Listener to check connection state
    public class XMPPConnectionListener implements ConnectionListener {

        @Override
        public void connected(final XMPPConnection connection) {
            Log.d("xmpp: ", "Connected!");
            listenerConnection = connection;
            if(connectionResponse!=null)connectionResponse.onConnected(true, connection);
        }

        @Override
        public void connectionClosed() {
            Log.d("xmpp: ", "ConnectionCLosed!");
            if(connectionResponse!=null)connectionResponse.onConnected(false, null);
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            Log.d("xmpp: ", "ConnectionClosedOn Error: "+arg0.getMessage());
            if(connectionResponse!=null)connectionResponse.onConnected(false, null);
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean authenticated) {
            Log.d("xmpp: ", "Authenticated : "+ authenticated);
            if (authenticated)if(loginResponse!=null)loginResponse.onLoggedIn(true);
        }
    }

    public void setUpReceiver(){
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
    public void receiveFriendRequest(){
        Roster roster = Roster.getInstanceFor(connection);
        roster.addSubscribeListener(new SubscribeListener() {
            @Override
            public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
                if(subscribeRequest.getType()==Presence.Type.subscribe){
                    String username = String.valueOf(from);
                    Log.d("xmpp: ", "Friend request from: "+username);
                    toast("Friend request from: "+username);
                    //sendFriendRequest(username, Presence.Type.subscribed);
                    //sendFriendRequest(username, Presence.Type.subscribe);
                }
                return null;
            }
        });
    }

    public void toast(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
