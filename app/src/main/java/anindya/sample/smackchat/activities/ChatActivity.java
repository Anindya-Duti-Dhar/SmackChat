package anindya.sample.smackchat.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.adapter.ChatAdapter;
import anindya.sample.smackchat.adapter.ChatListAdapter;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.model.ChatItem;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;

public class ChatActivity extends BaseActivity {

    ArrayList<ChatItem> chatItem;
    RecyclerView recyclerView;
    ChatListAdapter adapter;
    String userName;
    String mChat;
    LinearLayoutManager mLinearLayoutManager;
    ChatItem chatListObject;

    @Override
    public void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, XmppService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        if(!EventBus.getDefault().isRegistered(this))EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        if(EventBus.getDefault().isRegistered(this))EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(BroadcastEvent event) {
        Log.d("xmpp: ", "BroadcastEvent: " + event.item + "\nCategory: " + event.category + "\nMessage: " + event.message);
        if(event.item.equals("login")){

        } else if(event.chatEvent.type== Message.Type.chat){
            if(event.chatEvent.subject.equals("chat")){
                addMessage(event.chatEvent.type, event.chatEvent.subject, event.chatEvent.message, event.chatEvent.messageID, event.chatEvent.timeStamp, event.chatEvent.from);
            }
        }
    }

    private void addMessage(Message.Type type, String subject, String message, String messageID, String timeStamp, String from) {
                chatListObject = new ChatItem();
                // check last message ID with last entered message ID in array list
                if (!chatItem.isEmpty()) {
                    if (chatItem.get(chatItem.size() - 1).getChatMessageID() != messageID) {
                        chatListObject.setChatMessageType(type);
                        chatListObject.setChatSubject(subject);
                        chatListObject.setChatText(message);
                        chatListObject.setChatMessageID(messageID);
                        chatListObject.setChatTimeStamp(timeStamp);
                        chatListObject.setChatUserName(from);
                        chatItem.add(chatListObject);
                    }
                } else {
                    chatListObject.setChatMessageType(type);
                    chatListObject.setChatSubject(subject);
                    chatListObject.setChatText(message);
                    chatListObject.setChatMessageID(messageID);
                    chatListObject.setChatTimeStamp(timeStamp);
                    chatListObject.setChatUserName(from);
                    chatItem.add(chatListObject);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(chatItem.size() - 1);
                    }
                });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        super.register(this, "");
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        // keep device screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // keyboard adjustment
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //region Load Record Using Extras
        if (!dt.extra().isEmpty()) userName = dt.extra();
        dt.ui.textView.set(R.id.chat_toolbar_title, userName);
        //endregion

        // initialization of streams list
        chatItem = new ArrayList<ChatItem>();
        // set the recycler view to inflate the list
        recyclerView = (RecyclerView) findViewById(R.id.mRecylerView);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new ChatListAdapter(dt, getApplicationContext(), chatItem);

        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
        mLinearLayoutManager.setStackFromEnd(true);

        final EditText chat_edit_text = (EditText) findViewById(R.id.chat_edit_text);
        ImageButton chat_send = (ImageButton) findViewById(R.id.chat_send);

        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChat = chat_edit_text.getText().toString();
                if (!mChat.isEmpty()) {
                    mService.sendStanza(userName, Message.Type.chat, "chat", mChat);
                    chat_edit_text.setText("");
                }
            }
        });

    }

    // back button press method
    @Override
    public void onBackPressed() {
        dt.tools.startActivity(ContactProfileActivity.class, dt.extra());
    }

}
