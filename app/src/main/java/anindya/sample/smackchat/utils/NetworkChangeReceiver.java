package anindya.sample.smackchat.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import anindya.sample.smackchat.services.ConnectXmpp;
import base.droidtool.DroidTool;

/**
 * Created by Duti on 8/31/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    DroidTool dt;

    @Override
    public void onReceive(Context context, Intent intent) {
        dt = new DroidTool(context);
        String status = dt.droidNet.getConnectivityStatusString();
        if(status.equalsIgnoreCase("Wifi enabled")||status.equalsIgnoreCase("Mobile data enabled")){
            Log.d("xmpp: ", "connectivity:: "+status);
            /*Intent pushIntent = new Intent(context, ConnectXmpp.class);
            pushIntent.putExtra("user", PrefManager.getUserName(context));
            pushIntent.putExtra("pwd", PrefManager.getUserPassword(context));
            pushIntent.putExtra("code", "122");
            context.startService(pushIntent);*/
        }
        dt.msg(status);
    }
}
