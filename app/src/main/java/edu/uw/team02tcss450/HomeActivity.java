package edu.uw.team02tcss450;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import edu.uw.team02tcss450.model.Connections;
import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.utils.GetAsyncTask;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnChangePasswordFragmentInteractionListener,
        VerificationFragment.OnVerificationFragmentInteractionListener,
        ConnectionListFragment.OnListFragmentInteractionListener,
        ConnectionDetailFragment.OnIndividualConnectionListener,
        WeatherFragment.OnWeatherFragmentInteractionListener, RequestsTabFragment.OnRequestTabbedFragmentInteractionListener {


    private String mJwToken;
    private String mEmail;



    @Override
    public void onWeatherFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Pushy.listen(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadChatFragment();
                fab.hide();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        if (savedInstanceState == null) {
            if (findViewById(R.id.main_container) != null) {
                Credentials credentials = (Credentials) getIntent()
                        .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
                String emailAddress = mEmail = credentials.getEmail();
                final Bundle args = new Bundle();
                args.putString(getString(R.string.key_email), emailAddress);

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
            loadFragment(new WeatherFragment());
        } else if(id == R.id.nav_connection_fragment){

           loadConnectionFragment();

        }else if (id == R.id.nav_chat_fragment) {
            loadChatFragment();
        } //else if (id == R.id.nav_profile_fragment) {

        //}
    // else if (id == R.id.nav_setting_fragment) {

         else if (id == R.id.nav_logout_fragment) {
            logout();
        }
//        }  else if (id == R.id.nav_tabbed_request_fragment) {
//            loadFragment(new RequestsTabFragment());
//        }

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


    @Override
    protected void onStart() {
        super.onStart();

        loadHomeFragment();
    }

    private void loadFragment(Fragment frag) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


    public void loadChatFragment() {
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();

        args.putSerializable(getString(R.string.key_email), mEmail);
        args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
        chatFragment.setArguments(args);
        loadFragment(chatFragment);
    }

    private void loadHomeFragment() {
        Credentials credentials = (Credentials) getIntent()
                .getExtras().getSerializable(getString(R.string.keys_intent_credentials));

        mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
        mEmail = credentials.getEmail();

        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.keys_intent_credentials), credentials);
        homeFragment.setArguments(args);
        loadFragment(homeFragment);

        // Alex
        ConditionsFragment conFrag = new ConditionsFragment();
        Bundle conArgs = new Bundle();
        conArgs.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
        conFrag.setArguments(conArgs);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment);
        transaction.commit();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_fragment_home_conditions_container, conFrag)
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
    public void onRequestTabbedFragmentInteraction(Uri uri) {

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
        loadChatFragment();
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

            //or close this activity and bring back the Login
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
            //Ends this Activity and removes it from the Activity back stack.
//            finish();
        }
    }

}
