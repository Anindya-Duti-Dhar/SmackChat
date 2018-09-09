package base.droidtool.dtlib;

import android.graphics.Color;
import android.text.TextUtils;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;

public class SweetAlert {

    DroidTool dt;
    public SweetAlertDialog alert, progress;
    public AlertListener customAlertClickListener = null;

    public SweetAlert(DroidTool droidTool) {
        dt = droidTool;
    }

    public interface AlertListener {
        void onAlertClick(boolean isCancel);
    }

    public void showError(String title, String message, String okText){
        alert = new SweetAlertDialog(dt.c, SweetAlertDialog.ERROR_TYPE);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.setTitleText(title);
        alert.setContentText(message);
        alert.setConfirmText(okText);
        alert.show();
    }

    public SweetAlertDialog showProgress(String title){
        progress = new SweetAlertDialog(dt.c, SweetAlertDialog.PROGRESS_TYPE);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.getProgressHelper().setBarColor(Color.parseColor("#137EF0"));
        progress.setTitleText(title);
        progress.show();
        return progress;
    }

    public void showWarning(String message){
        alert = new SweetAlertDialog(dt.c, SweetAlertDialog.WARNING_TYPE);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.setTitleText(dt.gStr(R.string.common_warning_title));
        alert.setContentText(message);
        alert.setCancelText(dt.gStr(R.string.no));
        alert.setConfirmText(dt.gStr(R.string.yes));
        alert.show();
    }

    public void showWarningWithOneButton(String message){
        alert = new SweetAlertDialog(dt.c, SweetAlertDialog.WARNING_TYPE);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.showCancelButton(false);
        alert.setTitleText(dt.gStr(R.string.common_warning_title));
        alert.setContentText(message);
        alert.setConfirmText(dt.gStr(R.string.ok));
        alert.show();
    }

    public void showWarningWithOneButton(String title, String message){
        alert = new SweetAlertDialog(dt.c, SweetAlertDialog.WARNING_TYPE);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.showCancelButton(false);
        alert.setTitleText(title);
        alert.setContentText(message);
        alert.setConfirmText(dt.gStr(R.string.ok));
        alert.show();
    }

    public void showSuccess(String title, String message, String okText){
        alert = new SweetAlertDialog(dt.c, SweetAlertDialog.SUCCESS_TYPE);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.setTitleText(title);
        alert.setContentText(message);
        alert.setConfirmText(okText);
        alert.show();
    }

    public void showGeneral(String title, String message, String okText, String cancelText){
        alert = new SweetAlertDialog(dt.c, SweetAlertDialog.NORMAL_TYPE);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.setTitleText(title);
        alert.setContentText(message);
        alert.setConfirmText(okText);
        if(!TextUtils.isEmpty(cancelText))alert.setCancelText(cancelText);
        alert.show();
    }

    // hide progress dialog
    public void hideDialog(SweetAlertDialog alert) {
        // if previously showing it then hide that
        if (alert.isShowing()) {
            alert.dismiss();
        }
    }

    // bind Listener of dialog
    public void setAlertListener(AlertListener alertClickListener) {
        this.customAlertClickListener = alertClickListener;
        alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                customAlertClickListener.onAlertClick(false);
            }
        });
        alert.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                customAlertClickListener.onAlertClick(true);
            }
        });
    }
}

