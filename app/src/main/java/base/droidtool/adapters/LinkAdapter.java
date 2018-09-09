package base.droidtool.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;


public class LinkAdapter<T> extends RecyclerView.Adapter<LinkAdapter.ViewHolder> {

    private static Context mContext;
    private static int _deleteImageViewResId;
    private static int _iconResId;
    private int _styleResId;
    private ArrayList<T> _data;
    private DroidTool dt;

    public LinkAdapter(DroidTool droidTool, ArrayList<T> _data, int resId, int deleteImageViewResId, int iconResId) {
        this._data = _data;
        dt = droidTool;
        mContext = dt.c;
        _styleResId = resId;
        _deleteImageViewResId = deleteImageViewResId;
        _iconResId = iconResId;
    }

    public interface onAdapterItemDelete {
        void onItemDeleteClick(Object o, int pos);
    }

    public interface onAdapterItemClick {
        void onItemRowClick(Object o, int pos);
    }

    public onAdapterItemClick customListenerClick = null;
    public onAdapterItemDelete customListenerDelete = null;

    public void setClickResponseListener(onAdapterItemClick listener) {
        this.customListenerClick = listener;
    }

    public void setDeleteResponseListener(onAdapterItemDelete listener) {
        this.customListenerDelete = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Map<String, TextView> arrTextView = new HashMap<String, TextView>();
        Map<String, ImageView> arrImageView = new HashMap<String, ImageView>();
        Map<String, CheckBox> arrCheckBox = new HashMap<String, CheckBox>();
        public CardView item_card_view;
        public ImageView listIcon, listSyncStatusIcon, delete_action;

        public ViewHolder(View itemView, Object _data) {
            super(itemView);
            String sFieldName = "";
            try {
                for (Field field : ((ArrayList) _data).get(0).getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    sFieldName = field.getName().toString();
                    int resID = mContext.getResources().getIdentifier(sFieldName, "id", mContext.getPackageName());
                    makeViewMap(itemView.findViewById(resID), sFieldName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (_deleteImageViewResId != 0)
                delete_action = (ImageView) itemView.findViewById(_deleteImageViewResId);
            item_card_view = (CardView) itemView.findViewById(R.id.item_card_view);
            if (_iconResId != 0) {
                //listIcon = (ImageView) itemView.findViewById(R.id.listIcon);
                //listSyncStatusIcon = (ImageView) itemView.findViewById(R.id.listSyncStatusIcon);
            }
        }

        private void makeViewMap(View view, String sFieldName) {
            if (view instanceof ImageView) {
                arrImageView.put(sFieldName, (ImageView) view);
            } else if (view instanceof CheckBox) {
                arrCheckBox.put(sFieldName, (CheckBox) view);
            } else {
                arrTextView.put(sFieldName, (TextView) view);
            }
        }

        private String getSqlFieldType(String s) {
            String type = "";
            if (s.contains("String")) type = "TEXT";
            else if (s.contains("long")) type = "INTEGER";
            else if (s.contains("int")) type = "INTEGER";
            else if (s.contains("drawable")) type = "DRAWABLE";
            else type = "";
            return type;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(_styleResId, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView, _data);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final int iPos = position;
        final T data = _data.get(position);
        CardView item_card_view = viewHolder.item_card_view;

        if (!TextUtils.isEmpty(getFieldValue(data, "ItemSize")))
            if (Integer.parseInt(getFieldValue(data, "ItemSize")) > 0) {
                double height = Double.parseDouble(getFieldValue(data, "ScreenSize")) / Double.parseDouble(getFieldValue(data, "ItemSize"));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
                item_card_view.setLayoutParams(params);
            }

        ImageView listSyncStatusIcon = viewHolder.listSyncStatusIcon;
        if (listSyncStatusIcon != null)
            if (getFieldValue(data, "Status").equalsIgnoreCase("1")) {
                listSyncStatusIcon.setVisibility(View.VISIBLE);
            } else {
                listSyncStatusIcon.setVisibility(View.GONE);
            }

        ImageView listIcon = viewHolder.listIcon;
        if (listIcon != null) {
            listIcon.setImageResource(_iconResId);
        }

        for (Map.Entry<String, TextView> entry : viewHolder.arrTextView.entrySet()) {
            TextView tv = viewHolder.arrTextView.get(entry.getKey());
            if (tv != null) {
                if(tv.getTag() != null){
                    if((tv.getTag().toString().contains("Date"))){
                        tv.setText(dt.dateTime.fineFormatDateFromString(getFieldValue(data, entry.getKey())));
                    } else {
                        tv.setText(getFieldValue(data, entry.getKey()));
                    }
                } else {
                    tv.setText(getFieldValue(data, entry.getKey()));
                }
            }
        }

        for (Map.Entry<String, ImageView> entry : viewHolder.arrImageView.entrySet()) {
            ImageView iv = viewHolder.arrImageView.get(entry.getKey());
            if (iv != null) iv.setImageDrawable(getImageFieldValue(data, entry.getKey()));
        }

        for (final Map.Entry<String, CheckBox> entry : viewHolder.arrCheckBox.entrySet()) {
            CheckBox cb = viewHolder.arrCheckBox.get(entry.getKey());
            if (cb != null){
                if(Integer.parseInt(getFieldValue(data, entry.getKey())) == 1)cb.setChecked(true);
                else cb.setChecked(false);

                // listener for check box
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) dt.dynamic.executeMethod(data, "set"+entry.getKey(), 1);
                        else dt.dynamic.executeMethod(data, "set"+entry.getKey(), 0);
                    }
                });
            }
        }

        item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customListenerClick != null) customListenerClick.onItemRowClick(data, iPos);
            }
        });

        ImageView delete_action = viewHolder.delete_action;
        if (delete_action != null)
            delete_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customListenerDelete != null)
                        customListenerDelete.onItemDeleteClick(data, iPos);
                }
            });
    }

    private String getFieldValue(T obj, String fieldName) {
        String fieldValue = "";
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.get(obj) != null) fieldValue = field.get(obj).toString();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }

    private Drawable getImageFieldValue(T obj, String fieldName) {
        Drawable fieldValue = null;
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.get(obj) != null) fieldValue = (Drawable) field.get(obj);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }

    @Override
    public int getItemCount() {
        if (_data != null)
            return _data.size();
        else
            return 0;
    }

    public void setItems(ArrayList<T> list) {
        this._data = list;
    }
}