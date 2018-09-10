package anindya.sample.smackchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.XMPPConnection;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;
import base.droidtool.dtlib.SweetAlert;


public class SplashActivity extends BaseActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private String userName;
    private String password;
    private Window win;
    private int Count = 0;
    private boolean isActive = false;

    @Override
    public void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, XmppService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        if(!EventBus.getDefault().isRegistered(this))EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        isActive = false;
        if(EventBus.getDefault().isRegistered(this))EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(BroadcastEvent event) {
        Log.d("xmpp: ", "BroadcastEvent: " + event.item + "\nCategory: " + event.category + "\nMessage: " + event.message);
        if(event.item.equals("login")){
            mService.setUpReceiver();
            dt.tools.startActivity(HomeActivity.class, "");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // added the following methods to awake the device from lock state and keep screen on so the broadcast messages could be received
        win = this.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.splash);

        super.register(this, "");
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        isActive = true;

        getUserInfo();
        GoToNext();
    }

    // get user login info from shared preference
    private void getUserInfo() {
        userName = dt.pref.getString("username");
        password = dt.pref.getString("password");
    }

    // Go to Home Page
    private void GoToNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dt.pref.getBoolean("login")) {
                    if(dt.droidNet.hasConnection()){
                        showDialog();
                        xmppLogin();
                    }
                    else {
                        hideDialog();
                        dt.droidNet.internetErrorDialog();
                    }
                } else {
                    dt.tools.startActivity(LoginActivity.class, "");
                }
            }
        }, SPLASH_TIME_OUT);
    }

    // xmpp login method
    private void xmppLogin() {
        Count++;
        if (Count % 4 == 0) {
            Log.d("xmpp", "time out");
            loginFailed();
        } else {
            try {
                mService.initConnection(userName, password, new XmppService.onConnectionResponse() {
                    @Override
                    public void onConnected(boolean isConnected, XMPPConnection connection) {
                        if(isConnected){
                            mService.login(userName, password, new XmppService.onLoginResponse() {
                                @Override
                                public void onLoggedIn(boolean isLogged) {
                                    if (!isLogged) loginFailed();
                                }
                            });
                        } else loginFailed();
                    }
                });
                mService.connectConnection();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xmpp: ", "UI:: Login Error: " + e.getMessage());
                xmppLogin();
            }
        }
    }

    public void loginFailed(){
        hideDialog();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isActive){
                    dt.alert.showWarningWithOneButton("Login Failed");
                    dt.alert.setAlertListener(new SweetAlert.AlertListener() {
                        @Override
                        public void onAlertClick(boolean isCancel) {
                            finish();
                        }
                    });
                }
            }
        });
    }
}

