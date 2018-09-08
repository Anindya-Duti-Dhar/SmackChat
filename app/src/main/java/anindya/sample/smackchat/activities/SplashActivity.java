package anindya.sample.smackchat.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.jivesoftware.smack.XMPPConnection;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.services.XmppService;
import anindya.sample.smackchat.utils.PrefManager;


public class SplashActivity extends BaseActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private String userName;
    private String password;
    private Window win;
    private int Count = 0;

    @Override
    public void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, XmppService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(SplashActivity.this).registerReceiver(mBroadcastReceiver, new IntentFilter("login"));
    }

    @Override
    public void onStop() {
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        LocalBroadcastManager.getInstance(SplashActivity.this).unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // added the following methods to awake the device from lock state and keep screen on so the broadcast messages could be received
        win = this.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.splash);

        getUserInfo();

        GoToNext();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("login")) {
                    mService.setUpReceiver();
                    startHomeActivity();
                }
            }
        };
    }

    // get user login info from shared preference
    private void getUserInfo() {
        userName = PrefManager.getUserName(SplashActivity.this);
        password = PrefManager.getUserPassword(SplashActivity.this);
    }

    // Go to Home Page
    private void GoToNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PrefManager.getUserLoggedData(SplashActivity.this).equals("Yes")) {
                    xmppLogin();
                } else {
                    goToLoginPage();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void goToLoginPage(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    // xmpp login method
    private void xmppLogin() {
        Count++;
        if (Count % 4 == 0) {
            Log.d("xmpp", "time out");
            toast("Login Failed");
        } else {
            try {
                mService.initConnection(userName, password, new XmppService.onConnectionResponse() {
                    @Override
                    public void onConnected(XMPPConnection connection) {
                        if(!connection.isAuthenticated()){
                            mService.login(userName, password, new XmppService.onLoginResponse() {
                                @Override
                                public void onLoggedIn(boolean isLogged) {
                                    if(!isLogged)toast("Login Failed");
                                }
                            });
                        }
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

    // Launch Home Screen
    private void startHomeActivity() {
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this);
        Intent i2 = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(i2, oc2.toBundle());
        finish();
    }
}

