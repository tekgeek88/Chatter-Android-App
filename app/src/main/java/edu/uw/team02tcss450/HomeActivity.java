package edu.uw.team02tcss450;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.model.Connections;
import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.model.EveryMessage;
import edu.uw.team02tcss450.tasks.AsyncTaskFactory;
import edu.uw.team02tcss450.utils.GetAsyncTask;
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
        RequestSentListFragment.OnRequestListFragmentInteractionListener, RequestReceivedListFragment.OnRequestReceivedListFragmentInteractionListener,
        GoogleMap.OnMapClickListener, TabFragment.OnTabFragmentInteractionListener {


    public String getmJwToken() {
        return mJwToken;
    }

    private String mJwToken;
    private String mEmail;
    private String mUsername;
    private LatLng mLocation;

    public Credentials getmCredentials() {
        return mCredentials;
    }

    private Credentials mCredentials;

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
            args.putParcelable(getString(R.string.keys_map_latlng), mLocation);
            tempFrag.setArguments(args);
            loadFragment(tempFrag);
        } else if(id == R.id.nav_connection_fragment){
            loadConnectionFragment();
        }else if (id == R.id.nav_chat_fragment) {
            // Always load the global chat from this side bar action
            loadChatFragment(1);
        } //else if (id == R.id.nav_profile_fragment) {

        else if (id == R.id.nav_requests_fragment) {
            TabFragment tabFragment = new TabFragment();
            Bundle args = new Bundle();
            args.putSerializable(getString(R.string.key_email), mEmail);
            args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
            args.putSerializable(getString(R.string.key_username), mCredentials.getUsername());
            tabFragment.setArguments(args);
            loadFragment(tabFragment);
        }

        else if (id == R.id.nav_logout_fragment) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void loadConnectionFragment(){
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("username",credentials.getUsername())
                .build();


        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleConnectionListGetOnPostExecute)
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }


    private void handleConnectionListGetOnPostExecute(final String result) {
        //parse JSON
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
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
                    // Log.d("cded","ghjkl");
                    Connections[] connectionAsArray = new Connections[connectionList.size()];
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }


    public void handleRequestOnPostWithToast(final String result) {
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
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonMessage = data.getJSONObject(i);
                        EveryMessage tempMessage = new EveryMessage(
                                jsonMessage.getString("username"),
                                jsonMessage.getString("message"),
                                jsonMessage.getString("email")
                        );
                                tempMessage.setChatId(jsonMessage.getInt("chatid"));
                                tempMessage.setTimeStamp(jsonMessage.getString("timestamp"));
                        messageList.add(tempMessage
                        );
                    }
                    Bundle args = new Bundle();
                    args.putSerializable(getString(R.string.keys_intent_messages), messageList);
                    args.putSerializable(getString(R.string.key_email), mEmail);
                    args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
                    args.putSerializable(getString(R.string.key_chat_id), 1);

                    Fragment frag = new ChatFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();

                    loadFragment(frag, ChatFragment.getTAG());
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


    @Override
    protected void onStart() {
        super.onStart();
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
        } catch ( JSONException e) {
            Log.wtf("WTF", "SHIT HIT THE FAN!\n" + e.toString());
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleChatMessagesGetOnPostExecute)
                .onCancelled(this::handleErrorsInTask)
                .addHeaderField("authorization", mJwToken)
                .build().execute();


//        ChatFragment chatFragment = new ChatFragment();
//        Bundle args = new Bundle();
    }

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

    private void loadHomeFragment() {

        mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
        mEmail = mCredentials.getEmail();

        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.keys_intent_credentials), mCredentials);
        homeFragment.setArguments(args);
        loadFragment(homeFragment);

        // Alex
        ConditionsFragment conFrag = new ConditionsFragment();
        Bundle conArgs = new Bundle();
        conArgs.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
        conArgs.putParcelable(getString(R.string.keys_map_latlng), mLocation);
        conFrag.setArguments(conArgs);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment);
        transaction.commit();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_fragment_home_conditions_container, conFrag, ConditionsFragment.TAG)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onHomeFragmentInteraction(Uri uri) {

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

    private void logout() {

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

        new DeleteTokenAsyncTask().execute();

        //close the app
        //finishAndRemoveTask();

        //or close this activity and bring back the Login
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        // End this Activity and remove it from the Activity back stack.
        finish();
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
    public void onListFragmentInteraction(Connections mItem) {


        ConnectionDetailFragment connectionDetail = new ConnectionDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("firstname", mItem.getFirstName());
        args.putSerializable("lastname", mItem.getLastName());
        args.putSerializable("username", mItem.getUserName());
        args.putSerializable("action", mItem.getVerified());

        connectionDetail.setArguments(args);
        loadFragment(connectionDetail);



    }

    @Override
    public void OnIndividualConnectionAddInteraction(String username) {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                .appendQueryParameter("sent_from",credentials.getUsername())
                .appendQueryParameter("sent_to", username)
                .build();

        Log.d("a1",username);
        Log.d("a1",credentials.getUsername());
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
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections))
                //  .appendPath("delete")
                .appendQueryParameter("sent_from",credentials.getUsername())
                .appendQueryParameter("sent_to", username)
                .build();

        new DelAsyncTask.Builder(uri.toString())
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleConnectionRemoveListGetOnPostExecute)
                .addHeaderField("authorization", mJwToken)
                .build().execute();



    }

    private void handleConnectionRemoveListGetOnPostExecute(final String result) {

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
    public void OnIndividualConnectionChatInteraction(String url) {
        loadChatFragment(1);
    }


    @Override
    public void onRequestReceivedListFragmentInteraction(Connections item) {
        Log.wtf("WTF", "The listview was clicked!");
    }

    @Override
    public void onRequestReceivedListButtonInteraction(View v, Connections connection) {

        int id = v.getId();

        if (id == R.id.textview_requests_accept) {
            Log.wtf("WTF", "ACCEPT was pressed!");
            AsyncTaskFactory.confirmConnection(this, mJwToken, connection.getUserName());
        } else if (id == R.id.textview_requests_cancel) {
            Log.wtf("WTF", "CANCEL was pressed!");
            AsyncTaskFactory.removeConnectionRequestSentFrom(this, mJwToken, connection.getUserName());
        }
        removeFragment(RequestReceivedListFragment.TAG);
        // Update the fragment

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
    public void onRequestSentListFragmentInteraction(Connections item) {

    }

    @Override
    public void onRequestSentListButtonInteraction(View v, Connections connection) {
        int id = v.getId();

        if (id == R.id.textview_requests_accept) {
            Log.wtf("WTF", "PENDING was pressed!");
        } else if (id == R.id.textview_requests_cancel) {
            Log.wtf("WTF", "CANCEL was pressed!");
            AsyncTaskFactory.removeConnectionRequestSentTo(this, mJwToken, connection.getUserName());
        }
        removeFragment(RequestSentListFragment.TAG);
        // Update the fragment
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onRequestTabInteraction(String interaction) {

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
        if (mLocation == null){
            mLocation = new LatLng(47.2529,-122.4443);//Tacoma
        }
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra(getString(R.string.keys_intent_jwt), mJwToken);
        i.putExtra(getString(R.string.keys_map_latlng), mLocation);
        i.putExtra(getString(R.string.keys_intent_credentials), mCredentials);
        startActivity(i);
    }

}
