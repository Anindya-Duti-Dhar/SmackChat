package anindya.sample.smackchat.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.ChatItem;
import base.droidtool.DroidTool;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView my_chat_time_stamp, friend_chat_time_stamp, chat_user_without_image, chat_username, chat_user_chat, my_user_chat;
        public LinearLayout friend_chat, my_chat;
        public RelativeLayout chat_item_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            friend_chat_time_stamp = (TextView) itemView.findViewById(R.id.friend_chat_time_stamp);
            my_chat_time_stamp = (TextView) itemView.findViewById(R.id.my_chat_time_stamp);
            chat_username = (TextView) itemView.findViewById(R.id.chat_username);
            chat_user_chat = (TextView) itemView.findViewById(R.id.chat_user_chat);
            chat_user_without_image = (TextView) itemView.findViewById(R.id.chat_user_without_image);
            my_user_chat = (TextView) itemView.findViewById(R.id.my_user_chat);
            friend_chat = (LinearLayout) itemView.findViewById(R.id.friend_chat);
            my_chat = (LinearLayout) itemView.findViewById(R.id.my_chat);
            chat_item_layout = (RelativeLayout) itemView.findViewById(R.id.chat_item_layout);
        }
    }

    private ArrayList<ChatItem> _data;
    private Context mContext;
    private DroidTool dt;

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public ChatListAdapter(DroidTool droidTool, Context context, ArrayList<ChatItem> _data) {
        this._data = _data;
        mContext = context;
        dt = droidTool;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View chatView = inflater.inflate(R.layout.adapter_chat_list, parent, false);
        ChatListAdapter.ViewHolder viewHolder = new ChatListAdapter.ViewHolder(chatView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final ChatItem data = _data.get(position);
        RelativeLayout ChatLayout = viewHolder.chat_item_layout;
        final TextView ChatUserChatTimeStamp = viewHolder.friend_chat_time_stamp;
        final TextView MyUserChatTimeStamp = viewHolder.my_chat_time_stamp;
        LinearLayout FriendChat = viewHolder.friend_chat;
        LinearLayout MyChat = viewHolder.my_chat;
        if (data.getChatUserName().equals(dt.pref.getString("username"))) {
            FriendChat.setVisibility(View.GONE);
            MyChat.setVisibility(View.VISIBLE);

            TextView MyUserChat = viewHolder.my_user_chat;
            try {
                MyUserChat.setText(data.getChatText());
                MyUserChatTimeStamp.setText(data.getChatTimeStamp());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FriendChat.setVisibility(View.VISIBLE);
            MyChat.setVisibility(View.GONE);

            TextView ChatUserNOImage = viewHolder.chat_user_without_image;
            try {
                ChatUserNOImage.setText(String.valueOf(data.getChatUserName().toString().charAt(0)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView ChatUsername = viewHolder.chat_username;
            try {
                String desiredUsername = data.getChatUserName();
                ChatUsername.setText(toInitCap(desiredUsername));
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView ChatUserChat = viewHolder.chat_user_chat;
            try {
                ChatUserChat.setText(data.getChatText());
                ChatUserChatTimeStamp.setText(data.getChatTimeStamp());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    // to set username start with upper case
    public static String toInitCap(String param) {
        if (param != null && param.length() > 0) {
            char[] charArray = param.toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            // set capital letter to first position
            return new String(charArray);
            // return desired output
        } else {
            return "";
        }
    }

}