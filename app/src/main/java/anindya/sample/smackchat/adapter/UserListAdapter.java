package anindya.sample.smackchat.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.Users;
import anindya.sample.smackchat.utils.MyXMPP;
import anindya.sample.smackchat.utils.PrefManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    String mUsername, mUserEmail;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userImage;
        public ImageView userOnlineStatus;
        public TextView username, userEmail, userNoImage;

        public ViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
            userEmail = (TextView) itemView.findViewById(R.id.user_email);
            userImage = (CircleImageView) itemView.findViewById(R.id.user_image);
            userOnlineStatus = (ImageView) itemView.findViewById(R.id.user_online_status);
            userNoImage = (TextView) itemView.findViewById(R.id.user_no_image);
        }
    }

    private ArrayList<Users> _data;
    private Context mContext;
    private MyXMPP xmpp;

    public UserListAdapter(Context context, ArrayList<Users> _data) {
        this._data = _data;
        this.mContext = context;
        xmpp = new MyXMPP(mContext);
        for (int i = 0; i < _data.size(); i++) {
            Users val = _data.get(i);
            if (val.getUsername().equals(PrefManager.getUserName(context))) {
                _data.remove(i);
            }
            if (val.getUsername().equals("admin")) {
                _data.remove(i);
                break;
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
        View contactView = inflater.inflate(R.layout.adapter_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Users data = _data.get(position);

        TextView username = viewHolder.username;
        username.setText(data.getUsername());
        TextView userEmail = viewHolder.userEmail;
        userEmail.setText(data.getUsername()+"@gmail.com");

        ImageView userOnlineStatus = viewHolder.userOnlineStatus;
        userOnlineStatus.setImageResource(R.drawable.ic_online);
        String status = xmpp.userStatus(data.getUsername());
        if(status.equals("online")){
            userOnlineStatus.setVisibility(View.VISIBLE);
        } else {
            userOnlineStatus.setVisibility(View.GONE);
        }

        TextView userNoImage = viewHolder.userNoImage;
        userNoImage.setVisibility(View.VISIBLE);
        userNoImage.setText(String.valueOf(data.getUsername().toString().charAt(0)));

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return _data.size();
    }

    public int getLastItemCount() {
        return _data.size() - 1;
    }

}