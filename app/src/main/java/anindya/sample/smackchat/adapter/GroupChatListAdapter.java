package anindya.sample.smackchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * Created by Duti on 9/14/2018.
 */

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout whole_chat_layout;
        public LinearLayout friend_chat_layout;
        public LinearLayout my_chat_layout;
        public TextView friend_name, friend_no_image, friend_chat, friend_chat_time_stamp;
        public TextView my_chat, my_chat_time_stamp ;

        public ViewHolder(View itemView) {
            super(itemView);
            whole_chat_layout = (RelativeLayout) itemView.findViewById(R.id.whole_chat_layout);
            friend_chat_layout = (LinearLayout) itemView.findViewById(R.id.friend_chat_layout);
            my_chat_layout = (LinearLayout) itemView.findViewById(R.id.my_chat_layout);

            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friend_no_image = (TextView) itemView.findViewById(R.id.friend_no_image);
            friend_chat = (TextView) itemView.findViewById(R.id.friend_chat);
            friend_chat_time_stamp = (TextView) itemView.findViewById(R.id.friend_chat_time_stamp);

            my_chat = (TextView) itemView.findViewById(R.id.my_chat);
            my_chat_time_stamp = (TextView) itemView.findViewById(R.id.my_chat_time_stamp);
        }
    }

    private ArrayList<ChatItem> _data;
    private Context mContext;
    private DroidTool dt;

    private Context getContext() {
        return mContext;
    }

    public GroupChatListAdapter(DroidTool droidTool, Context context, ArrayList<ChatItem> _data) {
        this._data = _data;
        mContext = context;
        dt = droidTool;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View chatView = inflater.inflate(R.layout.adapter_chat_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(chatView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ChatItem data = _data.get(position);
        RelativeLayout wholeChatLayout = viewHolder.whole_chat_layout;
        LinearLayout friendChatLayout = viewHolder.friend_chat_layout;
        final LinearLayout myChatLayout = viewHolder.my_chat_layout;

        final TextView friendName = viewHolder.friend_name;
        TextView friendNoImage = viewHolder.friend_no_image;
        TextView friendChat = viewHolder.friend_chat;
        final TextView friendChatTimeStamp = viewHolder.friend_chat_time_stamp;
        TextView myChat = viewHolder.my_chat;
        final TextView myChatTimeStamp = viewHolder.my_chat_time_stamp;

        if (data.getChatUserName().equals(dt.pref.getString("username"))) {
            friendChatLayout.setVisibility(View.GONE);
            myChatLayout.setVisibility(View.VISIBLE);
            try {
                myChat.setText(data.getChatText());
                myChatTimeStamp.setText(data.getChatTimeStamp());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xmpp: ", "chat adapter: "+e.getMessage());
            }
        } else {
            friendChatLayout.setVisibility(View.VISIBLE);
            myChatLayout.setVisibility(View.GONE);
            try {
                friendName.setText(toInitCap(data.getChatUserName()));
                friendNoImage.setText(String.valueOf(data.getChatUserName().toString().charAt(0)));
                friendChat.setText(data.getChatText());
                friendChatTimeStamp.setText(data.getChatTimeStamp());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("xmpp: ", "chat adapter: "+e.getMessage());
            }
        }

        wholeChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(data.isChatIsTapped()){
                    if(friendChatTimeStamp.getVisibility()==View.VISIBLE)friendChatTimeStamp.setVisibility(View.GONE);
                    if(myChatTimeStamp.getVisibility()==View.VISIBLE)myChatTimeStamp.setVisibility(View.GONE);
                    if(friendName.getVisibility()==View.VISIBLE)friendName.setVisibility(View.GONE);
                    data.setChatIsTapped(false);
                } else {
                    if(myChatLayout.getVisibility()==View.VISIBLE){
                        if(friendChatTimeStamp.getVisibility()==View.VISIBLE)friendChatTimeStamp.setVisibility(View.GONE);
                        if(myChatTimeStamp.getVisibility()==View.GONE)myChatTimeStamp.setVisibility(View.VISIBLE);
                        if(friendName.getVisibility()==View.VISIBLE)friendName.setVisibility(View.GONE);
                    } else {
                        if(friendChatTimeStamp.getVisibility()==View.GONE)friendChatTimeStamp.setVisibility(View.VISIBLE);
                        if(myChatTimeStamp.getVisibility()==View.VISIBLE)myChatTimeStamp.setVisibility(View.GONE);
                        if(friendName.getVisibility()==View.GONE)friendName.setVisibility(View.VISIBLE);
                    }
                    data.setChatIsTapped(true);
                }
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
            return new String(charArray);
        } else {
            return "";
        }
    }
}
