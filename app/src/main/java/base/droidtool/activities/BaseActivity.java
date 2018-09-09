package base.droidtool.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.amitshekhar.DebugDB;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.services.XmppService;
import anindya.sample.smackchat.utils.LocalBinder;
import base.droidtool.DroidTool;
import base.droidtool.dtlib.ExceptionHandler;


public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected DroidTool dt;
    protected ProgressDialog mProgressDialog;

    protected BroadcastReceiver mBroadcastReceiver;

    protected XmppService mService;
    protected boolean mBounded;
    protected final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            mService = ((LocalBinder<XmppService>) service).getService();
            mBounded = true;
            Log.d("xmpp:", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.d("xmpp:", "onServiceDisconnected");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get db log browser address
        Log.d("xmpp: ","DB Browser" + DebugDB.getAddressLog());
        // init crash detector
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    public void initProgressDialog(String message){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }

    public void toast(final String text) {
        BaseActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void hideDialog() {
        BaseActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mProgressDialog.isShowing())mProgressDialog.hide();
            }
        });
    }

    public void showDialog() {
        BaseActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!mProgressDialog.isShowing())mProgressDialog.show();
            }
        });
    }

    public void setStatusBarColor(int colorRes){
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(colorRes);
    }

    public void register(Context context, int activityTitle) {
        mContext = context;
        dt = new DroidTool(mContext);
        if (activityTitle > 0) {
            setupToolbar(activityTitle);
        }
    }

    // bind toolbar
    public void setupToolbar(int titleResourceId) {
        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(titleResourceId);

        // bind back arrow in toolbar
        ActionBar ctBr = getSupportActionBar();
        ctBr.setDisplayHomeAsUpEnabled(true);
        ctBr.setDisplayShowHomeEnabled(true);
        ctBr.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);
    }

}
