package anindya.sample.smackchat.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.activities.HomeActivity;
import anindya.sample.smackchat.adapter.UserListAdapter;
import anindya.sample.smackchat.model.Users;
import anindya.sample.smackchat.utils.ApiCalls;

public class UserFragment extends Fragment {

    public HomeActivity activity;
    private List<Users> userListArrayList = new ArrayList<Users>();
    private RecyclerView mRecyclerView;
    private UserListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private boolean isLoading = false;
    ApiCalls apiCalls = new ApiCalls();

    // create instance
    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public UserFragment() {
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (HomeActivity) getActivity();
        // view initialize
        initUi(view);
        // load user list
        if(!isLoading)startLoadUsers();
    }

    private void initUi(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecylerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoading)startLoadUsers();
            }
        });
    }

    private void startLoadUsers() {
        if(activity.dt.droidNet.hasConnection()){
            activity.showDialog();
            isLoading = true;
            apiCalls.setLoadUserListener(new ApiCalls.onLoadUserListener() {
                @Override
                public void onHttpResponse(List<Users> usersList) {
                    if(usersList!=null){
                        userListArrayList.clear();
                        userListArrayList = usersList;
                        adapter = new UserListAdapter(activity.dt, getActivity(), userListArrayList);
                        mRecyclerView.setAdapter(adapter);
                    }
                    activity.hideDialog();
                    isLoading = false;
                    refreshLayout.setRefreshing(false);
                }
            });
            apiCalls.getAllUsers();
        } else activity.dt.droidNet.internetErrorDialog();
    }

}
