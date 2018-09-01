package anindya.sample.smackchat.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();
    TextInputLayout inputLayoutUsername, inputLayoutPassword;
    EditText _usernameText, _passwordText;
    Button _loginButton;
    String userName;
    String password;
    Handler handler;

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;

    RelativeLayout mLoginMainContent;
    Snackbar snackbar;

    boolean isWiFiConnected = false;

    private boolean internetConnected=true;

    int Count =0;
    int Count2 = 0;

    SweetAlertDialog savedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.login);

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(Color.parseColor("#08427a"));

        // Set up the toolbar.
        Toolbar login_toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(login_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ctBr = getSupportActionBar();

        mLoginMainContent = (RelativeLayout) findViewById(R.id.login_main_content);

        initSnackBar();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.getting_ready));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        inputLayoutUsername = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);

        _usernameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);

        _loginButton = (Button) findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                // Initializing Internet Check
                if (isWiFiConnected) {
                    // go to signUp method
                    signUp();
                } else {
                    mProgressDialog.hide();
                    // if there is no internet
                    // Show Snack bar message
                    snackbar.show();
                }
            }
        });

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("signin")) {
                    mProgressDialog.hide();
                    PrefManager.setUserLoggedData(Login.this, "Yes");
                    PrefManager.setUserName(Login.this, userName.toLowerCase());
                    PrefManager.setUserPassword(Login.this, password);
                    handler = new Handler();
                    savedDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.SUCCESS_TYPE);
                    savedDialog.setTitleText(getString(R.string.success_dialog_title))
                            .setContentText(getString(R.string.sign_up_dialog_message))
                            .setConfirmText(getString(R.string.thanks))
                            .show();
                    savedDialog.setCancelable(false);
                    savedDialog.setCanceledOnTouchOutside(false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 1000ms
                            savedDialog.dismissWithAnimation();
                            startNewActivity();
                        }
                    }, 2500);
                    Log.d("xmpp: ", "successfully Logged in ");
                } else if (intent.getAction().equals("connectionerror")) {
                    XmppRegistration();
                } else if (intent.getAction().equals("signuperror")) {
                    String errorMessage = intent.getStringExtra("action");
                    if (errorMessage.equals("XMPPError: conflict - cancel")) {
                        XmppLogin();
                    } else {
                        XmppRegistration();
                    }
                } else if (intent.getAction().equals("signinerror")) {
                    XmppLogin();
                }
            }
        };
    }

    public void signUp() {
        Log.d(TAG, "signUp");

        if (!validate()) {
            onSignUpFailed();
            return;
        }
        // xmpp registration
        XmppRegistration();
    }

    private void startNewActivity() {
        Log.d(TAG, "startNewActivity");
        startActivity(new Intent(Login.this, Chat.class));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignUpFailed() {
        mProgressDialog.hide();
        Toast.makeText(getBaseContext(), getString(R.string.login_failed_message), Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3 || username.length() > 64) {
            _usernameText.setError(getString(R.string.valid_username));
            valid = false;
        } else if (username.contains(" ") || username.contains("@") || username.contains("#")) {
            _usernameText.setError(getString(R.string.valid_username2));
            valid = false;
        } else if (username.equals("samsung")) {
            _usernameText.setError(getString(R.string.valid_username3));
            valid = false;
        }
        else {
            _usernameText.setError(null);
        }


        if (password.isEmpty() || password.length() < 3 || password.length() > 32) {
            _passwordText.setError(getString(R.string.valid_password));
            valid = false;
        } else if (password.contains(" ")) {
            _passwordText.setError(getString(R.string.valid_username2));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void toast(String text) {
        Toast.makeText(Login.this, text, Toast.LENGTH_SHORT).show();
    }

    // back arrow action
    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        return true;
    }

    // back button press method
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void XmppLogin() {
        Count2++;
        Log.d("xmpp: ", "Login count: "+Count);
        if(Count2%4==0){
            Log.d("xmpp: ", "Login time out");
            mProgressDialog.hide();
            // after 3rd attempt
            _loginButton.setEnabled(true);
            _loginButton.setText(getString(R.string.try_again));
            toast(getString(R.string.xmpp_signin_error));
        }
        else {
            try {
                Intent intent = new Intent(getBaseContext(), ConnectXmpp.class);
                intent.putExtra("user", userName.toLowerCase());
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

    public void XmppRegistration() {
        Count++;
        Log.d("xmpp: ", "Registration count: "+Count);
        if(Count%4==0){
            Log.d("xmpp: ", "Registration time out");
            mProgressDialog.hide();
            // after 3rd attempt
            _loginButton.setEnabled(true);
            _loginButton.setText(getString(R.string.try_again));
            toast(getString(R.string.xmpp_signup_error));
        }
        else {
            try {
                _loginButton.setEnabled(false);
                userName = _usernameText.getText().toString();
                password = _passwordText.getText().toString();
                Intent intent = new Intent(getBaseContext(), ConnectXmpp.class);
                intent.putExtra("user", userName.toLowerCase());
                intent.putExtra("pwd", password);
                intent.putExtra("code", "4");
                startService(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xmpp: ", "UI:: Registration Error: " + e.getMessage());
                XmppRegistration();
            }
        }
    }

    // create snack bar
    public void initSnackBar() {
        // show no internet image
        snackbar = Snackbar
                .make(mLoginMainContent, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
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
                    if(!snackbar.isShown()){
                        snackbar.show();
                    }
                    Log.d("xmpp: ", "connectivity1:: "+internetStatus);
                    internetConnected=false;
                }
            }else{
                if(!internetConnected){
                    if(snackbar.isShown()){
                        snackbar.dismiss();
                    }
                    Log.d("xmpp: ", "connectivity2:: "+internetStatus);
                    internetConnected=true;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // register run time internet checking broadcast receiver
        registerInternetCheckReceiver();

        // Initializing Internet Check
        if (NetworkChecking.hasConnection(Login.this)) {
            isWiFiConnected = true;
            if (snackbar.isShown()){
                snackbar.dismiss();
            }
        } else {
            // if there is no internet
            isWiFiConnected = false;
            // Show Snack bar message
            snackbar.show();
        }
        // register receiver for connection
        LocalBroadcastManager.getInstance(Login.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("signin"));

        // register receiver for connection error
        LocalBroadcastManager.getInstance(Login.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("connectionerror"));

        // register receiver for registration error
        LocalBroadcastManager.getInstance(Login.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("signuperror"));

        // register receiver for login error
        LocalBroadcastManager.getInstance(Login.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("signinerror"));
    }

    @Override
    protected void onPause() {
        // unregister all receiver
        unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(Login.this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

}
