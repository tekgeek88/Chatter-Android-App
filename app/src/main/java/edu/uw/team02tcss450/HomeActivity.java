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
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import edu.uw.team02tcss450.model.Credentials;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnChangePasswordFragmentInteractionListener,
        VerificationFragment.OnVerificationFragmentInteractionListener,
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
                        .getSerializableExtra(getString(R.string.keys_intent_credentials));
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
        } else if (id == R.id.nav_chat_fragment) {

        } else if (id == R.id.nav_profile_fragment) {

        } else if (id == R.id.nav_setting_fragment) {

        } else if (id == R.id.nav_logout_fragment) {
            logout();
        }  else if (id == R.id.nav_tabbed_request_fragment) {
            loadFragment(new RequestsTabFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment);
        transaction.commit();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_fragment_home_conditions_container, new ConditionsFragment())
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
