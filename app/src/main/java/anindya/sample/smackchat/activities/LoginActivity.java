package anindya.sample.smackchat.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.XMPPConnection;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;


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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onMessageEvent(BroadcastEvent event) {
        Log.d("xmpp: ", "BroadcastEvent: " + event.item + "\nCategory: " + event.category + "\nMessage: " + event.message);
        if(event.item.equals("login")){
            mService.setUpReceiver();
            dt.pref.set("login", true);
            dt.pref.set("username", userName.toLowerCase());
            dt.pref.set("password", password);
            hideDialog();
            dt.tools.startActivity(HomeActivity.class, "");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        super.register(this, 0);
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        initView();
        setListener();
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
                if (dt.droidNet.hasConnection()) {
                    login();
                } else {
                    hideDialog();
                    dt.droidNet.internetErrorDialog();
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
                    public void onConnected(boolean isConnected, XMPPConnection connection) {
                        if(isConnected){
                            mService.login(userName, password, new XmppService.onLoginResponse() {
                                @Override
                                public void onLoggedIn(boolean isLogged) {
                                    if(!isLogged) onLoginFailed();
                                }
                            });
                        } else onLoginFailed();
                       /* if(connection!=null){
                            if(!connection.isAuthenticated()){
                                mService.login(userName, password, new XmppService.onLoginResponse() {
                                    @Override
                                    public void onLoggedIn(boolean isLogged) {
                                        if(!isLogged) onLoginFailed();
                                    }
                                });
                            }
                        } else onLoginFailed();*/
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
