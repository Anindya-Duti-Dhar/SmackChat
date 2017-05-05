package anindya.sample.smackchat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by user on 5/5/2017.
 */

public class ChatRoom extends AppCompatActivity {

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("user");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Joining........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("join")) {
                    mProgressDialog.hide();
                    Intent intentActivity = new Intent(getBaseContext(),Chat.class );
                    intentActivity.putExtra("user",userName);
                    startActivity(intentActivity);
                    Log.d("xmpp", "successfully Joined");
                }
            }
        };

        Button joinChatRoom = (Button)findViewById(R.id.btnJoinChatRoom);
        joinChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                Intent intent = new Intent(getBaseContext(),ConnectXmpp.class );
                intent.putExtra("user", userName);
                intent.putExtra("code","1");
                startService(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        LocalBroadcastManager.getInstance(ChatRoom.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("join"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(ChatRoom.this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

}