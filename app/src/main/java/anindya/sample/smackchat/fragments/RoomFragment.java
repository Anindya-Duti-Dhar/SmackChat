package anindya.sample.smackchat.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                // show no feeds if list is empty
                if (roomItemList.size() == 0) {
                    //showAllLoadingAnimation(false);
                }
                // call server to fetch feeds
                //getFeeds(false);
                hasFragmentLoadedOnce = true;
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (HomeActivity) getActivity();
        activity.setLoginResponse(new HomeActivity.onLoginListener() {
            @Override
            public void onLogged(boolean isSuccess) {
                if(isSuccess){
                    getRoomList();
                }
            }
        });
    }

    private void getRoomList(){
        activity.mService.getRoomList(new XmppService.onRoomLoadResponse() {
            @Override
            public void onLoad(List<HostedRoom> hostedRoomList) {
                if(hostedRoomList!=null){
                    if(hostedRoomList.size()>0){
                        roomItemList.clear();
                        for (HostedRoom roomList: hostedRoomList) {
                            RoomItem roomItem = new RoomItem();
                            roomItem = activity.mService.getRoomInfo(roomList.getName());
                            if(roomItem!=null){
                                roomItem.setName(roomList.getName());
                                roomItem.setJid(String.valueOf(roomList.getJid()));
                                roomItemList.add(roomItem);
                            }
                        }
                        for (RoomItem item: roomItemList) {
                            Log.d("xmpp: ", "Room List: "+item.getOccupantsCount());
                        }
                    }
                }
            }
        });
    }
}
