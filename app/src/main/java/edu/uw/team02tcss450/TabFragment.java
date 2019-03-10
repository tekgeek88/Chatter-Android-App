package edu.uw.team02tcss450;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.model.Connections;
import edu.uw.team02tcss450.tasks.AsyncTaskFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    public interface OnTabFragmentInteractionListener extends
            WaitFragment.OnFragmentInteractionListener {
        void onRequestTabInteraction(String interaction);
    }


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


        EditText edittext_searchbox = v.findViewById(R.id.edittext_tabfragment_searchbox);
        edittext_searchbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null
                    && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    || actionId == EditorInfo.IME_ACTION_DONE) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        if (v.getText().toString().isEmpty()) {
                            Toast toast = Toast.makeText(getActivity(), "Please type in a valid search entry", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            // Fetch users and open a new fragment so we can add them
    //                        doUpdate(v.getText().toString());
                            Log.wtf("WTF", v.getText().toString());
                        }
                }
                return false;
            }
        });


//        loadRequestsSentFragment();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

//        ArrayAdapter<String> mArrayAdapter = ArrayAdapter<String>(,
//                R.layout.my_spinner,
//                getResources().getStringArray(R.array.search_options_array));

        Spinner search_spinner = getActivity().findViewById(R.id.spinner_search_options);
        ArrayAdapter mArrayAdapter = ArrayAdapter.createFromResource(
                getActivity().getApplicationContext(),
                R.array.search_options_array, R.layout.my_spinner); // where array_name consists of the items to show in Spinner
        mArrayAdapter.setDropDownViewResource(R.layout.my_spinner); // where custom-spinner is mycustom xml file.
        search_spinner.setAdapter(mArrayAdapter);
        search_spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView)view;
        String searchMethod = tv.getText().toString();
        Log.wtf("WTF", "id: " + id + "view: " + tv.getText().toString());

        if ("First Name".equals(searchMethod)) {

        } else if ("Last Name".equals(searchMethod)) {

        } else if ("Username".equals(searchMethod)) {

        } else if ("Email".equals(searchMethod)) {

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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




    private void loadRequestsReceivedFragment(){

        HomeActivity homeActivity = (HomeActivity)getActivity();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_to", homeActivity.getmCredentials().getUsername())
                .build();

        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestReceivedOnPostExecute)
                .addHeaderField("authorization", homeActivity.getmJwToken())
                .build().execute();
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
                    args.putSerializable(getString(R.string.keys_intent_connections_sent), connectionAsArray);
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



    private void loadRequestsSentFragment(){
        HomeActivity homeActivity = (HomeActivity)getActivity();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", homeActivity.getmCredentials().getUsername())
                .build();

        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestSentOnPostExecute)
                .addHeaderField("authorization", homeActivity.getmJwToken())
                .build().execute();
        loadRequestsReceivedFragment();
        Log.wtf("WTF", "done with sent frag!");
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
                    args.putSerializable(getString(R.string.keys_intent_connections_sent), connectionAsArray);
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
        if (mViewPager != null) {
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



}
