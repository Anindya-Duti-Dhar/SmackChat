package anindya.sample.smackchat.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.adapter.ChatListAdapter;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.model.ChatItem;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;

public class ChatActivity extends BaseActivity {

    ArrayList<ChatItem> chatItemArrayList = new ArrayList<ChatItem>();
    RecyclerView recyclerView;
    LinearLayoutManager mLinearLayoutManager;
    ChatListAdapter adapter;
    String opponentName, mChat;
    EditText etChat;
    boolean isLogged = false;
    int Count = 0;

    @Override
    public void onStart() {
        super.onStart();
        registerService(ChatActivity.this, new onServiceCreatedListener() {
            @Override
            public void onServiceCreated() {
                xmppLogin();
            }
        });
    }

    @Override
    public void onDestroy() {
        unregisterService(ChatActivity.this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(BroadcastEvent event) {
        if (event.item.equals("login")) {
            isLogged = true;
            mService.setUpReceiver();
            mService.receiveOldMessages(opponentName, new XmppService.onOldMessagesResponse() {
                @Override
                public void onReceived(List<Message> message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setLayoutManager(mLinearLayoutManager);
                            recyclerView.setAdapter(adapter);
                            mLinearLayoutManager.setStackFromEnd(true);
                        }
                    });
                    mService.receiveStanza();
                    hideDialog();
                }
            });
        } else if (event.item.equals("chat")) {
            if (event.chatEvent.type == Message.Type.chat) {
                if (event.chatEvent.subject.equals("chat")) {
                    addMessage(event.chatEvent.type, event.chatEvent.subject, event.chatEvent.message, event.chatEvent.messageID, event.chatEvent.timeStamp, event.chatEvent.from);
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
        setContentView(R.layout.activity_chat);
        super.register(this, "");
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        // keep device screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // keyboard adjustment
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //region Load Record Using Extras
        if (!dt.extra().isEmpty()) opponentName = dt.extra();
        setupToolbar(opponentName);
        dt.ui.textView.set(R.id.chat_toolbar_title, opponentName);
        dt.ui.textView.set(R.id.chat_toolbar_no_image, String.valueOf(opponentName.charAt(0)));
        //endregion

        // set the recycler view to inflate the list
        recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        adapter = new ChatListAdapter(dt, ChatActivity.this, chatItemArrayList);
        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
    }

    public void sendChat(View view) {
        etChat = dt.ui.editText.getRes(R.id.chat_edit_text);
        mChat = dt.ui.editText.get(R.id.chat_edit_text);
        if (!TextUtils.isEmpty(mChat)) {
            if (isLogged) {
                mService.sendStanza(opponentName, Message.Type.chat, "chat", mChat);
                ChatItem chatItem = new ChatItem();
                chatItem.setChatMessageType(Message.Type.chat);
                chatItem.setChatSubject("chat");
                chatItem.setChatText(mChat);
                chatItem.setChatTimeStamp(convertDate(System.currentTimeMillis(), "dd-MMM-yyyy h:mm a"));
                chatItem.setChatUserName(username);
                chatItemArrayList.add(chatItem);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatItemArrayList.size() - 1);
                etChat.setText("");
            } else toast(getString(R.string.login_failed_message));
        }
    }

    public static String convertDate(Long dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
    }

    public void xmppLogin() {
        showDialog();
        Count++;
        if (Count % 4 == 0) {
            // after 3rd attempt
            Log.d("xmpp: ", "Login time out");
            hideDialog();
            onLoginFailed();
        } else {
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
                xmppLogin();
            }
        }
    }

    public void onLoginFailed() {
        isLogged = false;
        hideDialog();
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
            case R.id.action_profile:
                dt.tools.startActivity(ContactProfileActivity.class, opponentName);
                break;
            default:
                break;
        }
        return true;
    }

    // back button press method
    @Override
    public void onBackPressed() {
        dt.tools.startActivity(ContactProfileActivity.class, dt.extra());
    }
}
