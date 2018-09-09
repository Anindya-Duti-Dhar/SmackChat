package base.droidtool.dtlib;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import anindya.sample.smackchat.R;


public class MonthYearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private DatePickerDialog.OnDateSetListener listener;

    private String[] monthList = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat stringMonthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat stringYearFormat = new SimpleDateFormat("yyyy");

        View dialog = inflater.inflate(R.layout.dialog_month_picker, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        final TextView selected_month = (TextView) dialog.findViewById(R.id.selected_month);
        selected_month.setText(stringMonthFormat.format(cal.getTime()));

        final TextView selected_year = (TextView) dialog.findViewById(R.id.selected_year);
        selected_year.setText(stringYearFormat.format(cal.getTime()));

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setWrapSelectorWheel(true);
        monthPicker.setDisplayedValues(monthList);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVa2, int newVal2) {
                selected_month.setText(monthList[newVal2-1]);
            }
        });

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVa2, int newVal2) {
                selected_year.setText(String.valueOf(newVal2));
            }
        });

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 01);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}