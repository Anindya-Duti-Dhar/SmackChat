package anindya.sample.smackchat.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.XMPPConnection;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;


public class RegisterActivity extends BaseActivity {

    String userName;
    String password;
    String email;
    int Count = 0;

    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private Button btGo;
    private FloatingActionButton fab;
    private CardView cvAdd;

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
        if(EventBus.getDefault().isRegistered(this))EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(BroadcastEvent event) {
        Log.d("xmpp: ", "BroadcastEvent: " + event.item + "\nCategory: " + event.category + "\nMessage: " + event.message);
        if(event.item.equals("login")){
            mService.setUpReceiver();
            dt.pref.set("login", true);
            dt.pref.set("username", userName.toLowerCase());
            dt.pref.set("password", password);
            mService.setProfileInfo(userName, email, new XmppService.onProfileSetupResponse() {
                @Override
                public void onProfileSetup(boolean isSetup) {
                    hideDialog();
                    if (!isSetup) toast("Profile Info Setup Error");
                    dt.tools.startActivity(HomeActivity.class, "");
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        super.register(this, "");
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        ShowEnterAnimation();
        initView();
        setListener();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        fab = findViewById(R.id.fab);
        btGo = findViewById(R.id.bt_go);
        cvAdd = findViewById(R.id.cv_add);
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();
                if (dt.droidNet.hasConnection()) {
                    register();
                } else {
                    hideDialog();
                    dt.droidNet.internetErrorDialog();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    public void register() {
        if (!validate()) {
            onRegisterFailed();
            return;
        }
        getUserInfo();
        xmppRegister();
    }

    public void onRegisterFailed() {
        hideDialog();
        toast(getString(R.string.login_failed_message));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!btGo.isEnabled())btGo.setEnabled(true);
                btGo.setText(getString(R.string.try_again));
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

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

        if (email.isEmpty() || email.length() < 10 || email.length() > 150) {
            etEmail.setError(getString(R.string.valid_password));
            valid = false;
        } else if (email.contains(" ")) {
            etEmail.setError(getString(R.string.valid_username2));
            valid = false;
        } else {
            etEmail.setError(null);
        }

        return valid;
    }

    // get user Register info from shared preference
    public void getUserInfo() {
        btGo.setEnabled(false);
        userName = etUsername.getText().toString().trim();
        password = etUsername.getText().toString().trim();
        email = etEmail.getText().toString().trim();
    }

    public void xmppRegister() {
        Count++;
        Log.d("xmpp: ", "Register count: " + Count);
        if (Count % 4 == 0) {
            Log.d("xmpp: ", "Register time out");
            hideDialog();
            // after 3rd attempt
            onRegisterFailed();
        } else {
            try {
                mService.initConnection(userName, password, new XmppService.onConnectionResponse() {
                    @Override
                    public void onConnected(boolean isConnected, XMPPConnection connection) {
                        if(isConnected){
                            mService.registration(userName, password, new XmppService.onRegistrationResponse() {
                                @Override
                                public void onRegistered(boolean isRegistered) {
                                    if (isRegistered) {
                                        mService.login(userName, password, new XmppService.onLoginResponse() {
                                            @Override
                                            public void onLoggedIn(boolean isLogged) {
                                                if (!isLogged) onRegisterFailed();
                                            }
                                        });
                                    } else onRegisterFailed();
                                }
                            });
                        } else onRegisterFailed();
                    }
                });
                mService.connectConnection();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xmpp: ", "UI:: Register Error: " + e.getMessage());
                xmppRegister();
            }
        }
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                //super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

}
