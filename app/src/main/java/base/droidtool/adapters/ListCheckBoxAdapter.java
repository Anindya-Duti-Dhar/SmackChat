package base.droidtool.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import base.droidtool.model.ListCheckBox;


public class ListCheckBoxAdapter extends RecyclerView.Adapter<ListCheckBoxAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

    private ArrayList<ListCheckBox> _data;
    private Context mContext;

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public ListCheckBoxAdapter(Context context, ArrayList<ListCheckBox> _data) {
        this._data = _data;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.adapter_checkbox_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final ListCheckBox data = _data.get(position);

        CheckBox checkBox = viewHolder.checkbox;
        checkBox.setText(data.getCheckboxText());
        //if true, your checkbox will be selected, else unselected
        checkBox.setChecked(data.isCheckState());

        //checkbox click event handling
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
               /* if (isChecked) {
                    makeToast("Select "+ data.getCheckboxText());
                } else {
                    makeToast("Deselect "+data.getCheckboxText());
                }*/
                data.setCheckState(isChecked);
            }
        });

    }

    // method to make toast
    public void makeToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        if (_data != null)
            return _data.size();
        else return 0;
    }

}