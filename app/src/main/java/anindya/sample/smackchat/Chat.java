package anindya.sample.smackchat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by user on 5/5/2017.
 */

public class Chat extends AppCompatActivity{
    // list inflating variable
    ArrayList<ChatItem> chatItem;
    RecyclerView mRecyclerView;
    ChatAdapter adapter;
    String userName;

    private ConnectXmpp mService;
    private boolean mBounded;

    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            mService = ((LocalBinder<ConnectXmpp>) service).getService();
            mBounded = true;
            Log.d("xmpp", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.d("xmpp", "onServiceDisconnected");
        }
    };


    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        Log.d("Conversation", event.from);
        Log.d("Conversation", event.message);
        // insert array list
        //ChatItem chatListItem = new ChatItem();
        //chatListItem.setChatUserName("serverdata");
        //chatListItem.setChatText("serverdata");
        //chatItem.add(chatListItem);

        // set the recycler view to inflate the list
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //adapter = new ChatAdapter(getApplicationContext(), chatItem);
        //mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // keep device screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // keyboard adjustment
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("user");

        // initialization of streams list
        chatItem = new ArrayList<ChatItem>();
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_list);
        // set the recycler view to inflate the list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ChatAdapter(getApplicationContext(), chatItem);
        mRecyclerView.setAdapter(adapter);

        final EditText chat_edit_text = (EditText)findViewById(R.id.chat_edit_text);
        ImageButton chat_send = (ImageButton)findViewById(R.id.chat_send);

        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chat_edit_text.getText().equals("")){
                    String chat = chat_edit_text.getText().toString();
                    Intent intent = new Intent(getBaseContext(),ConnectXmpp.class );
                    intent.putExtra("chat", chat);
                    intent.putExtra("code","2");
                    startService(intent);
                    // insert array list
                    ChatItem chatListItem = new ChatItem();
                    chatListItem.setChatUserName(userName);
                    chatListItem.setChatText(chat);
                    chatItem.add(chatListItem);
                    // set the recycler view to inflate the list
                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                    mLinearLayoutManager.setStackFromEnd(true);
                    adapter = new ChatAdapter(getApplicationContext(), chatItem);
                    mRecyclerView.setAdapter(adapter);
                    chat_edit_text.setText("");
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(),ConnectXmpp.class );
        intent.putExtra("code","3");
        startService(intent);
        super.onBackPressed();
    }
}
