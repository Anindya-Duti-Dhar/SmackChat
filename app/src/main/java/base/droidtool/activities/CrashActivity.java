package base.droidtool.activities;

import android.os.Bundle;
import android.view.Window;

import anindya.sample.smackchat.R;
import base.droidtool.dtlib.SweetAlert;


public class CrashActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_crash);
        register(this, "");

        dt.alert.showError(dt.gStr(R.string.sorry), dt.gStr(R.string.crash_message), dt.gStr(R.string.ok));
        dt.alert.setAlertListener(new SweetAlert.AlertListener() {
            @Override
            public void onAlertClick(boolean isCancel) {
                finish();
            }
        });
    }
}