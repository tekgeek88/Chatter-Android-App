package edu.uw.team02tcss450;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.model.ChatThread;
import edu.uw.team02tcss450.model.Connection;
import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.model.EveryMessage;
import edu.uw.team02tcss450.tasks.AsyncTaskFactory;
import edu.uw.team02tcss450.utils.DelAsyncTask;
import edu.uw.team02tcss450.utils.GetAsyncTask;
import edu.uw.team02tcss450.utils.PutAsyncTask;
import edu.uw.team02tcss450.utils.SendPostAsyncTask;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnChangePasswordFragmentInteractionListener,
        VerificationFragment.OnVerificationFragmentInteractionListener,
        ConnectionListFragment.OnListFragmentInteractionListener,
        ConnectionDetailFragment.OnIndividualConnectionListener,
        WeatherFragment.OnWeatherFragmentInteractionListener,
        RequestReceivedListFragment.OnRequestReceivedInteractionListener,
        GoogleMap.OnMapClickListener,
        TabFragment.OnTabFragmentInteractionListener,
        TabFrag2.OnTabFrag2InteractionListener,
        RecentChatFragment.OnRecentChatListFragmentInteractionListener,
        RequestSearchListFragment.OnRequestSearchInteractionListener,
        RequestSentListFragment.OnRequestSentInteractionListener {


    public String getmJwToken() {
        return mJwToken;
    }

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 100000;//100,000mili or 100 seconds

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 10;//10 seconds
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    public static View mAlertDialogView = null;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private TabFrag2 mTabFragmentHolder;
    private Connection mConnectionCallback;

    private String mJwToken;
    private String mEmail;
    private String mUsername;
    private LatLng mLocation;

    private int mChatId;

    private ConditionsFragment mConFrag;
    private boolean isReloaded = false;

    private List<Connection> mFriends = new ArrayList<>();
    private boolean mLoadFromChatNotification = false;
    private boolean mLoadFromConnRequest = false;
    private boolean mLoadFromConvoRequest = false;


    public Credentials getmCredentials() {
        return mCredentials;
    }

    private Credentials mCredentials;



    //########################################################################
    //##                 THE ACTIVITY/FRAGMENT LIFE CYCLE
    //########################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Home");
        setSupportActionBar(toolbar);
        Pushy.listen(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadChatFragment(1);
            }
        });
        //GPS
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    setLocation(location);
                    Log.d("LOCATION UPDATE!", location.toString());
                }
            }

            ;
        };
        createLocationRequest();


        Button chatButton = findViewById(R.id.button_activity_home_chat);
        chatButton.setVisibility(View.GONE);
        chatButton.setOnClickListener(view -> startChat());
        Button removeButton = findViewById(R.id.button_activity_home_remove);
        removeButton.setVisibility(View.GONE);
        removeButton.setOnClickListener(this::removeSelectedFriend);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        mJwToken = intent.getStringExtra(getString(R.string.keys_intent_jwt));
        mCredentials = (Credentials) args.getSerializable(getString(R.string.keys_intent_credentials));

        if (savedInstanceState == null) {
            if (findViewById(R.id.main_container) != null) {
                mEmail = mCredentials.getEmail();
                mUsername = mCredentials.getUsername();
                args = new Bundle();
                args.putString(getString(R.string.key_email), mEmail);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MainActivity.class);
        startService(intent);

        isReloaded = false;
        if (getIntent().getExtras().getString(getString(R.string.keys_intent_fragment_tag)) != null
                && getIntent().getExtras().getString(getString(R.string.keys_intent_fragment_tag)).equals(WeatherFragment.TAG)) {
            WeatherFragment tempFrag = new WeatherFragment();
            Bundle args = new Bundle();
            mJwToken = getIntent().getExtras().getString(getString(R.string.keys_intent_jwt));
            mLocation = getIntent().getExtras().getParcelable(getString(R.string.keys_map_latlng));
            args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
            args.putParcelable(getString(R.string.keys_map_latlng), mLocation);
            tempFrag.setArguments(args);
            loadFragment(tempFrag);
        } else {
            loadHomeFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        } else if (id == R.id.action_change_password) {
            changePassword();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home_fragment) {
            loadHomeFragment();
        } else if (id == R.id.nav_weather_fragment) {
            WeatherFragment tempFrag = new WeatherFragment();
            Bundle args = new Bundle();
            args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
            requestLocation();
            args.putParcelable(getString(R.string.keys_map_latlng), mLocation);
            tempFrag.setArguments(args);
            loadFragment(tempFrag);
        } else if (id == R.id.nav_connection_fragment) {
            loadConnectionFragment();
        } else if (id == R.id.nav_chat_fragment) {
            // Always load the global chat from this side bar action
            //loadChatFragment(1);
            loadRecentChatFragment();
        }   //else if (id == R.id.nav_profile_fragment) {
        else if (id == R.id.nav_requests_fragment) {
            mTabFragmentHolder = new TabFrag2();
            loadFragment(mTabFragmentHolder);
        } else if (id == R.id.nav_refer_fragment) {
            loadFragment(new InvitationFragment());
        } else if (id == R.id.nav_logout_fragment) {
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void handleRecentChatGetOnPostExecute(final String result) {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        //parse JSON
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {

                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    List<ChatThread> chatList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        chatList.add(new ChatThread.Builder(jsonConnection.getString("name"),
                                jsonConnection.getInt("chatid"),
                                credentials.getUsername())
                                .build());
                    }
                    // Log.d("cded","ghjkl");
                    ChatThread[] chatAsArray = new ChatThread[chatList.size()];
                    chatAsArray = chatList.toArray(chatAsArray);
                    Bundle args = new Bundle();
                    args.putSerializable(RecentChatFragment.ARG_CONNECTION_LIST, chatAsArray);
                    Fragment frag = new RecentChatFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();

                    loadFragment(frag);
                    setTitle("Recent Chats");
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: No Friends yet!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    private void handleRecentChatHomepageGetOnPostExecute(final String result) {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        //parse JSON
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {

                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    List<ChatThread> chatList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        chatList.add(new ChatThread.Builder(jsonConnection.getString("name"),
                                jsonConnection.getInt("chatid"),
                                credentials.getUsername())
                                .build());
                    }
                    // Log.d("cded","ghjkl");
                    ChatThread[] chatAsArray = new ChatThread[chatList.size()];
                    chatAsArray = chatList.toArray(chatAsArray);
                    Bundle args = new Bundle();
                    args.putSerializable(RecentChatFragment.ARG_CONNECTION_LIST, chatAsArray);
                    Fragment frag = new RecentChatFragment();
                    frag.setArguments(args);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_fragment_home_chats_container, frag, RecentChatFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                    onWaitFragmentInteractionHide();
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: No Friends yet!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void handleRecentChatHomepageInError (final String result) {
        onWaitFragmentInteractionHide();
        Log.d("Homepage","Empty load : " + result);
    }

    private void removeSelectedFriend(View view) {
        if (!mFriends.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mAlertDialogView.getContext());
            builder.setTitle("Remove Friend")
                    .setMessage("Are you sure you want to break this friendship?")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeFriends();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mFriends.clear();
                        }
                    })
                    .setIcon(R.drawable.ic_sad_face)
                    .show();

        } else {
            Log.d("LOL", "no selected friends to remove");
        }
    }

    private void startChat() {
        if (!mFriends.isEmpty()) {
            Credentials credentials = (Credentials) getIntent()
                    .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_startChat))
                    .appendQueryParameter("count", String.valueOf(mFriends.size() + 1))
                    .appendQueryParameter("username1", credentials.getUsername());
            for (int i = 0; i < mFriends.size(); i++) {

                builder.appendQueryParameter("username" + (i + 2), mFriends.get(i).getUserName()).build();
                /// .build();
            }
            Uri uri = builder.build();
            Log.d("URL", uri.toString());
            mFriends.clear();
            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleStartChatGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken)
                    .build().execute();
//
//            loadChatFragment(1);
        } else {
            Log.d("LOL", "no selected friends to remove");
        }
    }

    private void handleStartChatGetOnPostExecute(final String result) {
        //parse JSON
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {

                if (resultsJSON.has("data")) {
                    int chatId = resultsJSON.getInt("data");
                    onWaitFragmentInteractionHide();

                    loadChatFragment(chatId);
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: No Friends yet!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    private void handleConnectionListGetOnPostExecute(final String result) {
        //parse JSON
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {

                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    List<Connection> connectionList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        connectionList.add(new Connection.Builder(jsonConnection.getInt("memberid"),
                                jsonConnection.getString("firstname"),
                                jsonConnection.getString("lastname"),
                                jsonConnection.getString("username"),
                                jsonConnection.getInt("verified"))
                                .build());
                    }
                    // Log.d("cded","ghjkl");
                    Connection[] connectionAsArray = new Connection[connectionList.size()];
                    connectionAsArray = connectionList.toArray(connectionAsArray);
                    Bundle args = new Bundle();
                    args.putSerializable(ConnectionListFragment.ARG_CONNECTION_LIST, connectionAsArray);
                    Fragment frag = new ConnectionListFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();

                    loadFragment(frag);
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: No Friends yet!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    public void handleRemoveConnectionRequestSentToOnPost(final String result) {
        //parse JSON
        String response = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has("message")) {
                    response = resultsJSON.getString("message");
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Success: " + response,
                            Toast.LENGTH_LONG).show();
                    mTabFragmentHolder.getmRequestsSentFragment().removeItem(mConnectionCallback);
                } else {
                    Log.e("ERROR!", response);
                    //notify user
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Error: " + response,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("ERROR!", response);
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: " + response,
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }
    }

    public void handleSendEmailInviteOnPostWithToast(final String result) {
        //parse JSON
        String response = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has("message")) {
                    response = resultsJSON.getString("message");
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Success: " + response,
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.e("ERROR!", response);
                    //notify user
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Error: " + response,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("ERROR!", response);
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: " + response,
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }
    }


    public void handleFriendRequestOnPostWithToast(final String result) {
        //parse JSON
        String message = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            message = resultsJSON.getString("message");
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has("message")) {
                    message = resultsJSON.getString("message");
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Success: " + message,
                            Toast.LENGTH_LONG).show();
                    mTabFragmentHolder.getmRequestsSentFragment().addItem(mConnectionCallback);

                } else {
                    Log.e("ERROR!", message);
                    //notify user
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Error: " + message,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("ERROR!", message);
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: " + message,
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }
    }

    public void handleRequestReceivedOnPostExecute(final String result) {
        //parse JSON
        String error;
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            error = resultsJSON.getString("message");
            if (success) {
                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    List<Connection> connectionList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        connectionList.add(new Connection.Builder(jsonConnection.getInt("memberid"),
                                jsonConnection.getString("firstname"),
                                jsonConnection.getString("lastname"),
                                jsonConnection.getString("username"),
                                jsonConnection.getInt("verified"))
                                .build());
                    }
                    Connection[] connectionAsArray = new Connection[connectionList.size()];
                    connectionAsArray = connectionList.toArray(connectionAsArray);
                    Bundle args = new Bundle();
                    args.putSerializable(getString(R.string.keys_intent_connections_sent), connectionAsArray);
                    Fragment frag = new RequestReceivedListFragment();
                    frag.setArguments(args);
                    onWaitFragmentInteractionHide();
                    loadFragment(frag, RequestReceivedListFragment.TAG);
                } else {
                    Log.e("ERROR!", error);
                    //notify user
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Error: " + error,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("ERROR!", error);
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: " + error,
                        Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void removeConnectionRequestSentTo(String sent_to_username) {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", mCredentials.getUsername())
                .appendQueryParameter("sent_to", sent_to_username)
                .build();

        new DelAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRemoveConnectionRequestSentToOnPost)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }


    //########################################################################
    //##                     FRAGMENT LOADERS / UTILS
    //########################################################################

    public void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(frag.getTag());
        // Commit the transaction
        transaction.commit();
    }

    public void loadFragment(Fragment frag, String tag) {
        removeFragment(tag);
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
        //frag.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, tag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    public void loadChatFragment(int chatId) {
        mChatId = chatId;
        // Create the request
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_getAll))
                .build();
        // Build the message to post
        JSONObject msg = new JSONObject();

        try {
            msg.put("chat_id", chatId);
        } catch (JSONException e) {
            Log.wtf("WTF", "SHIT HIT THE FAN!\n" + e.toString());
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleChatMessagesGetOnPostExecute)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void loadConnectionFragment() {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("username", credentials.getUsername())
                .build();

        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleConnectionListGetOnPostExecute)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void loadRecentChatFragment() {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_recentChats))
                .appendQueryParameter("username", credentials.getUsername())
                .build();

        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRecentChatGetOnPostExecute)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void loadHomeFragment() {

        mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
        mEmail = mCredentials.getEmail();

        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.keys_intent_credentials), mCredentials);
        homeFragment.setArguments(args);
        loadFragment(homeFragment);

        // Alex
        mConFrag = new ConditionsFragment();
        Bundle conArgs = new Bundle();
        conArgs.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
        conArgs.putParcelable(getString(R.string.keys_map_latlng), mLocation);
        mConFrag.setArguments(conArgs);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment);
        transaction.commit();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_fragment_home_conditions_container, mConFrag, ConditionsFragment.TAG)
                .addToBackStack(null)
                .commit();

        // Alex Recent chat on homepage
//        Uri uri = new Uri.Builder()
//                .scheme("https")
//                .appendPath(getString(R.string.ep_base_url))
//                .appendPath(getString(R.string.ep_recentChats))
//                .appendQueryParameter("username", mCredentials.getUsername())
//                .build();
//
//        new GetAsyncTask.Builder(uri.toString())
//                .onPreExecute(this::onWaitFragmentInteractionShow)
//                .onPostExecute(this::handleRecentChatHomepageGetOnPostExecute)
//                .addHeaderField("authorization", mJwToken)
//                .build().execute();
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }


    //########################################################################
    //##                     ASYNC TASK HANDLERS
    //########################################################################

    public void handleChatMessagesGetOnPostExecute(final String result) {
        //parse JSON
        String error = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            error = resultsJSON.getString("message");
            if (success) {
                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    ArrayList<EveryMessage> messageList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonMessage = data.getJSONObject(i);
                        EveryMessage tempMessage = new EveryMessage(
                                jsonMessage.getString("email"),
                                jsonMessage.getString("message"),
                                mEmail
                        );
                        tempMessage.setChatId(jsonMessage.getInt("chatid"));
                        tempMessage.setTimeStamp(jsonMessage.getString("timestamp"));
                        messageList.add(tempMessage
                        );
                    }
                    Bundle args = new Bundle();
                    args.putSerializable(getString(R.string.keys_intent_messages), messageList);
                    args.putString(getString(R.string.key_email), mEmail);
                    args.putString(getString(R.string.keys_intent_jwt), mJwToken);
                    args.putInt(getString(R.string.key_chat_id), mChatId);

                    Fragment frag = new ChatFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();
                    loadFragment(frag, ChatFragment.getTAG());
                } else {
                    Log.e("ERROR!", error);
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", error);
                onWaitFragmentInteractionHide();
                Bundle args = new Bundle();
                args.putString(getString(R.string.key_email), mEmail);
                args.putString(getString(R.string.keys_intent_jwt), mJwToken);
                args.putInt(getString(R.string.key_chat_id), mChatId);

                Fragment frag = new ChatFragment();
                frag.setArguments(args);

                onWaitFragmentInteractionHide();

                loadFragment(frag, ChatFragment.getTAG());

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void handleRequestSentOnPostExecute(final String result) {
        //parse JSON
        String error;
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            error = resultsJSON.getString("message");
            if (success) {
                if (resultsJSON.has("data")) {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    List<Connection> connectionList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        connectionList.add(new Connection.Builder(jsonConnection.getInt("memberid"),
                                jsonConnection.getString("firstname"),
                                jsonConnection.getString("lastname"),
                                jsonConnection.getString("username"),
                                jsonConnection.getInt("verified"))
                                .build());
                    }
                    Connection[] connectionAsArray = new Connection[connectionList.size()];
                    connectionAsArray = connectionList.toArray(connectionAsArray);
                    Bundle args = new Bundle();
                    args.putSerializable(getString(R.string.keys_intent_connections_sent), connectionAsArray);
                    Fragment frag = new RequestSentListFragment();
                    frag.setArguments(args);
                    onWaitFragmentInteractionHide();
                    loadFragment(frag, RequestSentListFragment.TAG);
                } else {
                    Log.e("ERROR!", error);
                    //notify user
                    onWaitFragmentInteractionHide();
                    Toast.makeText(this, "Error: " + error,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("ERROR!", error);
                //notify user
                onWaitFragmentInteractionHide();
                Toast.makeText(this, "Error: " + error,
                        Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    //########################################################################
    //##                     ASYNC TASK HANDLERS
    //########################################################################
    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        String response = result.toString();
        onWaitFragmentInteractionHide();
        Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
        Log.e("ASYNC_TASK_ERROR", result);

    }


    @Override
    public void onHomeFragmentInteraction(Uri uri) {

    }


    private void logout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
        new DeleteTokenAsyncTask().execute();

        //or close this activity and bring back the Login
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        //  TODO: PICK ONE OF THESE TO KEEP
        // End this Activity and remove it from the Activity back stack.
        finish();
        //close the app
        finishAndRemoveTask();
    }


    private void changePassword() {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        ChangePasswordFragment changePassFragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.keys_intent_credentials), credentials.getEmail());
        changePassFragment.setArguments(args);
        loadFragment(changePassFragment);
    }


    @Override
    public void onChangePasswordSuccess(Credentials cr) {
        VerificationFragment verificationFragment = new VerificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.string_fragment_from_register_to_login_email),
                cr.getEmail());
        args.putString("fragment_header", getString(R.string.string_fragment_change_password_verification_change_successful));
        args.putString("fragment_body", getString(R.string.string_fragment_change_password_verification_first_phrase));
        verificationFragment.setArguments(args);
        loadFragment(verificationFragment);

    }


    @Override
    public void onGoBackLoginClicked() {
        logout();
    }

    @Override
    public void onListFragmentInteraction(Connection item) {
        mFriends.add(item);
        startChat();
    }

    @Override
    public void onCheckBoxListInteraction(View v, Connection item) {
        mFriends.add(item);
        mAlertDialogView = v;
    }

    public void removeFriends() {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        for (int i = 0; i < mFriends.size(); i++) {
            Log.d("LOL", mFriends.get(i).toString());
            if (mFriends.get(i).getUserName() == credentials.getUsername()) {
                AsyncTaskFactory.removeConnectionRequestSentTo(this, mJwToken, mFriends.get(i).getUserName());
            } else {
                AsyncTaskFactory.removeConnectionRequestSentFrom(this, mJwToken, mFriends.get(i).getUserName());
            }
        }
        loadConnectionFragment();
        mFriends.clear();
    }

    @Override
    public void OnIndividualConnectionAddInteraction(String username) {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", credentials.getUsername())
                .appendQueryParameter("sent_to", username)
                .build();

        Log.d("a1", username);
        Log.d("a1", credentials.getUsername());
        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleConnectionAddListGetOnPostExecute)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void handleConnectionAddListGetOnPostExecute(final String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                loadConnectionFragment();

            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }

    }

    @Override
    public void OnIndividualConnectionRemoveInteraction(String username) {
//        Credentials credentials = (Credentials) getIntent()
//                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
//        Uri uri = new Uri.Builder()
//                .scheme("https")
//                .appendPath(getString(R.string.ep_base_url))
//                .appendPath(getString(R.string.ep_connections))
//                //  .appendPath("delete")
//                .appendQueryParameter("sent_from",credentials.getUsername())
//                .appendQueryParameter("sent_to", username)
//                .build();
//
//        new DelAsyncTask.Builder(uri.toString())
//                .onPreExecute(this::onWaitFragmentInteractionShow)
//                .onPostExecute(this::handleConnectionRemoveListGetOnPostExecute)
//                .addHeaderField("authorization", mJwToken)
//                .build().execute();


    }


    @Override
    public void OnIndividualConnectionChatInteraction(String url) {
        loadChatFragment(1);
    }


    @Nullable
    private void removeFragment(String tag) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(frag)
                    .commit();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onRequestTabInteraction(String interaction) {

    }

    @Override
    public void onFrag2Interaction(View view) {

    }


    @Override
    public void onRecentChatListFragmentInteraction(ChatThread mItem) {
        loadChatFragment(mItem.getChatId());
    }


    //########################################################################
    //##                  FRIEND REQUEST HANDLERS
    //########################################################################


    /**
     * This method allows the view to speak with the controller. In-turn, the controller
     * will let the data model know (the adapter) that it should remove some items. It
     * will then remove items and the let the view know that it can update its view of the data
     *
     * @param connection
     */
    @Override
    public void onRequestSentCancelInteraction(Connection connection) {
        mConnectionCallback = connection;
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", mCredentials.getUsername())
                .appendQueryParameter("sent_to", connection.getUserName())
                .build();

        new DelAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestSentCancelInteractionOnPost)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }



    public void handleRequestSentCancelInteractionOnPost(final String result) {
        //parse JSON
        String response = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has("message")) {
                    response = resultsJSON.getString("message");
                    onWaitFragmentInteractionHide();
                    mTabFragmentHolder.getmRequestsSentFragment().removeItem(mConnectionCallback);
                } else {
                    Log.e("ERROR!", response);
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", response);
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }


    @Override
    public void onRequestReceivedAcceptInteraction(Connection connection) {
        mConnectionCallback = connection;
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendPath(getString(R.string.ep_confirm))
                .appendQueryParameter("sent_from", connection.getUserName())
                .appendQueryParameter("sent_to", mCredentials.getUsername())
                .build();

        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestReceivedAcceptInteractionOnPost)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }


    public void handleRequestReceivedAcceptInteractionOnPost(final String result) {
        //parse JSON
        String response = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has("message")) {
                    response = resultsJSON.getString("message");
                    onWaitFragmentInteractionHide();
                    mTabFragmentHolder.getmRequestsReceivedFragment().removeItem(mConnectionCallback);
                } else {
                    Log.e("ERROR!", response);
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", response);
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }


    @Override
    public void onRequestReceivedCancelInteraction(Connection connection) {
        mConnectionCallback = connection;
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", connection.getUserName())
                .appendQueryParameter("sent_to", mCredentials.getUsername())
                .build();

        new DelAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestReceivedCancelInteractionOnPost)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    public void handleRequestReceivedCancelInteractionOnPost(final String result) {
        //parse JSON
        String response = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has("message")) {
                    response = resultsJSON.getString("message");
                    onWaitFragmentInteractionHide();
                    mTabFragmentHolder.getmRequestsReceivedFragment().removeItem(mConnectionCallback);
                } else {
                    Log.e("ERROR!", response);
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", response);
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }


    @Override
    public void onRequestSearchAcceptInteraction(Connection connection) {
        mConnectionCallback = connection;
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", mCredentials.getUsername())
                .appendQueryParameter("sent_to", connection.getUserName())
                .build();

        new PutAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleRequestSearchAcceptInteractionOnPost)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }


    public void handleRequestSearchAcceptInteractionOnPost(final String result) {
        //parse JSON
        String response = "";
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has("message")) {
                    response = resultsJSON.getString("message");
                    onWaitFragmentInteractionHide();
                    mTabFragmentHolder.getmRequestsSearchFragment().removeItem(mConnectionCallback);
                    mTabFragmentHolder.getmRequestsSentFragment().addItem(mConnectionCallback);
                } else {
                    Log.e("ERROR!", response);
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", response);
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }


    // Deleting the Pushy device token must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

            //unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();
        }
    }


    @Override
    public void onWeatherFragmentInteraction(Uri uri) {
    }

    @Override
    public void onWeatherFragmentOpenMap(LatLng location) {
        if (mLocation == null && location == null){
            mLocation = new LatLng(47.2529,-122.4443);//Tacoma
        }

        Intent i = new Intent(this, MapActivity.class);
        i.putExtra(getString(R.string.keys_intent_jwt), mJwToken);
        if (location == null){
            i.putExtra(getString(R.string.keys_map_latlng), mLocation);
        } else {
            i.putExtra(getString(R.string.keys_map_latlng), location);
        }
        i.putExtra(getString(R.string.keys_intent_credentials), mCredentials);
        startActivity(i);
    }


    //GPS
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                setLocation(location);
                                //Log.d("LOCATION", location.toString());
                            }
                        }
                    });
        }
    }

    private void setLocation(final Location location) {
        mLocation = new LatLng(location.getLatitude(),location.getLongitude());
        if (mConFrag != null && !isReloaded) {
            mConFrag.reloadWeather(mLocation);
            isReloaded = true;
        }
    }

    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

}
