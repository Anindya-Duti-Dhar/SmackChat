package anindya.sample.smackchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.ChatItem;
import anindya.sample.smackchat.utils.PrefManager;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView chat_user_without_image, chat_username, chat_user_chat, my_user_chat;
        public LinearLayout friend_chat, my_chat;

        public ViewHolder(View itemView) {
            super(itemView);
            chat_username = (TextView) itemView.findViewById(R.id.chat_username);
            chat_user_chat = (TextView) itemView.findViewById(R.id.chat_user_chat);
            chat_user_without_image = (TextView) itemView.findViewById(R.id.chat_user_without_image);
            my_user_chat = (TextView) itemView.findViewById(R.id.my_user_chat);
            friend_chat = (LinearLayout) itemView.findViewById(R.id.friend_chat);
            my_chat = (LinearLayout) itemView.findViewById(R.id.my_chat);
        }
    }

    private ArrayList<ChatItem> _data;
    private Context mContext;

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public ChatAdapter(Context context, ArrayList<ChatItem> _data) {
        this._data = _data;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View chatView = inflater.inflate(R.layout.chat_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(chatView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final ChatItem data = _data.get(position);
        LinearLayout FriendChat = viewHolder.friend_chat;
        LinearLayout MyChat = viewHolder.my_chat;
        if(data.getChatUserName().equals(PrefManager.getUserName(mContext))){
            FriendChat.setVisibility(View.GONE);
            MyChat.setVisibility(View.VISIBLE);

            TextView MyUserChat = viewHolder.my_user_chat;
            try {
                MyUserChat.setText(data.getChatText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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