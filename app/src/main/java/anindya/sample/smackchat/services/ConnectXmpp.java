package anindya.sample.smackchat.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import anindya.sample.smackchat.utils.LocalBinder;
import anindya.sample.smackchat.utils.MyXMPP;

import static anindya.sample.smackchat.utils.NetworkChecking.getConnectivityStatusString;


public class ConnectXmpp extends Service {

    private String userName;
    private String roomName;
    private String passWord;
    private String mChat;
    private String mSubject;
    private MyXMPP xmpp = new MyXMPP(this);

    private boolean internetConnected=true;

    Context context = this;

    public ConnectXmpp() {
    }

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

        // register run time internet checking broadcast receiver
        registerInternetCheckReceiver();

        if (intent != null) {
            try {
                roomName = "demo";
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
                    }
                }, 1000);
            }
            // send chat
            else if (code.equals("2")) {
                xmpp.sendChat(mChat, mSubject);
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
                xmpp.createPersistentRoom(userName);
                //xmpp.createChatRoom(userName);
            }
            // destroy chat room
            else if (code.equals("6")) {
                xmpp.destroyChatRoom();
            }
            // configure chat room after create new chat room
            else if (code.equals("7")) {
                xmpp.configRoom();
            }
            // logout from the server
            else if (code.equals("9")) {
                xmpp.disconnectConnection();
            }
            // room status from the server
            else if (code.equals("10")) {
                xmpp.getRoomStatus(userName);
            }
            // check user status from the server
            else if (code.equals("12")) {
                xmpp.UserStatus(userName);
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
        // set user has no running session
        //PrefManager.setUserLoggedIn(context, "No");
        // unregister all receiver
        ///unregisterReceiver(broadcastReceiver);
        // disconnect user
        xmpp.disconnectConnection();
        Intent intent = new Intent(getApplicationContext(), ConnectXmpp.class);
        intent.putExtra("user", "sadaf");
        intent.putExtra("pwd", "sadaf");
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

    //Method to register runtime broadcast receiver to show internet connection status
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }


    //Runtime Broadcast receiver inner class to capture internet connectivity events
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            String internetStatus="";
            if(status.equalsIgnoreCase("Wifi enabled")||status.equalsIgnoreCase("Mobile data enabled")){
                internetStatus="Internet Connected";
            }else {
                internetStatus="Lost Internet Connection";
            }

            if(internetStatus.equalsIgnoreCase("Lost Internet Connection")){
                if(internetConnected){
                    Log.d("xmpp", "service connectivity:: "+internetStatus);
                    //PrefManager.setUserLoggedIn(context, "No");
                    sendMessage(internetStatus);
                    internetConnected=false;
                }
            }else{
                if(!internetConnected){
                    Log.d("xmpp", "service connectivity:: "+internetStatus);
                    sendMessage(internetStatus);
                    internetConnected=true;
                }
            }
        }
    };

    public void sendMessage(String message){
        Intent broadCastIntent = new Intent("internet");
        broadCastIntent.putExtra("action", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastIntent);
    }

}
