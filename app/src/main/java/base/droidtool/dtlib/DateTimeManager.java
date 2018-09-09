package base.droidtool.dtlib;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;


public class DateTimeManager {

    // context variable
    private Context mContext;
    // valid list of months name
    public List<String> monthList = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));;
    // Month setup listener variable
    private onMonthSetListener myMonthSetListener = null;
    // Date setup listener variable
    private onDateSetListener myDateSetListener = null;
    // Date setup listener variable
    private onTimeSetListener myTimeSetListener = null;
    // final string of selected date
    private String mSelectedDate;
    // final string of selected time
    private String mSelectedTime;
    // empty constructor
    public DateTimeManager(Context context) {
        mContext = context;
    }

    DroidTool dt;
    public DateTimeManager(DroidTool droidTool) {
        dt = droidTool;
        mContext = dt.c;
    }

    // Month setup listener interface
    public interface onMonthSetListener {
        void onMonthSet(String date, TextView tvDate);
    }

    // Month setup response listener
    public void monthSetResponseListener(onMonthSetListener listener) {
        this.myMonthSetListener = listener;
    }

    MonthYearPickerDialog monthPicker;

    private String[] monthNameList = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

    // call TextView along with it's listener to show date picker dialog
    public TextView monthPicker(final int resId){

        // init TextView
        final TextView tvMonth = dt.ui.textView.getObject(resId);

        DateFormat tvDateFormat = new SimpleDateFormat("MMMM");
        Date date = new Date();
        String tvSelectedDate = tvDateFormat.format(date);
        tvMonth.setText(tvSelectedDate);

        monthPicker = new MonthYearPickerDialog();

        monthPicker.setListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String date = String.valueOf(dayOfMonth);
                String month = String.valueOf(monthOfYear);
                if(dayOfMonth<10) date = "0"+dayOfMonth;
                if(monthOfYear<10) month = "0"+monthOfYear;
                String mSelectedActualDate = year + "-" + month + "-" + date;

                tvMonth.setText(monthNameList[monthOfYear-1]);
                // integrate with our custom listener to send result to the activity
                if (myMonthSetListener != null) myMonthSetListener.onMonthSet(mSelectedActualDate, tvMonth);
            }
        });

        // init listener of this edit text
        tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call picker
                monthPicker.show(((FragmentActivity)(Activity)dt.c).getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        return tvMonth;
    }

    // Date setup listener interface
    public interface onDateSetListener {
        void onDateSet(String date, EditText etDate);
    }

    // Date setup response listener
    public void DateSetResponseListener(onDateSetListener listener) {
        this.myDateSetListener = listener;
    }

    // 1.1.1
    // call Edit text along with it's listener to show date picker dialog
    public EditText datePicker(final int resID, final boolean hasClearButton, final String maxDate, final String minDate, final String pickedDate, final int hintRes){

        // init edit text
        final EditText etDate = dt.ui.editText.getRes(resID);

        // set text
        etDate.setText(pickedDate);
        etDate.setHint(hintRes);

        // init listener of this edit text
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call date picker
                DatePickerManager(etDate, hasClearButton, maxDate, minDate, etDate.getText().toString().trim(), hintRes);
            }
        });
        return etDate;
    }

    // 1.1.2
    // call Edit text along with it's listener to show date picker dialog
    public EditText datePicker(final View view, final int resID, final boolean hasClearButton, final String maxDate, final String minDate, final String pickedDate, final int hintRes){

        // init edit text
        //final EditText etDate = dt.ui.editText.getRes(resID);
        final EditText etDate = (EditText) view.findViewById(resID);

        // set text
        etDate.setText(pickedDate);
        etDate.setHint(hintRes);

        // init listener of this edit text
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call date picker
                DatePickerManager(etDate, hasClearButton, maxDate, minDate, etDate.getText().toString().trim(), hintRes);
            }
        });
        return etDate;
    }

    // 1.2
    // Date picker main method
    public void DatePickerManager(final EditText etDate, boolean hasClearButton, String maxDate, String minDate, String pickedDate, final int hintRes) {
        Activity activity = (Activity) mContext;
        Calendar now;
        if(!TextUtils.isEmpty(pickedDate)) now = calenderFromString(pickedDate);
        else now = Calendar.getInstance();
        ClearableDatePicker dpd = ClearableDatePicker.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                        String date = String.valueOf(dayOfMonth);
                        String month = String.valueOf((monthOfYear + 1));
                        if(dayOfMonth<10) date = "0"+dayOfMonth;
                        if(monthOfYear<10) month = "0"+(monthOfYear + 1);

                        mSelectedDate = date + "-" + getNameOfTheMonth(monthOfYear + 1) + "-" + year;
                        String mSelectedActualDate = date + "-" + month + "-" + year;

                        etDate.setText(mSelectedDate);
                        etDate.setHint(hintRes);
                        // integrate with our custom listener to send result to the activity
                        if (myDateSetListener != null) myDateSetListener.onDateSet(mSelectedActualDate, etDate);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // listener for clear action in date picker dialog
        if(hasClearButton){
            dpd.setOnDateClearedListener(new ClearableDatePicker.OnDateClearedListener() {
                @Override
                public void onDateCleared(ClearableDatePicker view) {
                    etDate.setText("");
                    etDate.setHint(hintRes);
                    // integrate with our custom listener to send result to the activity
                    if (myDateSetListener != null) {
                        myDateSetListener.onDateSet("", etDate);
                    }
                }
            });
        }

        //check if has date max validator
        if(!TextUtils.isEmpty(maxDate)) dpd.setMaxDate(getValidDate(maxDate));

        //check if has date min validator
        if(!TextUtils.isEmpty(minDate)) dpd.setMinDate(getValidDate(minDate));

        // bind text and color for ok button
        dpd.setOkText(mContext.getString(R.string.select));
        dpd.setOkColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind text and color for cancel button
        dpd.setCancelText(mContext.getString(R.string.cancel));
        dpd.setCancelColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind color
        dpd.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        // finally show the dialog
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    // 1.3
    // get exact name of the selected month
    public String getNameOfTheMonth(int monthNo) {
        if (monthNo != 0)
            return monthList.get(monthNo - 1);
        return null;
    }

    // 1.4
    // get Valid date
    public Calendar getValidDate(String date) {
        Calendar output = calenderFromString(date);
        output.set(Calendar.YEAR, output.get(Calendar.YEAR));
        output.set(Calendar.DAY_OF_MONTH, output.get(Calendar.DAY_OF_MONTH));
        output.set(Calendar.MONTH, output.get(Calendar.MONTH));
        return output;
    }

    // 1.5
    // get calendar object from server given string
    public Calendar calenderFromString(String stringDate) {
        Calendar calendar = null;
        if(!TextUtils.isEmpty(stringDate)){
            SimpleDateFormat format = null;
            if(stringDate.matches(".*[a-zA-Z]+.*")) format = new SimpleDateFormat("dd-MMM-yyyy");
            else format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = format.parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
        }
        return calendar;
    }

    public String fineFormatDateFromString(String string) {
        Calendar calendar = null;
        String stringDate = "";
        if(!TextUtils.isEmpty(string)){
            SimpleDateFormat format = null;
            if(string.matches(".*[a-zA-Z]+.*")) {
                format = new SimpleDateFormat("dd-MMM-yyyy");
            }
            else {
                format = new SimpleDateFormat("yyyy-MM-dd");
            }
            Date date = null;
            try {
                date = format.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
            if(calendar!=null){
                calendar.add(Calendar.DATE, 0);  // number of days to add
                calendar.add(Calendar.MONTH, 0);// number of months to add
                calendar.add(Calendar.YEAR, 0);  // number of years to add
                stringDate = format2.format(calendar.getTime());
            }
        }
        return stringDate;
    }

    public String sqliteDateFromString(String string) {
        Calendar calendar = null;
        String stringDate = "";
        if(!TextUtils.isEmpty(string)){
            SimpleDateFormat format = null;

            if(string.matches(".*[a-zA-Z]+.*")) {
                format = new SimpleDateFormat("dd-MMM-yyyy");
            }
            else {
                format = new SimpleDateFormat("yyyy-MM-dd");
            }

            Date date = null;
            try {
                date = format.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }

            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

            if(calendar!=null){
                calendar.add(Calendar.DATE, 0);  // number of days to add
                calendar.add(Calendar.MONTH, 0);// number of months to add
                calendar.add(Calendar.YEAR, 0);  // number of years to add
                stringDate = format2.format(calendar.getTime());
            }
        }
        return stringDate;
    }

    // Time setup listener interface
    public interface onTimeSetListener {
        void onTimeSet(String time, EditText etTime);
    }

    // Time setup response listener
    public void TimeSetResponseListener(onTimeSetListener listener) {
        this.myTimeSetListener = listener;
    }

    // 2.1.1
    // call Edit text along with it's listener to show time picker dialog
    public EditText timePicker(int resID, final boolean hasClearButton, final String maxTime, final String minTime, final String pickedTime){
        // init edit text
        final EditText etTime = dt.ui.editText.getRes(resID);
        // set text
        etTime.setText(pickedTime);
        etTime.setHint(R.string.select);
        // init listener of this edit text
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call time picker
                TimePickerManager(etTime, hasClearButton, maxTime, minTime, etTime.getText().toString().trim());
            }
        });
        return etTime;
    }

    // 2.1.2
    // call Edit text along with it's listener to show time picker dialog
    public EditText timePicker(View view, int resID, final boolean hasClearButton, final String maxTime, final String minTime, final String pickedTime){
        // init edit text
        final EditText etTime = (EditText) view.findViewById(resID);
        // set text
        etTime.setText(pickedTime);
        // init listener of this edit text
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call time picker
                TimePickerManager(etTime, hasClearButton, maxTime, minTime, etTime.getText().toString().trim());
            }
        });
        return etTime;
    }

    // 2.2
    // Time picker main method
    public void TimePickerManager(final EditText etTime, boolean hasClearButton, String maxTime, String minTime, String pickedTime) {
        Activity activity = (Activity) mContext;
        Calendar now;
        if(!TextUtils.isEmpty(pickedTime)) now = timeCalenderFromString(pickedTime);
        else now = Calendar.getInstance();

        ClearableTimePicker tpd = ClearableTimePicker.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        String hourString = getExactHour(hourOfDay) < 10 ? "0" + getExactHour(hourOfDay) : "" + getExactHour(hourOfDay);
                        String minuteString = minute < 10 ? "0" + minute : "" + minute;
                        String secondString = second < 10 ? "0" + second : "" + second;
                        mSelectedTime = hourString + ":" + minuteString + " " + getTimeFormat(hourOfDay);
                        String mSelectedActualTime = hourOfDay + "-" + minute + "-" + second;
                        etTime.setText(mSelectedTime);
                        // integrate with our custom listener to send result to the activity
                        if (myTimeSetListener != null) {
                            myTimeSetListener.onTimeSet(mSelectedTime, etTime);
                        }
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND),
                false
        );

        // listener for clear action in time picker dialog
        if(hasClearButton){
            tpd.setOnTimeClearedListener(new ClearableTimePicker.OnTimeClearedListener() {
                @Override
                public void onTimeCleared(ClearableTimePicker view) {
                    etTime.setText("");
                    // integrate with our custom listener to send result to the activity
                    if (myTimeSetListener != null) {
                        myTimeSetListener.onTimeSet("", etTime);
                    }
                }
            });
        }

        //check if has time max validator
        if(!TextUtils.isEmpty(maxTime)) tpd.setMaxTime(validTime(maxTime));

        //check if has time min validator
        if(!TextUtils.isEmpty(minTime)) tpd.setMinTime(validTime(minTime));

        // bind text and color for ok button
        tpd.setOkText(mContext.getString(R.string.select));
        tpd.setOkColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind text and color for cancel button
        tpd.setCancelText(mContext.getString(R.string.cancel));
        tpd.setCancelColor(mContext.getResources().getColor(R.color.colorPrimary));
        // bind color
        tpd.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        // finally show the dialog
        tpd.show(activity.getFragmentManager(), "Timepickerdialog");
    }

    // 2.3
    // calender from time string
    public Calendar timeCalenderFromString(String stringTime){
        Calendar calendar = null;
        if(!TextUtils.isEmpty(stringTime)){
            SimpleDateFormat format = null;
            if(stringTime.matches(".*[a-zA-Z]+.*")) format = new SimpleDateFormat("h:mm a");
            else format = new SimpleDateFormat("h:mm a");
            Date date = null;
            try {
                date = format.parse(stringTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
        }
        return calendar;
    }

     // 2.4
    // time point from string
    public Timepoint validTime(String time) {
        Calendar output = timeCalenderFromString(time);
        int hour = output.get(Calendar.HOUR_OF_DAY);
        int minute = output.get(Calendar.MINUTE);
        int second = output.get(Calendar.SECOND);
        return new Timepoint(hour, minute, second);
    }

    // 2.5
    // get time format for time picker
    public String getTimeFormat(int hourOfDay) {
        String amOrPm = "am";
        if (hourOfDay >= 12) amOrPm = "pm";
        return amOrPm;
    }

    // 2.6
    // get exact hour of time picker
    public int getExactHour(int hourOfDay) {
        int exactHour = hourOfDay;
        if (hourOfDay > 12) exactHour = hourOfDay - 12;
        return exactHour;
    }

    public String getAge(int day, int month, int year){
        String ageYear;
        String ageMonth;
        String ageDay;
        LocalDate birthday = new LocalDate(year, month, day);
        LocalDate now = new LocalDate();//Today's date
        Period period = new Period(birthday, now, PeriodType.yearMonthDay());
        // years
        if(period.getYears()<10 && period.getYears()>=0){
            ageYear = "0"+period.getYears();
        }
        else if(period.getYears()<0){
            ageYear = "00";
        }
        else{
            ageYear = String.valueOf(period.getYears());
        }
        // months
        if(period.getMonths()<10 && period.getMonths()>=0){
            ageMonth = "0"+period.getMonths();
        }
        else if(period.getMonths()<0){
            ageMonth = "00";
        }
        else{
            ageMonth = String.valueOf(period.getMonths());
        }
        // days
        if(period.getDays()<10 && period.getDays()>=0){
            ageDay = "0"+period.getDays();
        }
        else if(period.getDays()<0){
            ageDay = "00";
        }
        else{
            ageDay = String.valueOf(period.getDays());
        }
        String actualAge = ageYear+"-"+ageMonth+"-"+ageDay;
        return actualAge;
    }

    // get edd date in string
    public String getEDDFromCalender(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String stringDate = "";
        if(calendar!=null){
            calendar.add(Calendar.DATE, 7);  // number of days to add
            calendar.add(Calendar.MONTH, 8);  // number of months to add
            calendar.add(Calendar.YEAR, 0);  // number of years to add
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    // get Date Of Birth string from day, month, year
    public String getDateOfBirthFromSplitAgeStrings(String yearString, String monthString, String dayString) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String stringDate = null;
        int day = 0, month = 0, year = 0;
        if(!TextUtils.isEmpty(dayString)){
            day = Integer.parseInt(dayString);
        }
        if(!TextUtils.isEmpty(monthString)){
            month = Integer.parseInt(monthString);
        }
        if(!TextUtils.isEmpty(yearString)){
            year = Integer.parseInt(yearString);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, - day);  // number of days to subtract
        calendar.add(Calendar.MONTH, - month);  // number of months to subtract
        calendar.add(Calendar.YEAR, - year);  // number of years to subtract

        if (calendar != null) {
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    public String getStringDateByAddingWithCurrentDate(int day, int month, int year){
        String stringDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(calendar!=null){
            calendar.add(Calendar.DATE, day);  // number of days to add
            calendar.add(Calendar.MONTH, month);  // number of months to add
            calendar.add(Calendar.YEAR, year);  // number of years to add
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    public String getStringDateBySubtractingWithCurrentDate(int day, int month, int year){
        String stringDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(calendar!=null){
            calendar.add(Calendar.DATE, - day);  // number of days to subtract
            calendar.add(Calendar.MONTH, - month);  // number of months to subtract
            calendar.add(Calendar.YEAR, - year);  // number of years to subtract
            stringDate = format.format(calendar.getTime());
        }
        return stringDate;
    }

    // get Age in String
    public String getAgeStringFromStringDate(String date) {
        String ageInString = "";
        String ageInYears = "";
        if (!TextUtils.isEmpty(date)) {
            List<String> dateList = Arrays.asList(date.split("-"));
            if(date.matches(".*[a-zA-Z]+.*")) {
                ageInYears = getAge(Integer.parseInt(dateList.get(0)), (monthList.indexOf(dateList.get(1))+1), Integer.parseInt(dateList.get(2)));
            }
            else{
                ageInYears = getAge(Integer.parseInt(dateList.get(2)), Integer.parseInt(dateList.get(1)), Integer.parseInt(dateList.get(0)));
            }
            List<String> ageList = Arrays.asList(ageInYears.split("-"));
            String year = "";  String month = ""; String day = "";
            if(!ageList.get(0).equals("00")) year = ageList.get(0) + " " + dt.gStr(R.string.year) + " ";
            if(!ageList.get(1).equals("00")) month = ageList.get(1) + " " + dt.gStr(R.string.month) + " ";
            if(ageList.get(2).equals("00")){
                if(ageList.get(0).equals("00")) {
                    if(ageList.get(1).equals("00")){
                        day = ageList.get(2) + " " + dt.gStr(R.string.day);
                    }
                }
            }
            ageInString = year + month + day;
        }
        return ageInString;
    }

    // get current system date
    public String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        return currentDate;
    }

    // get current system SQLite date
    public String getSqlCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        return currentDate;
    }

    // get current system time
    public String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        Date date = new Date();
        String currentTime = timeFormat.format(date);
        return currentTime;
    }

    // get current system time
    public String getCurrentSqlTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM");
        Date date = new Date();
        String currentTime = timeFormat.format(date);
        return currentTime;
    }

    // get current system month
    public String getCurrentMonth() {
        DateFormat dateFormat = new SimpleDateFormat("MMM-yyyy");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        String currentMonth = "01-"+currentDate;
        return currentMonth;
    }

    // get system current date and time
    public String getCurrentDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
        String formattedDate = dateFormat.format(new Date()).toString();
        System.out.println(formattedDate);
        return  formattedDate;
    }

}
