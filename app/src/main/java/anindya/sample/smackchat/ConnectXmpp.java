package anindya.sample.smackchat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class ConnectXmpp extends Service {

    private String userName;
    private String passWord;
    private String mChat;
    private String mSubject;
    private MyXMPP xmpp = new MyXMPP(this);

    public ConnectXmpp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new LocalBinder<ConnectXmpp>(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            try {
                userName = intent.getStringExtra("user");
                passWord = intent.getStringExtra("pwd");
                mChat = intent.getStringExtra("chat");
                mSubject = intent.getStringExtra("subject");
            } catch (Exception e) {
            }
            String code = intent.getStringExtra("code");
            if (code.equals("0")) {
                xmpp.initForLogin(userName, passWord);
                xmpp.connectConnection();
            } else if (code.equals("1")) {
                xmpp.joinChatRoom(userName);
            } else if (code.equals("2")) {
                xmpp.sendChat(mChat, mSubject);
            } else if (code.equals("3")) {
                xmpp.exitFromRoom();
            } else if (code.equals("4")) {
                xmpp.initForRegistration(userName, passWord);
                xmpp.connectConnection();
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //xmpp.disconnectConnection();
        super.onDestroy();
    }

}
