package base.droidtool.dtlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;


public class NetworkChecking {

    DroidTool dt;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public NetworkChecking(DroidTool droidTool) {
        dt = droidTool;
    }

    // Internet check method
    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) dt.c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    // get connectivity status at run time
    public int getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) dt.c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    // convert connectivity status into string
    public String getConnectivityStatusString() {
        int conn = getConnectivityStatus();
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    // Internet error dialog method
    public void internetErrorDialog() {
        // if there is no internet
        dt.alert.showError(dt.gStr(R.string.no_internet_title), dt.gStr(R.string.no_internet_message), dt.gStr(R.string.ok));
    }

}
