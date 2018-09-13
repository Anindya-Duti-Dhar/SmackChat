package anindya.sample.smackchat.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.adapter.ChatAdapter;
import anindya.sample.smackchat.model.ChatItem;
import anindya.sample.smackchat.services.ConnectXmpp;
import base.droidtool.activities.BaseActivity;


public class Chat extends BaseActivity {

    ArrayList<ChatItem> chatItemArrayList;
    RecyclerView mRecyclerView;
    ChatAdapter adapter;
    String userName;
    String mChat;
    LinearLayoutManager mLinearLayoutManager;
    ChatItem chatItem;

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;

    @Subscribe
    public void onMessageEvent(ChatItem event) {
        String chat = event.chatText;
        String from = event.chatUserName;
        String subject = event.chatSubject;
        String chatID = event.chatMessageID;
        addAMessage(from, chat, subject, chatID);
    }

    // add messages to the array list item from event bus
    private void addAMessage(String user, String message, String subject, String messageID) {
        if (subject.equals("comment")) {
            chatItem = new ChatItem();
            // check last message ID with last entered message ID in array list
            if (!chatItemArrayList.isEmpty()) {
                if (chatItemArrayList.get(chatItemArrayList.size() - 1).getChatMessageID() != messageID) {
                    chatItem.setChatText(message);
                    chatItem.setChatUserName(user);
                    chatItem.setChatMessageID(messageID);
                    chatItemArrayList.add(chatItem);
                }
            } else {
                chatItem.setChatText(message);
                chatItem.setChatUserName(user);
                chatItem.setChatMessageID(messageID);
                chatItemArrayList.add(chatItem);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(chatItemArrayList.size() - 1);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("xmpp", "onStart");
        if(!EventBus.getDefault().isRegistered(this))EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if(EventBus.getDefault().isRegistered(this))EventBus.getDefault().unregister(this);
        Log.d("xmpp", "onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xmpp", "onResume");
        // register receiver
        LocalBroadcastManager.getInstance(Chat.this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("join"));
    }

    @Override
    protected void onPause() {
        Log.d("xmpp", "onPause");
        LocalBroadcastManager.getInstance(Chat.this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("xmpp", "onCreate");
        setContentView(R.layout.chat);
        super.register(this, getString(R.string.app_name));
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        // keep device screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // keyboard adjustment
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        userName = dt.pref.getString("username");

        // initialization of streams list
        chatItemArrayList = new ArrayList<ChatItem>();
        // set the recycler view to inflate the list
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_list);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new ChatAdapter(dt, getApplicationContext(), chatItemArrayList);

        joinChatRoom();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("join")) {
                    mProgressDialog.hide();
                    Log.d("xmpp: ", "successfully joined");
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                    mRecyclerView.setAdapter(adapter);
                    mLinearLayoutManager.setStackFromEnd(true);
                }
            }
        };

        final EditText chat_edit_text = (EditText) findViewById(R.id.chat_edit_text);
        ImageButton chat_send = (ImageButton) findViewById(R.id.chat_send);

        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChat = chat_edit_text.getText().toString();
                if (!mChat.isEmpty()) {
                    String mSubject = "comment";
                    sendMessage(mChat, mSubject);
                    chat_edit_text.setText("");
                }
            }
        });
    }

    public void sendMessage(String chat, String subject) {
        Intent intent = new Intent(getBaseContext(), ConnectXmpp.class);
        intent.putExtra("chat", chat);
        intent.putExtra("subject", subject);
        intent.putExtra("code", "2");
        startService(intent);
    }

    public void joinChatRoom() {
        Intent intent = new Intent(getBaseContext(), ConnectXmpp.class);
        intent.putExtra("user", userName);
        intent.putExtra("code", "1");
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(getBaseContext(), ConnectXmpp.class);
        //intent.putExtra("code", "3");
        //startService(intent);
        super.onBackPressed();
    }
}
