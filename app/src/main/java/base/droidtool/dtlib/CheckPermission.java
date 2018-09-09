package base.droidtool.dtlib;


import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;


public class CheckPermission {

    DroidTool dt;

    public CheckPermission(DroidTool dt) {
        this.dt = dt;
    }

    public interface permissionCheckListener {
        void onChecked(boolean hasPermitted);
    }

    public void initPermissionListener(String[] listOfPermissions, permissionCheckListener listener){
        final permissionCheckListener customListener = listener;
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if(customListener!=null) customListener.onChecked(true);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                if(customListener!=null) customListener.onChecked(false);
                dt.alert.showWarningWithOneButton(dt.gStr(R.string.permission_failed));
                dt.alert.setAlertListener(new SweetAlert.AlertListener() {
                    @Override
                    public void onAlertClick(boolean isCancel) {
                        ((android.app.Activity) dt.c).finish();
                    }
                });
            }
        };
        checkPermission(permissionlistener, listOfPermissions);
    }

    private void checkPermission(PermissionListener permissionListener, String[] listOfPermissions){
        TedPermission.with(dt.c)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(dt.gStr(R.string.permission_message_twice))
                .setPermissions(listOfPermissions)
                .check();
    }

}
