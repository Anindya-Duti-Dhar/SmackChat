package anindya.sample.smackchat.activities;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.adapter.GroupChatListAdapter;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.model.ChatItem;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;

public class GroupChatActivity extends BaseActivity {

    private ArrayList<ChatItem> chatItemArrayList = new ArrayList<ChatItem>();
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GroupChatListAdapter adapter;
    private String roomName, mChat;
    private EditText etChat;
    private boolean isLogged = false;

    @Override
    public void onStart() {
        super.onStart();
        registerService(GroupChatActivity.this, new onServiceCreatedListener() {
            @Override
            public void onServiceCreated() {
                xmppLogin();
            }
        });
    }

    @Override
    public void onDestroy() {
        unregisterService(GroupChatActivity.this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(BroadcastEvent event) {
        if (event.item.equals("login")) {
            isLogged = true;
            mService.setUpReceiver();
            mService.joinRoom(username, roomName, new XmppService.onRoomJoinResponse() {
                @Override
                public void onJoin(boolean isJoined) {
                    if(isJoined){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(adapter);
                                mLinearLayoutManager.setStackFromEnd(true);
                            }
                        });
                        mService.receiveGroupMessage();
                    }
                    else toast("Room Join Error");
                }
            });
        } else if (event.item.equals("groupchat")) {
            Log.d("xmpp: ", "groupchat");
            if (event.chatItem.chatMessageType == Message.Type.groupchat) {
                if (event.chatItem.chatSubject.equals("groupchat")) {
                    addMessage(event.chatItem.chatMessageType, event.chatItem.chatSubject, event.chatItem.chatText, event.chatItem.chatMessageID, event.chatItem.chatTimeStamp, event.chatItem.chatUserName);
                }
            }
        }
    }

    private void addMessage(Message.Type type, String subject, String message, String messageID, String timeStamp, String from) {
        ChatItem chatItem = new ChatItem();
        // check last message ID with last entered message ID in array list
        if (!chatItemArrayList.isEmpty()) {
            if (chatItemArrayList.get(chatItemArrayList.size() - 1).getChatMessageID() != messageID) {
                chatItem.setChatMessageType(type);
                chatItem.setChatSubject(subject);
                chatItem.setChatText(message);
                chatItem.setChatMessageID(messageID);
                chatItem.setChatTimeStamp(timeStamp);
                chatItem.setChatUserName(from);
                chatItemArrayList.add(chatItem);
            }
        } else {
            chatItem.setChatMessageType(type);
            chatItem.setChatSubject(subject);
            chatItem.setChatText(message);
            chatItem.setChatMessageID(messageID);
            chatItem.setChatTimeStamp(timeStamp);
            chatItem.setChatUserName(from);
            chatItemArrayList.add(chatItem);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatItemArrayList.size() - 1);
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        super.register(this, "");
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        // keep device screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // keyboard adjustment
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //region Load Record Using Extras
        if (!dt.extra().isEmpty()) roomName = dt.extra();
        setupToolbar(roomName);
        dt.ui.textView.set(R.id.chat_toolbar_title, roomName);
        dt.ui.textView.set(R.id.chat_toolbar_no_image, String.valueOf(roomName.charAt(0)));
        //endregion

        // set the recycler view to inflate the list
        recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        adapter = new GroupChatListAdapter(dt, GroupChatActivity.this, chatItemArrayList);
        mLinearLayoutManager = new LinearLayoutManager(GroupChatActivity.this);
    }

    public void sendChat(View view) {
        etChat = dt.ui.editText.getRes(R.id.chat_edit_text);
        mChat = dt.ui.editText.get(R.id.chat_edit_text);
        if (!TextUtils.isEmpty(mChat)) {
            if (isLogged) {
                mService.sendGroupMessage(Message.Type.groupchat, "groupchat", mChat);
                etChat.setText("");
            } else toast(getString(R.string.login_failed_message));
        }
    }

    public void xmppLogin() {
        getCurrentUserInfo();
        try {
            mService.initConnection(username, password, new XmppService.onConnectionResponse() {
                @Override
                public void onConnected(boolean isConnected, XMPPConnection connection) {
                    if (isConnected) {
                        mService.login(username, password, new XmppService.onLoginResponse() {
                            @Override
                            public void onLoggedIn(boolean isLogged) {
                                if (!isLogged) onLoginFailed();
                            }
                        });
                    } else onLoginFailed();
                }
            });
            mService.connectConnection();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("xmpp: ", "UI:: Login Error: " + e.getMessage());
        }
    }

    public void onLoginFailed() {
        isLogged = false;
        toast(getString(R.string.login_failed_message));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            case R.id.action_profile:
                break;
            default:
                break;
        }
        return true;
    }

    // back button press method
    @Override
    public void onBackPressed() {
        dt.tools.startActivity(HomeActivity.class, dt.extra());
    }
}