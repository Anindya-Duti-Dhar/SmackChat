package base.droidtool.dtlib;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import anindya.sample.smackchat.R;


public class ClearableTimePicker extends TimePickerDialog {

    public static ClearableTimePicker newInstance(OnTimeSetListener callBack, int hourOfDay, int minute, int second, boolean is24Hours) {
        ClearableTimePicker ret = new ClearableTimePicker();
        ret.initialize(callBack, hourOfDay, minute, second, is24Hours);
        return ret;
    }

    private OnTimeClearedListener mOnTimeClearedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = super.onCreateView(inflater, container, state);
        LinearLayout buttonContainer = (LinearLayout) view.findViewById(
                com.wdullaer.materialdatetimepicker.R.id.mdtp_done_background);
        View clearButton = inflater.inflate(R.layout.date_picker_dialog_clear_button,
                buttonContainer, false);
        clearButton.setOnClickListener(new ClearClickListener());
        buttonContainer.addView(clearButton, 0);

        return view;
    }

    public void setOnTimeClearedListener(OnTimeClearedListener listener) {
        mOnTimeClearedListener = listener;
    }

    public OnTimeClearedListener getOnTimeClearedListener() {
        return mOnTimeClearedListener;
    }

    public interface OnTimeClearedListener {

        /**
         * @param view The view associated with this listener.
         */
        void onTimeCleared(ClearableTimePicker view);

    }

    private class ClearClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            tryVibrate();

            OnTimeClearedListener listener = getOnTimeClearedListener();
            if (listener != null) {
                listener.onTimeCleared(ClearableTimePicker.this);
            }

            dismiss();
        }

    }

}