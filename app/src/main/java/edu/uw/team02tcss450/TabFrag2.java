package edu.uw.team02tcss450;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Activities that contain this fragment must implement the
 * {@link OnTabFrag2InteractionListener} interface
 * to handle interaction events.
 */
public class TabFrag2 extends Fragment implements TabLayout.OnTabSelectedListener,
        RequestSentListFragment.OnRequestSentListFragmentInteractionListener,
        RequestReceivedListFragment.OnRequestReceivedListFragmentInteractionListener,
        AdapterView.OnItemSelectedListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String searchParam = "first_name"; // The initial term the spinner is selected with.

    private OnTabFrag2InteractionListener mListener;

    // Fragment Setup
    private RequestSentListFragment mRequestsSentFragment;
    private Connections[] mRequestsSentData;
    private RequestReceivedListFragment mRequestsReceivedFragment;
    private Connections[] mRequestsReceivedData;
    private RequestSearchListFragment mRequestsSearchFragment;
    private Connections[] mRequestsSearchData;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFrag2.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFrag2 newInstance(String param1, String param2) {
        TabFrag2 fragment = new TabFrag2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public TabFrag2() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequestsSentData = (Connections[]) getArguments().getSerializable(getString(R.string.keys_intent_connections_sent));
            mRequestsReceivedData = (Connections[]) getArguments().getSerializable(getString(R.string.keys_intent_connections_received));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_frag2, container, false);

        Bundle args;
        mRequestsSentFragment = new RequestSentListFragment();
        args = new Bundle();
        if (null != mRequestsSentData) {
            args.putSerializable(getString(R.string.keys_intent_connections_sent), mRequestsSentData);
            mRequestsSentFragment.setArguments(args);
        }
        args = new Bundle();
        mRequestsReceivedFragment = new RequestReceivedListFragment();
        if (null != mRequestsReceivedData) {
            args.putSerializable(getString(R.string.keys_intent_connections_received), mRequestsSentData);
            mRequestsReceivedFragment.setArguments(args);
        }
        args = new Bundle();
        mRequestsSearchFragment = new RequestSearchListFragment();
        if (null != mRequestsSearchData) {
            args.putSerializable(getString(R.string.keys_intent_connections_search), mRequestsSearchData);
            mRequestsSearchFragment.setArguments(args);
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
                        Toast toast = Toast.makeText(getActivity(), "Search for nothing and find nothing...", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        // Fetch users and open a new fragment so we can add them
                        //                        doUpdate(v.getText().toString());
                        Log.wtf("WTF", v.getText().toString());
                        doSearch(v.getText().toString());
                    }
                }
                return false;
            }
        });


        ViewPager viewPager = v.findViewById(R.id.viewpager_id);
        setupViewPager(viewPager);
        TabLayout tabLayout = v.findViewById(R.id.tablayout_id);
        tabLayout.setupWithViewPager(viewPager);

        setupTabLabels(tabLayout);

        tabLayout.addOnTabSelectedListener(this);
        fetchConnections();
        return v;
    }

    private void setupTabLabels(TabLayout tabLayout) {
        if (null != tabLayout) {
            TabLayout.Tab tab1 = tabLayout.getTabAt(0);
            TextView tabOne = (TextView) LayoutInflater.from(
                    getActivity()).inflate(
                    R.layout.custom_tab, null);
            tabOne.setText("Requests Sent");
            tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_theme_color, 0, 0);

            TabLayout.Tab tab2 = tabLayout.getTabAt(1);
            TextView tabTwo = (TextView) LayoutInflater.from(
                    getActivity()).inflate(
                    R.layout.custom_tab, null);
            tabTwo.setText("Requests Received");
            tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_theme_color, 0, 0);

            TabLayout.Tab tab3 = tabLayout.getTabAt(2);
            TextView tabThree = (TextView) LayoutInflater.from(
                    getActivity()).inflate(
                    R.layout.custom_tab, null);
            tabThree.setText("Search");
            tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_search_secondary_color, 0, 0);

            tab1.setCustomView(tabOne);
            tab2.setCustomView(tabTwo);
            tab3.setCustomView(tabThree);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onFrag2Interaction(view);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTabFrag2InteractionListener) {
            mListener = (OnTabFrag2InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTabFrag2InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Spinner search_spinner = getActivity().findViewById(R.id.spinner_search_options);
        ArrayAdapter mArrayAdapter = ArrayAdapter.createFromResource(
                getActivity().getApplicationContext(),
                R.array.search_options_array, R.layout.my_spinner); // where array_name consists of the items to show in Spinner
        mArrayAdapter.setDropDownViewResource(R.layout.my_spinner); // where custom-spinner is mycustom xml file.
        search_spinner.setAdapter(mArrayAdapter);
        search_spinner.setOnItemSelectedListener(this);
    }

    private void doSearch(String searchTerm) {
        Log.wtf("WTF", "param: " + searchParam + " value: " + searchTerm);

        // Fetch Requests Sent
        HomeActivity homeActivity = (HomeActivity) getActivity();
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendPath(homeActivity.getString(R.string.ep_search))
                .appendQueryParameter("user", homeActivity.getmCredentials().getUsername())
                .appendQueryParameter(searchParam, searchTerm)
                .build();

        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::handleRequestsSearchPreExecute)
                .onPostExecute(this::handleRequestSearchOnPostExecute)
                .addHeaderField("authorization", homeActivity.getmJwToken())
                .build().execute();

    }

    private void fetchConnections() {
        // Fetch Requests Sent
        HomeActivity homeActivity = (HomeActivity) getActivity();
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", homeActivity.getmCredentials().getUsername())
                .build();

        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onRequestsSentLoadPreExecute)
                .onPostExecute(this::handleRequestSentOnPostExecute)
                .addHeaderField("authorization", homeActivity.getmJwToken())
                .build().execute();
        Log.wtf("WTF", "done with sent frag!");

        Uri uri2 = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_to", homeActivity.getmCredentials().getUsername())
                .build();

        // This last task calls update to update the views
        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri2.toString())
                .onPreExecute(this::onRequestsReceivedLoadPreExecute)
                .onPostExecute(this::handleRequestReceivedOnPostExecute)
                .addHeaderField("authorization", homeActivity.getmJwToken())
                .build().execute();
        Log.wtf("WTF", "done with received frag!");
    }



    private void onRequestsSentLoadPreExecute() {
        Toast.makeText(getActivity(), "Fetching Friend request sent by you",
                Toast.LENGTH_SHORT).show();
    }

    private void onRequestsReceivedLoadPreExecute() {
        Toast.makeText(getActivity(), "Fetching Friend requests others have sent you",
                Toast.LENGTH_SHORT).show();
    }

    private void handleRequestsSearchPreExecute() {
        if (null != mRequestsSearchFragment.mAdapter) {
            mRequestsSearchFragment.mAdapter.clear();
        }
        Toast.makeText(getActivity(), "Searching...",
                Toast.LENGTH_SHORT).show();
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
                    mRequestsSentData = connectionAsArray;
                    updateDataModelInFragments();
                    Toast.makeText(homeActivity, "SUCCESS: " + connectionAsArray.length + " request(s) received",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ERROR!", error);
                    //notify user
                    Toast.makeText(homeActivity, "Error: " + error,
                            Toast.LENGTH_SHORT).show();
                    updateDataModelInFragments();

                }
            } else {
                Log.e("ERROR!", error);
                //notify user
                Toast.makeText(homeActivity, "Error: " + error,
                        Toast.LENGTH_SHORT).show();
                updateDataModelInFragments();


            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            Toast.makeText(homeActivity, "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            updateDataModelInFragments();


        }
        ViewPager viewPager = getActivity().findViewById(R.id.viewpager_id);
        setupViewPager(viewPager);
        TabLayout tabLayout = getActivity().findViewById(R.id.tablayout_id);
        setupTabLabels(tabLayout);

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
                    mRequestsReceivedData = connectionAsArray;
                    updateDataModelInFragments();
                    Toast.makeText(homeActivity, "SUCCESS: " + connectionAsArray.length + " request(s) received",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ERROR!", error);
                    //notify user
                    Toast.makeText(homeActivity, "Error: " + error,
                            Toast.LENGTH_SHORT).show();
                    updateDataModelInFragments();

                }
            } else {
                Log.e("ERROR!", error);
                //notify user
                Toast.makeText(homeActivity, "Error: " + error,
                        Toast.LENGTH_SHORT).show();
                updateDataModelInFragments();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            Toast.makeText(homeActivity, "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            updateDataModelInFragments();


        }
        ViewPager viewPager = getActivity().findViewById(R.id.viewpager_id);
        setupViewPager(viewPager);
        TabLayout tabLayout = getActivity().findViewById(R.id.tablayout_id);
        setupTabLabels(tabLayout);
    }

    public void handleRequestSearchOnPostExecute(final String result) {
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
                                1)
                                .build());
                    }
                    Connections[] connectionAsArray = new Connections[connectionList.size()];
                    connectionAsArray = connectionList.toArray(connectionAsArray);
                    mRequestsSearchData = connectionAsArray;
                    mRequestsSearchFragment.updateConnections(mRequestsSearchData);
                    Toast.makeText(homeActivity, "SUCCESS: " + connectionAsArray.length + " request(s) received",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ERROR!", error);
                    //notify user
                    Toast.makeText(homeActivity, "Error: " + error,
                            Toast.LENGTH_SHORT).show();
//                    updateDataModelInFragments();

                }
            } else {
                Log.e("ERROR!", error);
                //notify user
                Toast.makeText(homeActivity, "Error: " + error,
                        Toast.LENGTH_SHORT).show();
//                updateDataModelInFragments();


            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            Toast.makeText(homeActivity, "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
//            updateDataModelInFragments();


        }
        ViewPager viewPager = getActivity().findViewById(R.id.viewpager_id);
        setupViewPager(viewPager);
        TabLayout tabLayout = getActivity().findViewById(R.id.tablayout_id);
        setupTabLabels(tabLayout);
        viewPager.setCurrentItem(2);

    }


    private void updateDataModelInFragments() {
        Bundle args;
        args = new Bundle();
        if (null != mRequestsSentData) {
            args.putSerializable(getString(R.string.keys_intent_connections_sent), mRequestsSentData);
            mRequestsSentFragment.setArguments(args);
            mRequestsSentFragment.updateConnections(mRequestsSentData);
        }
        args = new Bundle();
        if (null != mRequestsReceivedData) {
            args.putSerializable(getString(R.string.keys_intent_connections_received), mRequestsReceivedData);
            mRequestsReceivedFragment.setArguments(args);
            mRequestsReceivedFragment.updateConnections(mRequestsReceivedData);
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch (position) {
            case 0:
                getActivity().findViewById(R.id.appbarid).setVisibility(View.GONE);
                break;
            case 1:
                getActivity().findViewById(R.id.appbarid).setVisibility(View.GONE);
                break;
            case 2:
                getActivity().findViewById(R.id.appbarid).setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch (position) {
            case 0:
                Log.wtf("WTF", "Tab: " + position);
                break;
            case 1:
                Log.wtf("WTF", "Tab: " + position);
                break;
            case 2:
                getActivity().findViewById(R.id.appbarid).setVisibility(View.GONE);
                Log.wtf("WTF", "Tab: " + position);
                break;
        }

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onRequestReceivedListFragmentInteraction(Connections connection) {

    }

    @Override
    public void onRequestReceivedListButtonInteraction(View v, Connections connection) {

    }

    @Override
    public void onRequestSentListFragmentInteraction(Connections item) {

    }

    @Override
    public void onRequestSentListButtonInteraction(View v, Connections item) {
        HomeActivity homeActivity = (HomeActivity) getActivity();

        int id = v.getId();
        if (id == R.id.textview_requests_accept) {
            Log.wtf("WTF", "PENDING was pressed!");
        } else if (id == R.id.textview_requests_cancel) {
            Log.wtf("WTF", "CANCEL was pressed!");
            AsyncTaskFactory.removeConnectionRequestSentTo(
                    homeActivity,
                    homeActivity.getmJwToken(),
                    item.getUserName());
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView)view;
        String selectedText = tv.getText().toString();
        Log.wtf("WTF", "id: " + id + "view: " + tv.getText().toString());
        if ("First Name".equals(selectedText)) {
            searchParam = "firstname";
        } else if ("Last Name".equals(selectedText)) {
            searchParam = "lastname";
        } else if ("Username".equals(selectedText)) {
            searchParam = "username";
        } else if ("Email".equals(selectedText)) {
            searchParam = "email";
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


    private void setupViewPager(ViewPager viewPager) {
        if (null != viewPager) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
            adapter.addFragment(mRequestsSentFragment, "Requests Sent");
            adapter.addFragment(mRequestsReceivedFragment, "Request Received");
            adapter.addFragment(mRequestsSearchFragment, "Search");
            viewPager.setAdapter(adapter);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTabFrag2InteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onFrag2Interaction(View view);
    }
}

