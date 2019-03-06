package edu.uw.team02tcss450;


import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.model.Connections;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {


    private Fragment mRequestsSent;
    private Fragment mRequestsReceived;
    private String mUsername;
    private String mEmail;
    private String mJwToken;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);
        getActivity().setTitle("Connection Manager");


        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
        mRequestsReceived = new WaitFragment();
        mRequestsSent = new WaitFragment();
        loadRequestsSentFragment();
        if (getArguments() != null) {
            //get the email and JWT from the Activity. Make sure the Keys match what you used
            mUsername = getArguments().getString(getString(R.string.key_username));
            mEmail = getArguments().getString(getString(R.string.key_email));
            mJwToken = getArguments().getString(getString(R.string.keys_intent_jwt));
        }



//        loadRequestsSentFragment();

        return v;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            mFragmentList.add(0, mRequestsSent);
            mFragmentList.add(1, mRequestsReceived);
            mFragmentTitleList.add(0, "Requests Sent");
            mFragmentTitleList.add(1, "Requests Received");
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void setFragment(int position, Fragment frag, String title) {
            this.mFragmentList.add(position, frag);
            this.mFragmentTitleList.add(position, title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }




    private void loadRequestsSentFragment(){
        HomeActivity homeActivity = (HomeActivity)getActivity();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", homeActivity.mCredentials.getUsername())
                .build();

        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestSentOnPostExecute)
                .addHeaderField("authorization", homeActivity.getmJwToken())
                .build().execute();
        loadRequestsReceivedFragment();
        Log.wtf("WTF", "done with sent frag!");
    }



    public void handleRequestReceivedOnPostExecute(final String result) {
        //parse JSON
        HomeActivity homeActivity = (HomeActivity)getActivity();
        String error;
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            error = resultsJSON.getString("message");
            if (success) {
                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    List<Connections> connectionList = new ArrayList<>();
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        connectionList.add(new Connections.Builder(jsonConnection.getInt("memberid"),
                                jsonConnection.getString("firstname"),
                                jsonConnection.getString("lastname"),
                                jsonConnection.getString("username"),
                                jsonConnection.getInt("verified"))
                                .build());
                    }
                    Connections[] connectionAsArray = new Connections[connectionList.size()];
                    connectionAsArray = connectionList.toArray(connectionAsArray);
                    Bundle args = new Bundle();
                    args.putSerializable(getString(R.string.keys_intent_connections), connectionAsArray);
                    Fragment frag = new RequestReceivedListFragment();
                    frag.setArguments(args);
                    homeActivity.onWaitFragmentInteractionHide();
                    mRequestsReceived = frag;
                    setupViewPager();
                } else {
                    Log.e("ERROR!", error);
                    //notify user
                    homeActivity.onWaitFragmentInteractionHide();
                    Toast.makeText(homeActivity, "Error: " + error,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ERROR!", error);
                //notify user
                homeActivity.onWaitFragmentInteractionHide();
                Toast.makeText(homeActivity, "Error: " + error,
                        Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            homeActivity.onWaitFragmentInteractionHide();
            Toast.makeText(homeActivity, "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();

        }
    }




    private void loadRequestsReceivedFragment(){

        HomeActivity homeActivity = (HomeActivity)getActivity();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_to", homeActivity.mCredentials.getUsername())
                .build();

        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestReceivedOnPostExecute)
                .addHeaderField("authorization", homeActivity.getmJwToken())
                .build().execute();
    }


    public void handleRequestSentOnPostExecute(final String result) {
        //parse JSON
        HomeActivity homeActivity = (HomeActivity)getActivity();
        String error;
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            error = resultsJSON.getString("message");
            if (success) {
                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    List<Connections> connectionList = new ArrayList<>();
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        connectionList.add(new Connections.Builder(jsonConnection.getInt("memberid"),
                                jsonConnection.getString("firstname"),
                                jsonConnection.getString("lastname"),
                                jsonConnection.getString("username"),
                                jsonConnection.getInt("verified"))
                                .build());
                    }
                    Connections[] connectionAsArray = new Connections[connectionList.size()];
                    connectionAsArray = connectionList.toArray(connectionAsArray);
                    Bundle args = new Bundle();
                    args.putSerializable(getString(R.string.keys_intent_connections), connectionAsArray);
                    Fragment frag = new RequestSentListFragment();
                    frag.setArguments(args);
                    homeActivity.onWaitFragmentInteractionHide();
                    mRequestsSent = frag;
                    loadRequestsReceivedFragment();
                } else {
                    Log.e("ERROR!", error);
                    //notify user
                    homeActivity.onWaitFragmentInteractionHide();
                    Toast.makeText(homeActivity, "Error: " + error,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ERROR!", error);
                //notify user
                homeActivity.onWaitFragmentInteractionHide();
                Toast.makeText(homeActivity, "Error: " + error,
                        Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            homeActivity.onWaitFragmentInteractionHide();
            Toast.makeText(homeActivity, "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();

        }
    }


    private void setupViewPager() {
        Log.e("updating view pager...", mViewPager.toString());
        final TabLayout tabLayout = getActivity().findViewById(R.id.tabs);
        mViewPager = getActivity().findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        if (mViewPager != null) {
            mViewPager.setAdapter(mViewPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        }
    }

}
