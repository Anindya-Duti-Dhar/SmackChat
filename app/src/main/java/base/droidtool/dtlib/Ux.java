package base.droidtool.dtlib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;
import base.droidtool.model.ListCheckBox;
import base.droidtool.model.SpinnerValue;


public class Ux {

    public HashMap<String, SpinnerValue[]> spinnerValueMap = new HashMap<>();
    public ListView listView;
    public checkbox checkbox = new checkbox();
    public radioButton radioButton = new radioButton();
    public imageView imageView = new imageView();
    public linearLayout linearLayout = new linearLayout();
    public imageButton imageButton = new imageButton();
    public textView textView = new textView();
    public editText editText = new editText();
    public spinner spinner = new spinner();
    public recyclerView recyclerView = new recyclerView();
    public fab fab = new fab();
    public modal modal;

    protected View modalView = null;
    DroidTool dt;

    public Ux(DroidTool droidTool, HashMap<String, SpinnerValue[]> spinnerValueMap, ArrayList<ListCheckBox> checkBoxArrayList) {
        dt = droidTool;
        this.spinnerValueMap = spinnerValueMap;
        listView = new ListView(dt.c, checkBoxArrayList);
        modal = new modal(dt);
    }

    public void setModalView(View view) {
        modalView = view;
    }

    public View find(int resId) {

        View view = null;
        if (modalView != null) {
            view = modalView.findViewById(resId);
        } else {
            Activity activity = ((Activity) dt.c);
            view = activity.findViewById(resId);
        }
        return view;
    }

    public interface onSpinnerChangeListener {
        void onChange(AdapterView<?> parent, View view, int position, long id);
    }

    public interface onEditTextChangeListener {
        void onChange(Editable s);
    }

    public interface onEditTextClickListener {
        void onClick(View v);
    }

    public interface onEditTextMultiClickListener {
        void onClick(View v);
    }

    public interface onChangeListener {
        void onChange(boolean b);
    }

    public class imageView {

        public ImageView getRes(int resId) {
            ImageView imageView = (ImageView) find(resId);
            return imageView;
        }

    }

    public class imageButton {

        public ImageButton getRes(int resId) {
            ImageButton imageButton = (ImageButton) find(resId);
            return imageButton;
        }

    }

    public class linearLayout {

        public LinearLayout getRes(int resId) {
            LinearLayout layout = (LinearLayout) find(resId);
            return layout;
        }

    }

    public class textView {

        public String get(int resId) {
            TextView txt = (TextView) ((Activity) dt.c).findViewById(resId);
            return txt.getText().toString();
        }

        public void set(int resId, String value) {
            TextView txt = (TextView) find(resId);
            txt.setText(value);
        }

        public void setFont(int resId, Typeface typeface){
            TextView txt = (TextView) find(resId);
            txt.setTypeface(typeface);
        }

        public TextView getObject(int resId) {
            TextView txt = (TextView) find(resId);
            return txt;
        }

        public void enable(int resId, boolean value) {
            TextView txt = (TextView) ((Activity) dt.c).findViewById(resId);
            txt.setEnabled(value);
        }

        public void clear(int resId) {
            TextView txt = (TextView) ((Activity) dt.c).findViewById(resId);
            txt.setText("");
        }

        public void color(int resId, int colorResId) {
            TextView txt = (TextView) ((Activity) dt.c).findViewById(resId);
            txt.setTextColor(dt.c.getResources().getColor(colorResId));
        }

        public String getTag(int resId) {
            TextView txt = (TextView) ((Activity) dt.c).findViewById(resId);
            return txt.getTag().toString();
        }

        public void setTag(int resId, String value) {
            TextView txt = (TextView) ((Activity) dt.c).findViewById(resId);
            txt.setTag(value);
        }
    }

    public class editText {
        public String get(int resId) {
            EditText txt = (EditText) find(resId);
            return txt.getText().toString();
        }

        public EditText getRes(int resId) {
            EditText txt = (EditText) find(resId);
            return txt;
        }

        public void set(int resId, String value) {
            EditText txt = (EditText) find(resId);
            txt.setText(value);
        }

        public void focus(int resId, boolean value) {
            EditText txt = (EditText) find(resId);
            txt.setFocusable(value);
            if (value == false) txt.clearFocus();
        }

        public void enable(int resId, boolean value) {
            EditText txt = (EditText) find(resId);
            txt.setEnabled(value);
        }

        public void clear(int resId) {
            EditText txt = (EditText) find(resId);
            txt.setText("");
        }

        public void color(int resId, int colorResId) {
            EditText txt = (EditText) find(resId);
            txt.setTextColor(dt.c.getResources().getColor(colorResId));
        }

        public String getTag(int resId) {
            String str = "";
            EditText txt = (EditText) find(resId);
            if (txt.getTag() != null) str = txt.getTag().toString();
            return str;
        }

        public void setTag(int resId, String value) {
            EditText txt = (EditText) find(resId);
            txt.setTag(value);
        }

        public void onChange(int resId, onEditTextChangeListener listener) {
            final onEditTextChangeListener customListener = listener;
           // EditText txt = (EditText) ((Activity) c).findViewById(resId);
            EditText txt = (EditText) find(resId);

            txt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    customListener.onChange(s);
                }
            });
        }

        public void onClick(int resId, final onEditTextClickListener listener) {
            EditText txt = (EditText) find(resId);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                }
            });
        }

        public void onMultiClick(String[] resId, final onEditTextMultiClickListener listener) {
            for (String resIdList: resId) {
                int resID = dt.c.getResources().getIdentifier(resIdList, "id", dt.c.getPackageName());
                EditText et = (EditText) find(resID);
                et.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
            }
        }
    }


    public class recyclerView {

        public RecyclerView getRes(int resId) {
            RecyclerView recyclerView = (RecyclerView) find(resId);
            return recyclerView;
        }

    }


    public class spinner {
        public Spinner get(int resId) {
            Spinner spinner = (Spinner) find(resId);
            return spinner;
        }

        public void enable(int resId, boolean value) {
            Spinner spinner = (Spinner) find(resId);
            spinner.setEnabled(value);
        }

        public void set(int resId, int pos) {
            ((Spinner) find(resId)).setSelection(pos);
        }

        public void set(int resId, String value, SpinnerValue[] sv) {
            ((Spinner) find(resId)).setSelection(getSpinnerValueIndex(Integer.parseInt(value), sv));
        }

        public void set(int resId, String value) {
            String ctlName = ((Activity) dt.c).getResources().getResourceEntryName(resId);
            ((Spinner) find(resId)).setSelection(getSpinnerValueIndex(Integer.parseInt(value), spinnerValueMap.get(ctlName)));
        }

        public Spinner bind(String controlName, SpinnerValue[] spinnerValue) {
            int resID = dt.c.getResources().getIdentifier(controlName, "id", dt.c.getPackageName());
            Spinner spinner = (Spinner) ((Activity) dt.c).findViewById(resID);

            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(dt.c, android.R.layout.simple_spinner_item, spinnerValue);
            spinner.setAdapter(spinnerArrayAdapter);

            spinnerValueMap.put(controlName, spinnerValue);
            return spinner;
        }

        public Spinner bind(int resID, SpinnerValue[] spinnerValue) {
            Spinner spinner = (Spinner) find(resID);

            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(dt.c, R.layout.spinner_drop, spinnerValue); //android.R.layout.simple_spinner_item
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);

            String fieldName = dt.c.getResources().getResourceEntryName(resID);
            spinnerValueMap.put(fieldName, spinnerValue);
            return spinner;
        }

        public String getText(String valueText, SpinnerValue[] spinnerValue) {
            String sText = "";
            for (int i = 0; i < spinnerValue.length; i++) {
                if (spinnerValue[i].valueText.equalsIgnoreCase(valueText)) {
                    sText = spinnerValue[i].displayText;
                }
            }
            return sText;
        }

        public String getValue(String displayText, SpinnerValue[] spinnerValue) {
            for (int i = 0; i < spinnerValue.length; i++) {
                if (spinnerValue[i].displayText.equalsIgnoreCase(displayText)) {
                    return spinnerValue[i].valueText;
                }
            }
            return "";
        }

        public String getValue(int resId) {
            Spinner spinner = (Spinner) find(resId);
            SpinnerValue sv = (SpinnerValue) spinner.getSelectedItem();
            return sv == null ? "" : sv.valueText;
        }

        public String getText(int resId) {
            Spinner spinner = (Spinner) find(resId);
            SpinnerValue sv = (SpinnerValue) spinner.getSelectedItem();
            return sv == null ? "" : sv.displayText;
        }

        public String setValue(String displayText, SpinnerValue[] spinnerValue) {
            for (int i = 0; i < spinnerValue.length; i++) {
                if (spinnerValue[i].displayText.equalsIgnoreCase(displayText)) {
                    return spinnerValue[i].valueText;
                }
            }
            return "";
        }


/*        private View find(int resId) {
            View view = null;
            if (modalView != null) {
                view = modalView.findViewById(resId);
            } else {
                Activity activity = ((Activity) c);
                view = activity.findViewById(resId);
            }
            return view;
        }*/

        public void onChange(int spinnerResID, onSpinnerChangeListener listener) {
            final onSpinnerChangeListener customListener = listener;

            Spinner sp = (Spinner) find(spinnerResID);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    customListener.onChange(parent, view, position, id);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        public void cascadeBind(final int position, final int spinnerResID, String value, final SpinnerValue[][] sv) {
            bind(spinnerResID, sv[position]);
            if (!value.isEmpty())
                ((Spinner) find(spinnerResID)).setSelection(getSpinnerValueIndex(Integer.parseInt(value), sv[position]));

        }

        public void cascadeBind(final int position, final int spinnerResID, String value, final SpinnerValue[] sv) {
            bind(spinnerResID, sv);
            if (!value.isEmpty())
                ((Spinner) find(spinnerResID)).setSelection(getSpinnerValueIndex(Integer.parseInt(value), sv));

        }

        public int getSpinnerValueIndex(int val, SpinnerValue[] spinnerValue) {
            int iVal = -1;
            for (int i = 0; i < spinnerValue.length; i++) {
                if (Integer.parseInt(spinnerValue[i].valueText) == val) {
                    iVal = i;
                }
            }
            return iVal;
        }

        public List<SpinnerValue> getSpinnerArrVlaue(String controlName, int controlCount) {
            List<SpinnerValue> spinnerValues = new ArrayList<SpinnerValue>();
            for (int i = 1; i <= controlCount; i++) {
                String cName = controlName + i;
                int resID = dt.c.getResources().getIdentifier(cName, "id", dt.c.getPackageName());
                Spinner spinner = (Spinner) ((Activity) dt.c).findViewById(resID);
                SpinnerValue value = (SpinnerValue) spinner.getSelectedItem();
                spinnerValues.add(value);
            }
            return spinnerValues;
        }
    }

    public class fab {
        public void setImage(int resID, int imageResId) {
            FloatingActionButton fabButton = (FloatingActionButton) ((Activity) dt.c).findViewById(resID);
            fabButton.setImageDrawable(dt.c.getResources().getDrawable(imageResId));
        }

        public void visible(int resID, boolean val) {
            FloatingActionButton fabButton = (FloatingActionButton) ((Activity) dt.c).findViewById(resID);
            if (val) fabButton.setVisibility(View.VISIBLE);
            else fabButton.setVisibility(View.INVISIBLE);
        }

        public FloatingActionButton get(int resId) {
            FloatingActionButton fabButton = (FloatingActionButton) find(resId);
            return fabButton;
        }
    }

    // List view access control
    public class ListView {

        public ItemList itemList;
        public CheckList checkList;
        public SearchList searchList;

        public ListView(Context c) {
            itemList = new ItemList(dt);
            searchList = new SearchList(c);
        }

        public ListView(Context c, ArrayList<ListCheckBox> checkBoxArrayList) {
            itemList = new ItemList(dt);
            checkList = new CheckList(c);
            searchList = new SearchList(c);
        }
    }

    public interface onModalPositiveButtonClickListener {
        void onClick(DialogInterface dialog, int id);
    }

    public interface onBottomSheetMenuClick {
        void onClick(int position);
    }

    public interface onModalListItemClickListener {
        void onClick(DialogInterface dialog, int id);
    }

    public class modal {

        DroidTool dt;

        public modal(DroidTool droidTool) {
            dt = droidTool;
        }

        public View showModal(int layout, String title, onModalPositiveButtonClickListener listener) {
            final onModalPositiveButtonClickListener customListener = listener;
            final View modalView = View.inflate(dt.c, layout, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(dt.c);
            builder.setTitle(title)
                    .setView(modalView)
                    .setCancelable(true)
                    .setNegativeButton(dt.gStr(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.setPositiveButton(dt.gStr(R.string.select), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    customListener.onClick(dialog, id);
                }
            });

            builder.show();
            return modalView;
        }

        public void buttonLessSingleChoiceModal(String title, String[] listItems, onModalListItemClickListener listener) {

            final onModalListItemClickListener customListener = listener;

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(dt.c);
            mBuilder.setTitle(title);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    customListener.onClick(dialog, id);
                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }

        public View inflateSubMenu(int bottomSheetLayout, int recyclerView, int adapterLayout, int columnCount, ArrayList<?> itemList, onBottomSheetMenuClick listener) {
            final onBottomSheetMenuClick customListener = listener;
            final View view = View.inflate(dt.c, bottomSheetLayout, null);
            Dialog mBottomSheetDialog = new Dialog(dt.c, R.style.MaterialDialogSheet);

            listView.itemList.set(((RecyclerView) view.findViewById(recyclerView)), itemList, adapterLayout, 0, columnCount, LinearLayout.VERTICAL, 0);
            listView.itemList.setRecyclerViewItemClickListener(new ItemList.onRecyclerViewItemClick() {
                @Override
                public void onItemRowClick(Object o, int position) {
                    customListener.onClick(position);
                }
            });

            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();

            return view;
        }

        public Dialog mBottomSheetDialog;
        public View mView;

        public View showModalDialog(int bottomSheetLayout, boolean isShown) {

            mView = View.inflate(dt.c, bottomSheetLayout, null);
            mBottomSheetDialog = new Dialog(dt.c, R.style.MaterialDialogSheet);

            mBottomSheetDialog.setContentView(mView);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

            if(isShown) mBottomSheetDialog.show();

            return mView;
        }

        public View showModalDialog(int bottomSheetLayout) {
            mView = View.inflate(dt.c, bottomSheetLayout, null);
            mBottomSheetDialog = new Dialog(dt.c, R.style.MaterialDialogSheet);
            mBottomSheetDialog.setContentView(mView);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();
            return mView;
        }

    }

    public class checkbox {

        public boolean get(int resId) {
            CheckBox checkBox = (CheckBox) ((Activity) dt.c).findViewById(resId);
            return checkBox.isChecked();
        }

        public void set(int resId, boolean value) {
            CheckBox checkBox = (CheckBox) ((Activity) dt.c).findViewById(resId);
            checkBox.setChecked(value);
        }

        public void onChange(int resId, final onChangeListener listener) {
            CheckBox checkBox = (CheckBox) ((Activity) dt.c).findViewById(resId);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null) {
                        listener.onChange(isChecked);
                    }
                }
            });
        }

    }

    public class radioButton {

        public boolean get(int resId) {
            RadioButton radioButton = (RadioButton) ((Activity) dt.c).findViewById(resId);
            return radioButton.isChecked();
        }

        public void set(int resId, boolean value) {
            RadioButton radioButton = (RadioButton) ((Activity) dt.c).findViewById(resId);
            radioButton.setChecked(value);
        }

        public void onChange(int resId, final onChangeListener listener) {
            RadioButton radioButton = (RadioButton) ((Activity) dt.c).findViewById(resId);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null) {
                        listener.onChange(isChecked);
                    }
                }
            });
        }

    }

    public void listDialog(String title, int dialogLayoutRes, int recyclerView, ArrayList<?> list, int adapterLayoutRes) {
        View dialogView = View.inflate(dt.c, dialogLayoutRes, null);

        dt.ui.listView.itemList.set(((RecyclerView) dialogView.findViewById(recyclerView)), list,
                adapterLayoutRes, 0, 1, LinearLayout.VERTICAL, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(dt.c);
        builder.setTitle(title)
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton(dt.gStr(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    /************ New Dialog View Start Region ************/

    public interface onOkClick {
        void onOk();
    }

    public interface onCancelClick {
        void onCancel();
    }

    private onOkClick customListenerOkClick = null;
    private onCancelClick customListenerCancelClick = null;

    public View popupListDialog(String title, int dialogLayoutRes, String okText, String cancelText
            , onOkClick okClickListener, onCancelClick cancelClickListener) {

        customListenerOkClick = okClickListener;
        customListenerCancelClick = cancelClickListener;

        View dialogView = View.inflate(dt.c, dialogLayoutRes, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(dt.c);
        builder.setTitle(title)
                .setView(dialogView)
                .setCancelable(true);

        if(!TextUtils.isEmpty(okText)){
            builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setModalView(null);
                    dialog.dismiss();
                    if(customListenerOkClick != null) customListenerOkClick.onOk();
                }
            });
        }

        if(!TextUtils.isEmpty(cancelText)){
            builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    setModalView(null);
                    dialog.dismiss();
                    if(customListenerCancelClick != null) customListenerCancelClick.onCancel();
                }
            });
        }

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setModalView(null);
                if(customListenerCancelClick != null) customListenerCancelClick.onCancel();
            }
        });

        builder.show();

        return dialogView;
    }

    /************ New Dialog View End Region ************/

}
