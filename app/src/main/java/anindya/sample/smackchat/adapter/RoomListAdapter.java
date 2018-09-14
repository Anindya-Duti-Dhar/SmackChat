package anindya.sample.smackchat.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.activities.GroupChatActivity;
import anindya.sample.smackchat.model.RoomItem;
import base.droidtool.DroidTool;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView room_no_image, room_name, room_owner, room_participant;
        public ImageButton room_menu;
        public CardView item_card_view;

        public ViewHolder(View itemView) {
            super(itemView);
            room_no_image = (TextView) itemView.findViewById(R.id.room_no_image);
            room_name = (TextView) itemView.findViewById(R.id.room_name);
            room_owner = (TextView) itemView.findViewById(R.id.room_owner);
            room_participant = (TextView) itemView.findViewById(R.id.room_participant);
            room_menu = (ImageButton)itemView.findViewById(R.id.room_menu);
            item_card_view = (CardView) itemView.findViewById(R.id.item_card_view);
        }
    }

    private List<RoomItem> _data;
    private Context mContext;
    private DroidTool dt;

    public RoomListAdapter(DroidTool droidTool, Context context, List<RoomItem> _data) {
        this._data = _data;
        this.mContext = context;
        dt = droidTool;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.adapter_room_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final RoomItem data = _data.get(position);

        TextView room_name = viewHolder.room_name;
        TextView room_owner = viewHolder.room_owner;
        TextView room_participant = viewHolder.room_participant;
        TextView room_no_image = viewHolder.room_no_image;
        ImageButton room_menu = viewHolder.room_menu;
        CardView item_card_view = viewHolder.item_card_view;

        room_name.setText(data.getName());
        room_owner.setText(data.getOwner());
        room_participant.setText(getAllParticipant(data.getOccupants()));
        room_no_image.setText(String.valueOf(data.getName().toString().charAt(0)));

        room_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, viewHolder.room_menu);
                popup.inflate(R.menu.room_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_profile:

                                return true;
                            case R.id.action_member:

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dt.tools.startActivity(GroupChatActivity.class, data.getName());
            }
        });

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return _data.size();
    }

    public String getAllParticipant(List<String> arrayList) {
        String val = "";
        if(arrayList!=null){
            if(arrayList.size()>0){
                for (int i = 0; i < arrayList.size(); i++) {
                    val = val + arrayList.get(i) + ", ";
                }
                val.substring(0, val.length() - 1);
            } else{
                val = "no member";
            }
        }
        else {
            val = "no member";
        }
        return val;
    }
}
