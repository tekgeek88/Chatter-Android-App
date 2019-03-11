package edu.uw.team02tcss450;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.utils.PutAsyncTask;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, WaitFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private String mJwt;
    private Credentials mCredentials;
    private LatLng mLocation;
    private Marker mMarker;
    private LatLng mLastLocation;
    private String mUsername;
    private String mNickname;
    private WaitFragment.OnFragmentInteractionListener mWaitListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMarker = null;
        if (savedInstanceState == null) {
            mCredentials = (Credentials) getIntent()
                    .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
            mJwt = getIntent().getExtras().getString(getString(R.string.keys_intent_jwt));
            mLocation = getIntent().getExtras().getParcelable(getString(R.string.keys_map_latlng));
            mLastLocation = mLocation;
            mUsername = mCredentials.getUsername();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton button = findViewById(R.id.imagebutton_activity_map_check);
        button.setOnClickListener(this::goBack);
        button = findViewById(R.id.imagebutton_activity_map_save);
        button.setOnClickListener(this::savePressed);
        if (this instanceof WaitFragment.OnFragmentInteractionListener) {
            mWaitListener = (WaitFragment.OnFragmentInteractionListener) this;
        }
    }


    private void savePressed (View v) {
        if (mMarker == null) {
            mLocation = mMap.getCameraPosition().target;
        }
        openSaveDialog();
    }

    @Override
    public void onBackPressed() {
        //goBack(true);
    }

    private void goBack (View v) {
        goBack(false);
    }

    private void goBack (boolean b) {
        if (mMarker == null && b){
            mLocation = mLastLocation;
        } else if (mMarker == null && !b) {
            mLocation = mMap.getCameraPosition().target;
        }
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra(getString(R.string.keys_intent_credentials), (Serializable) mCredentials);
        i.putExtra(getString(R.string.keys_intent_jwt), mJwt);
        i.putExtra(getString(R.string.keys_map_latlng), mLocation);
        i.putExtra(getString(R.string.keys_intent_fragment_tag), WeatherFragment.TAG);
        //i.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        startActivity(i);
        //End this Activity and remove it from the Activity back stack.
        mWaitListener = null;
        finish();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMarker != null){
            mMarker.remove();
        }
        mMarker = mMap.addMarker(new MarkerOptions().position(latLng)

                .title("Lat: " + Math.floor(latLng.latitude*1000)/1000 + ", Long: " + Math.floor(latLng.longitude*1000)/1000));
        mLocation = latLng;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mMarker != null){
            mMarker.remove();
        }
        mMarker = mMap.addMarker(new MarkerOptions().position(latLng)

                .title("Lat: " + Math.floor(latLng.latitude*1000)/1000 + ", Long: " + Math.floor(latLng.longitude*1000)/1000));
        mLocation = latLng;

        addToLocationFavorites();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Far away 10
        //City 13
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 12));
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    private void onRemoveMarker (DialogInterface a, int b) {
        mMarker.remove();
        mMarker = null;
    }

    private void addToLocationFavorites () {
        openDialog();

    }

    private void sendToFavorites (String nickname) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_locations))
                .appendQueryParameter(getString(R.string.keys_weather_username), mUsername)
                .appendQueryParameter(getString(R.string.keys_weather_latitude), Double.toString(mLocation.latitude))
                .appendQueryParameter(getString(R.string.keys_weather_longitude), Double.toString(mLocation.longitude))
                .appendQueryParameter(getString(R.string.keys_weather_nickname), nickname)
                .build();

        new PutAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleFavoriteAddOnPre)
                .onPostExecute(this::handleFavoriteAddOnPost)
                .onCancelled(this::handleFavoriteAddInError)
                .build().execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        openDialog();
        return false;
    }

    private void openDialog () {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Remove marker to not update weather location\nAdd to favorites to keep this location");
        dlgAlert.setTitle("Marker Setup");
        dlgAlert.setPositiveButton("Remove", this::onRemoveMarker);
        dlgAlert.setNegativeButton("Cancel", null);
        dlgAlert.setNeutralButton("Add to favorites", this::openNickname);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void openSaveDialog () {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Add to favorites to keep this location");
        dlgAlert.setTitle("Save Setup");
        dlgAlert.setNegativeButton("Cancel", null);
        dlgAlert.setPositiveButton("Add to favorites", this::openNickname);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void openNickname (DialogInterface a, int b) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name the Favorite");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = getLayoutInflater().inflate(R.layout.alert_name_input, (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0), false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.edittext_alert_input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendToFavorites(input.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void handleFavoriteAddOnPre () {
        mWaitListener.onWaitFragmentInteractionShow();
    }

    private void handleFavoriteAddOnPost (String result) {
        mWaitListener.onWaitFragmentInteractionHide();
    }

    private void handleFavoriteAddInError (String result) {
        mWaitListener.onWaitFragmentInteractionHide();
        Log.d("Favorites", result);
    }

    @Override
    public void onWaitFragmentInteractionShow() {

    }

    @Override
    public void onWaitFragmentInteractionHide() {

    }
}
