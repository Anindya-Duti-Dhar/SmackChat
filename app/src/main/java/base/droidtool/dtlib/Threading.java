package base.droidtool.dtlib;

import android.os.Handler;



public class Threading {

    public void sleep(int milliSecond) {
        try {
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface onDelayListener {
        void onResume();
    }

    public void delay(int milliSecond, onDelayListener listener) {
        final onDelayListener customListener = listener;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (customListener != null) customListener.onResume();
            }
        }, milliSecond);
    }

}
