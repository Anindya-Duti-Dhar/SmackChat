package anindya.sample.smackchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;


public class ContactProfileActivity extends BaseActivity {

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

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_profile);
        super.register(this, "");
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        //region Load Record Using Extras
        if (!dt.extra().isEmpty()) setupToolbar(dt.extra());
        //endregion
    }

    // back button press method
    @Override
    public void onBackPressed() {
        dt.tools.startActivity(HomeActivity.class, "");
    }

}
