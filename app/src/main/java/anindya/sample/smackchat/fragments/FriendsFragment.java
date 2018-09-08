package anindya.sample.smackchat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.model.Users;


public class FriendsFragment extends Fragment {

    //Defining Variables
    private boolean hasFragmentLoadedOnce = false;
    // list inflating variable
    private List<Users> usersList = new ArrayList<Users>();

    // create instance
    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public FriendsFragment() {
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
        return inflater.inflate(R.layout.fragment_friends, container, false);
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
                if (usersList.size() == 0) {
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
        // start view initialize and functionality declare from here
        // view initialize
        //initUi(view);
        // init listener
        //initListener();
    }
}
