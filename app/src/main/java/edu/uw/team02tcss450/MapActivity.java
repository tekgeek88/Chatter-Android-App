package edu.uw.team02tcss450;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String mJwt;
    private Credentials mCredentials;
    private LatLng mLocation;
    private Marker mMarker;
    private LatLng mLastLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (savedInstanceState == null) {
            mCredentials = (Credentials) getIntent()
                    .getExtras().getSerializable(getString(R.string.keys_intent_credentials));
            mJwt = getIntent().getExtras().getString(getString(R.string.keys_intent_jwt));
            mLocation = getIntent().getExtras().getParcelable(getString(R.string.keys_map_latlng));
            mLastLocation = mLocation;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton button = findViewById(R.id.imagebutton_activity_map_return);
        button.setOnClickListener(this::goBack);
    }


    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack () {
        goBack(null);
    }

    private void goBack (View v) {
        if (mMarker == null){
            mLocation = mLastLocation;
        }
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra(getString(R.string.keys_intent_credentials), (Serializable) mCredentials);
        i.putExtra(getString(R.string.keys_intent_jwt), mJwt);
        i.putExtra(getString(R.string.keys_map_latlng), mLocation);
        i.putExtra(getString(R.string.keys_intent_fragment_tag), WeatherFragment.TAG);
        //i.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        startActivity(i);
        //End this Activity and remove it from the Activity back stack.

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
//        Log.d("LAT/LONG", latLng.toString());
//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .draggable(true)
//                .position(latLng)
//                .title("Lat: " + latLng.latitude + ", Long: " + latLng.longitude));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
//        Log.d("LAT/LONG", latLng.toString());
//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .draggable(true)
//                .rotation((float)Math.PI/2)
//                .position(latLng)
//                .title("Long click!"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
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



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 10));
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    private void onRemoveMarker (DialogInterface a, int b) {
        mMarker.remove();
        mLocation = mLastLocation;
    }

    private void addToLocationPref (DialogInterface a, int b) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Remove marker to not update weather location\nAdd to favorites to keep this location");
        dlgAlert.setTitle("Marker Setup");
        dlgAlert.setPositiveButton("Remove", this::onRemoveMarker);
        dlgAlert.setNegativeButton("Cancel", null);
        dlgAlert.setNeutralButton("Add to favorites", this::addToLocationPref);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
        return false;
    }
}
