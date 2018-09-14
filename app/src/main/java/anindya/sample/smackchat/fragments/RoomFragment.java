package anindya.sample.smackchat.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jivesoftware.smackx.muc.HostedRoom;

import java.util.ArrayList;
import java.util.List;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.activities.HomeActivity;
import anindya.sample.smackchat.adapter.RoomListAdapter;
import anindya.sample.smackchat.model.RoomItem;
import anindya.sample.smackchat.services.XmppService;

public class RoomFragment extends Fragment {

    private boolean hasFragmentLoadedOnce = false;
    private List<RoomItem> roomItemList = new ArrayList<RoomItem>();
    public HomeActivity activity;
    private RecyclerView mRecyclerView;
    private RoomListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private boolean isLoading = false;
    private TextView mNoDataMessage;

    // create instance
    public static RoomFragment newInstance() {
        RoomFragment fragment = new RoomFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public RoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //this.MyApplication.getAppContext().getFragmentManager().beginTransaction().addToBackStack(null);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!hasFragmentLoadedOnce) {
                if(activity.isLogged){
                    getRoomList();
                    hasFragmentLoadedOnce = true;
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (HomeActivity) getActivity();
        initUi(view);
        activity.setRoomRefreshResponseListener(new HomeActivity.onRoomRefreshResponse() {
            @Override
            public void onRefresh() {
                if(!isLoading)getRoomList();
            }
        });
    }

    private void initUi(View view) {
        mNoDataMessage = (TextView)view.findViewById(R.id.mNoDataMessage);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecylerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new RoomListAdapter(activity.dt, getActivity(), roomItemList);
        mRecyclerView.setAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoading)getRoomList();
            }
        });
    }

    private void getRoomList(){
        isLoading = true;
        //activity.showDialog();
        activity.mService.getRoomList(new XmppService.onRoomLoadResponse() {
            @Override
            public void onLoad(List<HostedRoom> hostedRoomList) {
                if(hostedRoomList!=null){
                    roomItemList.clear();
                    if(hostedRoomList.size()>0){
                        mNoDataMessage.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        for (HostedRoom roomList: hostedRoomList) {
                            RoomItem roomItem = new RoomItem();
                            roomItem = activity.mService.getRoomInfo(roomList.getName());
                            if(roomItem!=null){
                                roomItem.setName(roomList.getName());
                                roomItem.setJid(String.valueOf(roomList.getJid()));
                                roomItemList.add(roomItem);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        for (RoomItem item: roomItemList) {
                            Log.d("xmpp: ", "Room List: "+item.getOccupantsCount());
                        }
                    } else {
                        mNoDataMessage.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    isLoading = false;
                    if(refreshLayout.isRefreshing())refreshLayout.setRefreshing(false);
                   //activity.hideDialog();
                }
            }
        });
    }
}
