package anindya.sample.smackchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.List;

import anindya.sample.smackchat.R;
import anindya.sample.smackchat.fragments.FriendsFragment;
import anindya.sample.smackchat.fragments.RoomFragment;
import anindya.sample.smackchat.fragments.UserFragment;
import anindya.sample.smackchat.model.BroadcastEvent;
import anindya.sample.smackchat.services.XmppService;
import base.droidtool.activities.BaseActivity;

public class HomeActivity extends BaseActivity {

    //Defining Variables
    public TabLayout tabLayout;
    public ViewPager viewPager;
    boolean isLogged = false;
    int Count = 0;

    public onServiceGetListener serviceGetListener = null;

    public interface onServiceGetListener {
        void onServiceCreated();
    }

    public void setServiceResponse(onServiceGetListener listener){
        serviceGetListener = listener;
    }

    public onLoginListener loginListener = null;

    public interface onLoginListener {
        void onLogged(boolean isSuccess);
    }

    public void setLoginResponse(onLoginListener listener){
        loginListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        registerService(HomeActivity.this, new onServiceCreatedListener() {
            @Override
            public void onServiceCreated() {
                xmppLogin();
                if(serviceGetListener!=null)serviceGetListener.onServiceCreated();
            }
        });
    }

    @Override
    public void onDestroy() {
        unregisterService(HomeActivity.this);
        super.onDestroy();
    }

    @Subscribe
    public void onMessageEvent(BroadcastEvent event) {
        if (event.item.equals("login")) {
            isLogged = true;
            mService.setUpReceiver();
            if(loginListener!=null);loginListener.onLogged(true);
        }
    }

    public void xmppLogin() {
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
        toast(getString(R.string.login_failed_message));
        if(loginListener!=null);loginListener.onLogged(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        super.register(this, "");
        super.setStatusBarColor(getResources().getColor(R.color.contact_profile_darkBlue));
        super.initProgressDialog(getString(R.string.getting_ready));

        // initialize tab layout with tab icon
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chat_tab_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friends_tab_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.users_tab_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.group_tab_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.profile_tab_selector));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // initialize view pager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // for smooth transition between tabs
        viewPager.setOffscreenPageLimit(5);
        // initialize view pager adapter and setting that adapter
        final PagerAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        // bind home tab as default tab when launch home fragment
        viewPager.setCurrentItem(2);

        // add tab layout listener into view pager listener
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // tab listener
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // set the current item for which tab is selected
                viewPager.setCurrentItem(tab.getPosition());
                // animate fab button when new tab selected
                /*fab.hide();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        fab.show();
                    }
                }, 200);*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // do something with only view pager listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    // do something when view pager appeared
                    // Set title bar
                    //((MainActivity) getActivity()).setActionBarTitle(getActivity().getString(R.string.all_feeds_toolbar));
                }
                if (position == 1) {
                    // do something when view pager appeared
                    // Set title bar
                    //((MainActivity) getActivity()).setActionBarTitle(getActivity().getString(R.string.friends_feeds_toolbar));
                }
                if (position == 2) {
                    // do something when view pager appeared
                    // Set title bar
                    //setActionBarTitle(getString(R.string.profile_toolbar));
                }
                if (position == 3) {
                    // do something when view pager appeared
                    // Set title bar
                    // ((MainActivity) getActivity()).setActionBarTitle(getActivity().getString(R.string.profile_toolbar));
                }
                if (position == 4) {
                    // do something when view pager appeared
                    // Set title bar
                    // ((MainActivity) getActivity()).setActionBarTitle(getActivity().getString(R.string.profile_toolbar));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    // view pager adapter class to call different fragments
    class PageAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PageAdapter(FragmentManager fm, int numTabs) {
            super(fm);
            this.mNumOfTabs = numTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FriendsFragment FragmentA = new FriendsFragment();
                    return FragmentA;
                case 1:
                    FriendsFragment FragmentB = new FriendsFragment();
                    return FragmentB;
                case 2:
                    UserFragment FragmentC = new UserFragment();
                    return FragmentC;
                case 3:
                    RoomFragment FragmentD = new RoomFragment();
                    return FragmentD;
                case 4:
                    FriendsFragment FragmentE = new FriendsFragment();
                    return FragmentE;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    // Set up the toolbar title
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
