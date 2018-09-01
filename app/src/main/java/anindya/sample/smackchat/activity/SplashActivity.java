package anindya.sample.smackchat.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.services.ConnectXmpp;
import anindya.sample.smackchat.utils.NetworkChecking;
import anindya.sample.smackchat.utils.PrefManager;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static anindya.sample.smackchat.utils.NetworkChecking.getConnectivityStatusString;


public class SplashActivity extends AppCompatActivity {

    private String TAG = SplashActivity.class.getSimpleName();

    private static int SPLASH_TIME_OUT = 2000;

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    public String userName;
    public String password;

    RelativeLayout mSplashMainContent;
    Snackbar snackbar;

    boolean isWiFiConnected = false;

    Window win;

    int Count = 0;

    SweetAlertDialog mLoginErrorDialog;

    private boolean internetConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate");

        // added the following methods to awake the device from lock state and keep screen on so the broadcast messages could be received
        win = this.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.splash);

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(Color.parseColor("#08427a"));

        // get current version code and name
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        //Log.d("xmpp: ", "version code: " + versionNumber);
        String versionName = pinfo.versionName;
        //Log.d("xmpp: ", "version name: " + versionName);
        // get current version code and name end

        mSplashMainContent = (RelativeLayout) findViewById(R.id.splash_main_content);

        // initialize snack bar
        initSnackBar();

        //initialize progress dialog object
        initProgressDialog();

        // get user login info from shared preference
        getUserInfo();

        // get broadcast messages for different action
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("signin")) {
                    mProgressDialog.dismiss();
                    launchHomeScreen();
                    Log.d("xmpp", "successfully Logged in ");
                } else if (intent.getAction().equals("connectionerror")) {
                    XmppLogin();
                } else if (intent.getAction().equals("signinerror")) {
                    XmppLogin();
                }
            }
        };
    }

    //initialize progress dialog object
    public void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.getting_ready));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }

    // get user login info from shared preference
    public void getUserInfo() {
        userName = PrefManager.getUserName(SplashActivity.this);
        password = PrefManager.getUserPassword(SplashActivity.this);
    }

    // xmpp login method
    public void XmppLogin() {
        Count++;
        Log.d("xmpp", "login count: " + Count);
        if (Count % 4 == 0) {
            Log.d("xmpp", "time out");
            mProgressDialog.dismiss();
            // after 3rd attempt
            LoginErrorWarning();
        } else {
            try {
                Intent intent = new Intent(getBaseContext(), ConnectXmpp.class);
                intent.putExtra("user", userName);
                intent.putExtra("pwd", password);
                intent.putExtra("code", "0");
                startService(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xmpp: ", "UI:: Login Error: " + e.getMessage());
                XmppLogin();
            }
        }
    }

    // create dialog pop up for login error
    public void LoginErrorWarning() {
        mLoginErrorDialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE);
        mLoginErrorDialog
                .setTitleText(getString(R.string.login_error_dialog_title))
                .setContentText(getString(R.string.login_error_dialog_message))
                .setConfirmText(getString(R.string.yes))
                .setCancelText(getString(R.string.no))
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        finish();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        mProgressDialog.show();
                        XmppLogin();
                    }
                })
                .show();
        mLoginErrorDialog.setCancelable(false);
        mLoginErrorDialog.setCanceledOnTouchOutside(false);
    }

    // Go to Home Page
    public void GoToNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // check if user was logged in previously
                // Start your app main activity
                // close this activity
                // Check if user's login data stored previously to make login
                if (PrefManager.getUserLoggedData(SplashActivity.this).equals("Yes")) {
                    // Check if user already has a running session
                    mProgressDialog.show();
                    // Go to Login
                    XmppLogin();
                } else {
                    mProgressDialog.dismiss();
                    Intent i = new Intent(SplashActivity.this, Login.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        }, SPLASH_TIME_OUT);
    }

    // Launch Home Screen
    private void launchHomeScreen() {
        Intent i = new Intent(SplashActivity.this, Chat.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // create snack bar
    public void initSnackBar() {
        // show no internet message
        snackbar = Snackbar
                .make(mSplashMainContent, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // go to wifi settings page tapping on retry button
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
    }

    private void toast(String text) {
        Toast.makeText(SplashActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // register run time internet checking broadcast receiver
        registerInternetCheckReceiver();

        // Initializing Internet Check
        if (NetworkChecking.hasConnection(SplashActivity.this)) {
            // Check if connected
            isWiFiConnected = true;
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
            // for run time permission check
            GoToNext();
        } else {
            // if there is no internet
            isWiFiConnected = false;
            // show Snack bar message
            snackbar.show();
        }

        // register receiver for connection
        LocalBroadcastManager.getInstance(SplashActivity.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("signin"));

        // register receiver for connection error
        LocalBroadcastManager.getInstance(SplashActivity.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("connectionerror"));

        // register receiver for login error
        LocalBroadcastManager.getInstance(SplashActivity.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("signinerror"));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        // unregister all receiver
        unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(SplashActivity.this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
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
            String internetStatus = "";
            if (status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
                internetStatus = "Internet Connected";
            } else {
                internetStatus = "Lost Internet Connection";
            }

            if (internetStatus.equalsIgnoreCase("Lost Internet Connection")) {
                if (internetConnected) {
                    if (!snackbar.isShown()) {
                        snackbar.show();
                    }
                    Log.d("xmpp", "connectivity1:: " + internetStatus);
                    internetConnected = false;
                }
            } else {
                if (!internetConnected) {
                    if (snackbar.isShown()) {
                        snackbar.dismiss();
                        mProgressDialog.show();
                        XmppLogin();
                    }
                    Log.d("xmpp", "connectivity2:: " + internetStatus);
                    internetConnected = true;
                }
            }
        }
    };
}

