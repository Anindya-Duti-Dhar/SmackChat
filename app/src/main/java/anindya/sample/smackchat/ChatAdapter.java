package anindya.sample.smackchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 5/5/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView chat_username, chat_user_chat;

        public ViewHolder(View itemView) {
            super(itemView);
            chat_username = (TextView) itemView.findViewById(R.id.chat_username);
            chat_user_chat = (TextView) itemView.findViewById(R.id.chat_user_chat);
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
/*        for (int i = 0; i < _data.size(); i++) {
            ChatItem val = _data.get(i);
            if (val.getChatText().equals("initxmpp")) {
                _data.remove(i);
                break;
            }
        }*/
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
        TextView ChatUsername = viewHolder.chat_username;
        ChatUsername.setText(data.getChatUserName());

        TextView ChatUserChat = viewHolder.chat_user_chat;
        ChatUserChat.setText(data.getChatText());
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

}