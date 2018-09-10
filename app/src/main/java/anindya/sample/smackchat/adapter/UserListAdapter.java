package anindya.sample.smackchat.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.activities.ContactProfileActivity;
import anindya.sample.smackchat.model.Users;
import base.droidtool.DroidTool;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userImage;
        public ImageView userOnlineStatus;
        public TextView username, userEmail, userNoImage;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
            userEmail = (TextView) itemView.findViewById(R.id.user_email);
            userImage = (CircleImageView) itemView.findViewById(R.id.user_image);
            userOnlineStatus = (ImageView) itemView.findViewById(R.id.user_online_status);
            userNoImage = (TextView) itemView.findViewById(R.id.user_no_image);
            cardView = (CardView) itemView.findViewById(R.id.item_card_view);
        }
    }

    private List<Users> _data;
    private Context mContext;
    private DroidTool dt;

    public UserListAdapter(DroidTool droidTool, Context context, List<Users> _data) {
        this._data = _data;
        this.mContext = context;
        dt = droidTool;
        for (int i = 0; i < _data.size(); i++) {
            Users val = _data.get(i);
            if ((val.getUsername().equals(dt.pref.getString("username")))) {
                _data.remove(i);
            }
        }
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.adapter_user_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Users data = _data.get(position);

        TextView username = viewHolder.username;
        username.setText(data.getUsername());
        TextView userEmail = viewHolder.userEmail;
        userEmail.setText(data.getUsername()+"@example.com");

        ImageView userOnlineStatus = viewHolder.userOnlineStatus;
        userOnlineStatus.setImageResource(R.drawable.ic_online);
        userOnlineStatus.setVisibility(View.VISIBLE);

        TextView userNoImage = viewHolder.userNoImage;
        userNoImage.setVisibility(View.VISIBLE);
        userNoImage.setText(String.valueOf(data.getUsername().toString().charAt(0)));

        CardView cardView = viewHolder.cardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dt.tools.startActivity(ContactProfileActivity.class, data.getUsername());
            }
        });

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return _data.size();
    }

}