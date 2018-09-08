package anindya.sample.smackchat.activities;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jivesoftware.smack.XMPPConnection;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.services.XmppService;
import anindya.sample.smackchat.utils.NetworkChecking;
import anindya.sample.smackchat.utils.PrefManager;


public class LoginActivity extends BaseActivity {

    String userName;
    String password;
    int Count = 0;

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private FloatingActionButton fab;



    @Override
    public void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, XmppService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(mBroadcastReceiver, new IntentFilter("login"));
    }

    @Override
    public void onStop() {
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        LocalBroadcastManager.getInstance(LoginActivity.this).unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        initView();
        setListener();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("login")) {
                    mService.setUpReceiver();
                    PrefManager.setUserLoggedData(LoginActivity.this, "Yes");
                    PrefManager.setUserName(LoginActivity.this, userName.toLowerCase());
                    PrefManager.setUserPassword(LoginActivity.this, password);
                    hideDialog();
                    startHomeActivity();
                }
            }
        };

    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        fab = findViewById(R.id.fab);
    }

    private void setListener() {

        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                if (NetworkChecking.hasConnection(mContext)) {
                    login();
                } else {
                    hideDialog();
                    toast("No Internet");
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());
            }
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        getUserInfo();
        xmppLogin();
    }

    private void startHomeActivity() {
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
        Intent i2 = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i2, oc2.toBundle());
        finish();
    }

    public void onLoginFailed() {
        hideDialog();
        toast(getString(R.string.login_failed_message));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!btGo.isEnabled()) btGo.setEnabled(true);
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || username.length() < 3 || username.length() > 64) {
            etUsername.setError(getString(R.string.valid_username));
            valid = false;
        } else if (username.contains(" ") || username.contains("@") || username.contains("#")) {
            etUsername.setError(getString(R.string.valid_username2));
            valid = false;
        } else if (username.equals("samsung")) {
            etUsername.setError(getString(R.string.valid_username3));
            valid = false;
        } else {
            etUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 || password.length() > 32) {
            etPassword.setError(getString(R.string.valid_password));
            valid = false;
        } else if (password.contains(" ")) {
            etPassword.setError(getString(R.string.valid_username2));
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    // get user login info from shared preference
    public void getUserInfo() {
        btGo.setEnabled(false);
        btGo.setText(getString(R.string.try_again));
        userName = etUsername.getText().toString().trim();
        password = etUsername.getText().toString().trim();
    }

    public void xmppLogin() {
        Count++;
        Log.d("xmpp: ", "Login count: " + Count);
        if (Count % 4 == 0) {
            Log.d("xmpp: ", "Login time out");
            hideDialog();
            // after 3rd attempt
            onLoginFailed();
        } else {
            try {
                mService.initConnection(userName, password, new XmppService.onConnectionResponse() {
                    @Override
                    public void onConnected(XMPPConnection connection) {
                        if(!connection.isAuthenticated()){
                            mService.login(userName, password, new XmppService.onLoginResponse() {
                                @Override
                                public void onLoggedIn(boolean isLogged) {
                                    if(!isLogged) onLoginFailed();
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

    // back button press method
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
