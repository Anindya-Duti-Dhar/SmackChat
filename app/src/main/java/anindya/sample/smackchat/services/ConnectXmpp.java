package anindya.sample.smackchat.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.packet.Presence;

import anindya.sample.smackchat.utils.LocalBinder;
import anindya.sample.smackchat.utils.MyXMPP;
import anindya.sample.smackchat.utils.PrefManager;

import static anindya.sample.smackchat.utils.Const.CHAT_DEMO_OPPONENT_NAME;
import static anindya.sample.smackchat.utils.Const.CHAT_SERVER_SERVICE_NAME;


public class ConnectXmpp extends Service {

    private String userName;
    private String roomName;
    private String passWord;
    private String mChat;
    private String mSubject;
    private MyXMPP xmpp = new MyXMPP(this);
    Context context = this;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("xmpp: ", "connection service onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new LocalBinder<ConnectXmpp>(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("xmpp: ", "connection service onStartCommand");
        if (intent != null) {
            try {
                roomName = "scibd";
                userName = intent.getStringExtra("user");
                passWord = intent.getStringExtra("pwd");
                mChat = intent.getStringExtra("chat");
                mSubject = intent.getStringExtra("subject");
            } catch (Exception e) {
            }
            String code = intent.getStringExtra("code");
            // login
            if (code.equals("0")) {
                xmpp.initForLogin(userName, passWord);
                xmpp.connectConnection();
           }
            // join chat room
             else if (code.equals("1")) {
                xmpp.joinChatRoom(userName, roomName);
            }
            // login and join chat room
            else if (code.equals("122")) {
                xmpp.initForLogin(userName, passWord);
                xmpp.connectConnection();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xmpp.joinChatRoom(userName, roomName);
                        xmpp.receiveStanza();
                        xmpp.getOldMessages();
                    }
                }, 1000);
            }
            // send chat
            else if (code.equals("2")) {
                xmpp.sendGroupChat(mChat, mSubject);
                xmpp.sendStanza(CHAT_DEMO_OPPONENT_NAME, mChat, mSubject);
                xmpp.sendFriendRequest(CHAT_DEMO_OPPONENT_NAME + "@" + CHAT_SERVER_SERVICE_NAME, Presence.Type.subscribe);
            }
            // exit from chat room
            else if (code.equals("3")) {
                xmpp.exitFromRoom();
            }
            // registration
            else if (code.equals("4")) {
                xmpp.initForRegistration(userName, passWord);
                xmpp.connectConnection();
            }
            // create chat room
            else if (code.equals("5")) {
                xmpp.createPersistentRoom(userName, roomName);
                //xmpp.createChatRoom(roomName);
            }
            // destroy chat room
            else if (code.equals("6")) {
                xmpp.destroyChatRoom();
            }
            // configure chat room after create new chat room
            else if (code.equals("7")) {
                xmpp.configRoom(roomName);
            }
            // logout from the server
            else if (code.equals("9")) {
                xmpp.disconnectConnection();
            }
            // room status from the server
            else if (code.equals("10")) {
                xmpp.getRoomStatus(roomName);
            }
            // check user status from the server
            else if (code.equals("12")) {
                xmpp.userStatus(userName);
            }
        }
        //return START_NOT_STICKY;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("xmpp: ", "connection service destroyed");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // disconnect user
        xmpp.disconnectConnection();
        // re launch service
        Intent intent = new Intent(getApplicationContext(), ConnectXmpp.class);
        intent.putExtra("user", PrefManager.getUserName(context));
        intent.putExtra("pwd", PrefManager.getUserPassword(context));
        intent.putExtra("code", "122");
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        Log.e("xmpp: ", "connection service going to be destroyed");
    }

}
