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


public class Login extends AppCompatActivity {

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    public static String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("connection")) {
                    mProgressDialog.hide();
                    finish();
                    Intent intentActivity = new Intent(getBaseContext(),Chat.class );
                    intentActivity.putExtra("user",userName);
                    startActivity(intentActivity);
                    Log.d("xmpp", "successfully connected");
                }
            }
        };

        Button button = (Button)findViewById(R.id.btnlogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                try {
                    EditText userId = (EditText) findViewById(R.id.txtUser);
                    EditText userPwd = (EditText) findViewById(R.id.txtPwd);
                    userName = userId.getText().toString();
                    String passWord = userPwd.getText().toString();
                    Intent intent = new Intent(getBaseContext(),ConnectXmpp.class );
                    intent.putExtra("user",userName);
                    intent.putExtra("pwd",passWord);
                    intent.putExtra("code","0");
                    startService(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


        Button button1 = (Button)findViewById(R.id.btnRegister);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                try {
                    EditText userId = (EditText) findViewById(R.id.txtUser);
                    EditText userPwd = (EditText) findViewById(R.id.txtPwd);
                    userName = userId.getText().toString();
                    String passWord = userPwd.getText().toString();
                    Intent intent = new Intent(getBaseContext(),ConnectXmpp.class );
                    intent.putExtra("user",userName);
                    intent.putExtra("pwd",passWord);
                    intent.putExtra("code","4");
                    startService(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        LocalBroadcastManager.getInstance(Login.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("connection"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(Login.this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

}
