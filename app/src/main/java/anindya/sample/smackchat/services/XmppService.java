package anindya.sample.smackchat.services;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.List;

import anindya.sample.smackchat.model.MyFriend;
import anindya.sample.smackchat.model.Users;
import anindya.sample.smackchat.utils.LocalBinder;
import anindya.sample.smackchat.utils.XmppManager;

public class XmppService extends Service {

    private String userName;
    private String passWord;
    private XmppManager xmppManager = new XmppManager(this);
    Context mContext = this;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("xmpp: ", "XMPP service Binding");
        return new LocalBinder<XmppService>(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("xmpp: ", "XMPP service unBinding");
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("xmpp: ", "XMPP service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("xmpp: ", "connection service onStartCommand");

        //TODO

        return START_STICKY;
    }

    public interface onConnectionResponse {
        void onConnected(boolean isConnected, XMPPConnection connection);
    }

    public void initConnection(String username, String password, onConnectionResponse listener){
        final onConnectionResponse connectionResponse = listener;
        userName = username;
        passWord = password;
        xmppManager.setConnectionResponseListener(new XmppManager.onConnectionResponse() {
            @Override
            public void onConnected(boolean isConnected, XMPPConnection connection) {
                if(connectionResponse!=null)connectionResponse.onConnected(isConnected, connection);
            }
        });
        xmppManager.initConnectionBuilder(userName, passWord);
    }

    public AbstractXMPPConnection getConnection(){
        return xmppManager.connection;
    }

    public interface onRegistrationResponse {
        void onRegistered(boolean isRegistered);
    }

    public void registration(String userName, String passWord, onRegistrationResponse listener){
        final onRegistrationResponse registrationResponse = listener;
        xmppManager.setRegistrationResponseListener(new XmppManager.onRegistrationResponse() {
            @Override
            public void onRegistered(boolean isRegistered) {
                if(registrationResponse!=null)registrationResponse.onRegistered(isRegistered);
            }
        });
        xmppManager.registration(userName, passWord);
    }

    public interface onLoginResponse {
        void onLoggedIn(boolean isLogged);
    }

    public void login(String userName, String passWord, onLoginResponse listener){
        final onLoginResponse loginResponse = listener;
        xmppManager.setLoginResponseListener(new XmppManager.onLoginResponse() {
            @Override
            public void onLoggedIn(boolean isLogged) {
                if(loginResponse!=null)loginResponse.onLoggedIn(isLogged);
            }
        });
        xmppManager.login(userName, passWord);
    }

    public interface onProfileSetupResponse {
        void onProfileSetup(boolean isSetup);
    }

    public void setProfileInfo(String userName, String email, onProfileSetupResponse listener){
        final onProfileSetupResponse profileSetupResponse = listener;
        xmppManager.setProfileSetupResponseListener(new XmppManager.onProfileSetupResponse() {
            @Override
            public void onProfileSetup(boolean isSetup) {
                if(profileSetupResponse!=null)profileSetupResponse.onProfileSetup(isSetup);
            }
        });
        xmppManager.setProfileInfo(userName, email);
    }

    public interface onFriendLoadResponse {
        void onLoaded(List<MyFriend> friendList);
    }

    public void getFriendList(onFriendLoadResponse listener){
        final onFriendLoadResponse friendLoadResponse = listener;
        xmppManager.setFriendLoadResponseListener(new XmppManager.onFriendLoadResponse() {
            @Override
            public void onLoaded(List<MyFriend> friendList) {
                if(friendLoadResponse!=null)friendLoadResponse.onLoaded(friendList);
            }
        });
        xmppManager.getFriendList();
    }

    public boolean isAuthenticated(){
        if(xmppManager.isAuthenticated())return true;
        else return false;
    }

    public void connectConnection(){
        xmppManager.connectConnection();
    }

    public void setUpReceiver(){
        xmppManager.setUpReceiver();
    }

    public void sendStanza(String username, Message.Type type, String subject, String chat){
        xmppManager.sendStanza(username, type, subject, chat);
    }

    public void receiveStanza(){
        xmppManager.receiveStanza();
    }

    public interface onOldMessagesResponse {
        void onReceived(List<Message> message);
    }

    public void receiveOldMessages(String username, onOldMessagesResponse listener){
        final onOldMessagesResponse oldMessagesResponse = listener;
        xmppManager.setOldMessagesResponseListener(new XmppManager.onOldMessagesResponse() {
            @Override
            public void onReceived(List<Message> message) {
                if(oldMessagesResponse!=null)oldMessagesResponse.onReceived(message);
            }
        });
        xmppManager.getOldMessages(username);
    }

    @Override
    public void onDestroy() {
        Log.d("xmpp: ", "connection service destroyed");
        //xmppManager.disconnectConnection();
        super.onDestroy();
        // re launch service
        //reLaunchService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("xmpp: ", "connection service going to be destroyed");
        //reLaunchService();
    }

    public void reLaunchService(){
        // re launch service
        Intent intent = new Intent(this, XmppService.class);
        PendingIntent service = PendingIntent.getService(
                this,
                1001,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }
}
