package anindya.sample.smackchat.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import anindya.sample.smackchat.services.ConnectXmpp;

/**
 * Created by Duti on 8/31/2018.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("xmpp", "Boot completed");
            /*Intent pushIntent = new Intent(context, ConnectXmpp.class);
            pushIntent.putExtra("user", PrefManager.getUserName(context));
            pushIntent.putExtra("pwd", PrefManager.getUserPassword(context));
            pushIntent.putExtra("code", "122");
            context.startService(pushIntent);*/
        }
    }
}
