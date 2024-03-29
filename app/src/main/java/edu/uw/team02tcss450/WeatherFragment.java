package edu.uw.team02tcss450;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.model.Favorite;
import edu.uw.team02tcss450.utils.DelAsyncTask;
import edu.uw.team02tcss450.utils.GetAsyncTask;
import edu.uw.team02tcss450.utils.PutAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWeatherFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment implements WaitFragment.OnFragmentInteractionListener, Favorite.OnFavoriteInteractionListener{

    public static String TAG = "WEATHER_FRAG";

    private WaitFragment.OnFragmentInteractionListener mWaitListener;

    private Favorite.OnFavoriteInteractionListener mFavListener;

    private String mJwt;

    private TextView[][] m10DayViews = new TextView[4][10];

    private TextView[][] mHourlyViews = new TextView[3][24];

    private TextView[] mTodayViews = new TextView[8];

    private ImageView[] mHourlyImages = new ImageView[24];

    private ImageView mCurrentImage;

    private ImageButton mFavButton;

    private ArrayList<Favorite> mFavorites = new ArrayList<>(10);

    private String mUsername;

    private LinearLayout m10DayLayout;

    private LinearLayout mTodayLayout;

    private LinearLayout mFavLayout;

    private ImageView[] m10DayImages = new ImageView[10];

    private TextView mLocationName;

    private EditText mInputText;

    private ImageButton mSearchButton;

    private Map<String, Integer> mIcons = new HashMap<>(8);

    private Map<Integer, String> mCodes = new HashMap<>(47);

    private Map<String, String> mHourlyCodes = new HashMap<>(12);

    private LatLng mLatLng;

    private String mLocation = "";

    private String mUnit = "f";

    private OnWeatherFragmentInteractionListener mListener;

    private LayoutInflater mInflater;

    private View mView;

    public WeatherFragment() {
        // Required empty public constructor
    }

    private Map<String, Integer> todayEnum = new HashMap<>(8);

    private void initMap () {
        todayEnum.put("low", 1);
        todayEnum.put("high", 2);
        todayEnum.put("chill", 3);
        todayEnum.put("speed", 4);
        todayEnum.put("temp", 5);
        todayEnum.put("description", 6);
        todayEnum.put("sunrise", 7);
        todayEnum.put("sunset", 0);
    }

    private void initIcons () {
        mIcons.put("sunny",R.drawable.ic_sunny);
        mIcons.put("rainy",R.drawable.ic_rain_day);
        mIcons.put("lightning",R.drawable.ic_lightining);
        mIcons.put("cloudy",R.drawable.ic_cloud_day);
        mIcons.put("breezy",R.drawable.ic_breezy);
        mIcons.put("partly cloudy",R.drawable.ic_partly_cloudy_day);
        mIcons.put("snowy",R.drawable.ic_snow_day);

    }

    private void initConditions () {
        mCodes.put(0,"breezy");
        mCodes.put(1,"lightning");
        mCodes.put(2,"breezy");
        mCodes.put(3,"lightning");
        mCodes.put(4,"lightning");
        mCodes.put(5,"snowy");
        mCodes.put(6,"rainy");
        mCodes.put(7,"snowy");
        mCodes.put(8,"rainy");
        mCodes.put(9,"rainy");
        mCodes.put(10,"rainy");
        mCodes.put(11,"rainy");
        mCodes.put(12,"rainy");
        mCodes.put(13,"snowy");
        mCodes.put(14,"snowy");
        mCodes.put(15,"snowy");
        mCodes.put(16,"rainy");
        mCodes.put(17,"rainy");
        mCodes.put(18,"rainy");
        mCodes.put(19,"sunny");
        mCodes.put(20,"cloudy");
        mCodes.put(21,"cloudy");
        mCodes.put(22,"cloudy");
        mCodes.put(23,"breezy");
        mCodes.put(24,"breezy");
        mCodes.put(25,"sunny");
        mCodes.put(26,"cloudy");
        mCodes.put(27,"partly cloudy");
        mCodes.put(28,"partly cloudy");
        mCodes.put(29,"partly cloudy");
        mCodes.put(30,"partly cloudy");
        mCodes.put(31,"sunny");
        mCodes.put(32,"sunny");
        mCodes.put(33,"sunny");
        mCodes.put(34,"sunny");
        mCodes.put(35,"rainy");
        mCodes.put(36,"sunny");
        mCodes.put(37,"lightning");
        mCodes.put(38,"lightning");
        mCodes.put(39,"rainy");
        mCodes.put(40,"rainy");
        mCodes.put(41,"snowy");
        mCodes.put(42,"snowy");
        mCodes.put(43,"snowy");
        mCodes.put(44,"partly cloudy");
        mCodes.put(45,"lightning");
        mCodes.put(46,"snowy");
        mCodes.put(47,"lightning");
    }

    private void initHourly () {
        mHourlyCodes.put("cloudy","cloudy");
        mHourlyCodes.put("clear-day","sunny");
        mHourlyCodes.put("partly-cloudy-day","partly cloudy");
        mHourlyCodes.put("rain","rainy");
        mHourlyCodes.put("partly-cloudy-night","partly cloudy");
        //mHourlyCodes.put("cloudy","cloudy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater;
        mView = inflater.inflate(R.layout.fragment_weather, container, false);
        initMap();
        initIcons();
        initConditions();
        initHourly();
        for (int i = 0; i < m10DayViews[0].length; i++){
            m10DayViews[0][i] = mView.findViewById(getResources().getIdentifier("textview_fragment_weather_date_" + i, "id", getActivity().getPackageName()));
            m10DayViews[1][i] = mView.findViewById(getResources().getIdentifier("textview_weather_high_" + i, "id", getActivity().getPackageName()));
            m10DayViews[2][i] = mView.findViewById(getResources().getIdentifier("textview_fragment_weather_low_" + i, "id", getActivity().getPackageName()));
            m10DayViews[3][i] = mView.findViewById(getResources().getIdentifier("textview_fragment_weather_state_" + i, "id", getActivity().getPackageName()));
            m10DayImages[i] = mView.findViewById(getResources().getIdentifier("imageview_fragment_weather_icon_" + i, "id", getActivity().getPackageName()));
        }
        for (int i = 0; i < mHourlyViews[0].length; i++) {
            mHourlyViews[0][i] = mView.findViewById(getResources().getIdentifier("textview_fragment_weather_hourly_time_" + i, "id", getActivity().getPackageName()));
            mHourlyViews[1][i] = mView.findViewById(getResources().getIdentifier("textview_fragment_weather_hourly_high_" + i, "id", getActivity().getPackageName()));
            mHourlyViews[2][i] = mView.findViewById(getResources().getIdentifier("textview_fragment_weather_hourly_low_" + i, "id", getActivity().getPackageName()));
            mHourlyImages[i] = mView.findViewById(getResources().getIdentifier("imageview_fragment_weather_hourly_icon_" + i, "id", getActivity().getPackageName()));
        }
        mCurrentImage = mView.findViewById(R.id.imageview_fragment_weather_current_condition);
        mTodayViews[todayEnum.get("low")] = mView.findViewById(R.id.textview_fragment_weather_current_low);
        mTodayViews[todayEnum.get("high")] = mView.findViewById(R.id.textview_fragment_weather_current_high);
        mTodayViews[todayEnum.get("chill")] = mView.findViewById(R.id.textview_fragment_weather_current_chill);
        mTodayViews[todayEnum.get("description")] = mView.findViewById(R.id.textview_fragment_weather_current_description);
        mTodayViews[todayEnum.get("sunrise")] = mView.findViewById(R.id.textview_fragment_weather_current_sunrise);
        mTodayViews[todayEnum.get("sunset")] = mView.findViewById(R.id.textview_fragment_weather_current_sunset);
        mTodayViews[todayEnum.get("speed")] = mView.findViewById(R.id.textview_fragment_weather_current_wind_speed);
        mTodayViews[todayEnum.get("temp")] = mView.findViewById(R.id.textview_fragment_weather_current_temp);
        m10DayLayout = mView.findViewById(R.id.layout_fragment_weather_10_day);
        mTodayLayout = mView.findViewById(R.id.layout_fragment_weather_today);
        mFavLayout = mView.findViewById(R.id.layout_fragment_weather_favorites);
        mFavLayout.setVisibility(View.GONE);
        mTodayLayout.setVisibility(View.VISIBLE);
        m10DayLayout.setVisibility(View.GONE);
        mLocationName = mView.findViewById(R.id.textview_fragment_weather_location);
        mSearchButton = mView.findViewById(R.id.imagebutton_fragment_weather_search);
        mSearchButton.setOnClickListener(this::onSearch);
        ImageButton mapButton = mView.findViewById(R.id.imagebutton_fragment_weather_map);
        mapButton.setOnClickListener(this::openMap);
        Button dayButton = mView.findViewById(R.id.button_weather_fragment_today);
        dayButton.setOnClickListener(this::showToday);
        dayButton = mView.findViewById(R.id.button_weather_fragment_10_day);
        dayButton.setOnClickListener(this::show10Day);
        mFavButton = mView.findViewById(R.id.imagebutton_fragment_weather_favorites);
        mFavButton.setOnClickListener(this::showFavorites);
        mInputText = mView.findViewById(R.id.edittext_fragment_weather_search);
        getActivity().setTitle(getString(R.string.text_fragment_weather_title));
        return mView;
    }

    private void reloadWeather (LatLng latLng) {
        if (null != latLng) {
            Uri uri10Day;
            Uri uriHourly;
            mLatLng = latLng;
            uri10Day = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_forecast))
                    .appendQueryParameter(getString(R.string.keys_weather_latitude), Double.toString(latLng.latitude))
                    .appendQueryParameter(getString(R.string.keys_weather_longitude), Double.toString(latLng.longitude))
                    .appendQueryParameter(getString(R.string.keys_weather_units), mUnit)
                    .build();
            uriHourly = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_forecast))
                    .appendPath(getString(R.string.ep_hourly))
                    .appendQueryParameter(getString(R.string.keys_weather_latitude), Double.toString(latLng.latitude))
                    .appendQueryParameter(getString(R.string.keys_weather_longitude), Double.toString(latLng.longitude))
                    .appendQueryParameter(getString(R.string.keys_weather_units), mUnit)
                    .build();

            new GetAsyncTask.Builder(uri10Day.toString())
                    .addHeaderField("authorization", mJwt)
                    .onPreExecute(this::handleWeatherOnPre)
                    .onPostExecute(this::handleWeatherOnPost)
                    .onCancelled(this::handleWeatherInError)
                    .build().execute();
            new GetAsyncTask.Builder(uriHourly.toString())
                    .addHeaderField("authorization", mJwt)
                    .onPreExecute(this::handleHourlyOnPre)
                    .onPostExecute(this::handleHourlyOnPost)
                    .onCancelled(this::handleHourlyInError)
                    .build().execute();

            loadFavorites();
        }
    }

    private void reloadWeather (String zipcode) {
        Uri uri10Day;
        Uri uriHourly;
        mLocation = zipcode;
        uri10Day = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_forecast))
                .appendQueryParameter(getString(R.string.keys_weather_location), zipcode)
                .appendQueryParameter(getString(R.string.keys_weather_units), mUnit)
                .build();
        uriHourly = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_forecast))
                .appendPath(getString(R.string.ep_hourly))
                .appendQueryParameter(getString(R.string.keys_weather_location), zipcode)
                .appendQueryParameter(getString(R.string.keys_weather_units), mUnit)
                .build();

        new GetAsyncTask.Builder(uri10Day.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleWeatherOnPre)
                .onPostExecute(this::handleWeatherOnPost)
                .onCancelled(this::handleWeatherInError)
                .build().execute();
        new GetAsyncTask.Builder(uriHourly.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleHourlyOnPre)
                .onPostExecute(this::handleHourlyOnPost)
                .onCancelled(this::handleHourlyInError)
                .build().execute();
        loadFavorites();

    }

    private void loadFavorites () {
        Log.d("FAVORITES", "Load favorites");
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_locations))
                .appendQueryParameter(getString(R.string.keys_weather_username), mUsername)
                .build();

        new GetAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleFavoriteGetOnPre)
                .onPostExecute(this::handleFavoriteGetOnPost)
                .onCancelled(this::handleFavoriteGetInError)
                .build().execute();
    }

    @Override
    public void onStart () {
        super.onStart();

        if (getArguments() != null) {
            mJwt = getArguments().getString(getString(R.string.keys_intent_jwt));
            mLatLng = getArguments().getParcelable(getString(R.string.keys_map_latlng));
            //Get the unit
        }
        Credentials credentials = (Credentials) getActivity().getIntent()
                .getExtras().getSerializable(getActivity().getString(R.string.keys_intent_credentials));
        mUsername = credentials.getUsername();

        Log.d("MAP", Boolean.toString(mLatLng == null));
        if (mLatLng != null) {
            //Log.d("MAP", Double.toString(mLatLng.latitude));
            //Log.d("MAP", Double.toString(mLatLng.longitude));
        }
        reloadWeather(mLatLng);
        loadFavorites();

    }

    private void onSearch(View v){
        mLocation = mInputText.getText().toString();
        //mInputText.setText("");
        reloadWeather(mLocation);
    }

    private void openMap(View v) {
        Log.d("FAVORITES3", mLatLng.toString());
        Log.d("FAVORITES3", mLocation);
        mListener.onWeatherFragmentOpenMap(mLatLng);
    }

    private void addToFavorites (LatLng latLng, String nickname) {
        //Database call to add zip or latlng to database
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_locations))
                .appendQueryParameter(getString(R.string.keys_weather_username), mUsername)
                .appendQueryParameter(getString(R.string.keys_weather_latitude), Double.toString(latLng.latitude))
                .appendQueryParameter(getString(R.string.keys_weather_longitude), Double.toString(latLng.longitude))
                .appendQueryParameter(getString(R.string.keys_weather_nickname), nickname)
                .build();

        new PutAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleFavoriteAddOnPre)
                .onPostExecute(this::handleFavoriteAddOnPost)
                .onCancelled(this::handleFavoriteAddInError)
                .build().execute();
    }

    private void addToFavorites (String zip, String nickname) {
        //Database call to add zip or latlng to database
        //username:::zipcode:::nickname
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_locations))
                .appendQueryParameter(getString(R.string.keys_weather_username), mUsername)
                .appendQueryParameter(getString(R.string.keys_weather_location), zip)
                .appendQueryParameter(getString(R.string.keys_weather_nickname), nickname)
                .build();

        new PutAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleFavoriteAddOnPre)
                .onPostExecute(this::handleFavoriteAddOnPost)
                .onCancelled(this::handleFavoriteAddInError)
                .build().execute();

    }

    private void removeFromFavorites (String nickname) {
        //Database call to remove nickname from database
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_locations))
                .appendQueryParameter(getString(R.string.keys_weather_username), mUsername)
                .appendQueryParameter(getString(R.string.keys_weather_nickname), nickname)
                .build();

        new DelAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleFavoriteRemoveOnPre)
                .onPostExecute(this::handleFavoriteRemoveOnPost)
                .onCancelled(this::handleFavoriteRemoveInError)
                .build().execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWeatherFragmentInteractionListener) {
            mListener = (OnWeatherFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherFragmentInteractionListener");
        }if (context instanceof WaitFragment.OnFragmentInteractionListener) {
            mWaitListener = (WaitFragment.OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onFavorite (View v) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
        dlgAlert.setMessage("Are you sure you want to add this location to your favorites?");
        dlgAlert.setTitle("Add to favorites");
        dlgAlert.setNegativeButton("Cancel", null);
        dlgAlert.setPositiveButton("Yes", this::getNickname);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void getNickname (DialogInterface a, int b) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pick a Nick");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.alert_name_input, (ViewGroup) getView(), false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.edittext_alert_input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);
        builder.setMessage("Input nickname of location");
// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!mInputText.getText().toString().equals("")) {
                    addToFavorites(mInputText.getText().toString(),input.getText().toString());
                } else {
                    addToFavorites(mLatLng, input.getText().toString());
                }
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

    private void show10Day (View v) {
        setLayoutsDefault();
        m10DayLayout.setVisibility(View.VISIBLE);
    }

    private void showToday (View v) {
        setLayoutsDefault();
        mTodayLayout.setVisibility(View.VISIBLE);
    }

    private void showFavorites (View v) {
        setLayoutsDefault();
        mFavButton.setVisibility(View.GONE);
        mFavButton.setClickable(false);
        mFavLayout.setVisibility(View.VISIBLE);
        mSearchButton.setImageResource(R.drawable.ic_favorite_red);
        mSearchButton.setOnClickListener(this::onFavorite);
        mInputText.setHint(getString(R.string.text_fragment_weather_text_add_to));
        Log.d("the JWT", mJwt.toString());
    }

    private void setLayoutsDefault () {
        mTodayLayout.setVisibility(View.GONE);
        m10DayLayout.setVisibility(View.GONE);
        mFavLayout.setVisibility(View.GONE);
        mSearchButton.setImageResource(R.drawable.ic_search);
        mSearchButton.setOnClickListener(this::onSearch);
        mInputText.setHint(R.string.text_fragment_weather_zip);
        mFavButton.setVisibility(View.VISIBLE);
        mFavButton.setClickable(true);

    }


    private void handleWeatherOnPre () {
        mWaitListener.onWaitFragmentInteractionShow();
    }

    private void handleWeatherOnPost (String result) {
        //Log.d("Conditions post", result);
        try {
            //Log.d("Weather", result);
            JSONObject fullResult = new JSONObject(result);
            if (fullResult.has("success") && fullResult.getString("success").equals("false")) {
                mInputText.setError(getString(R.string.text_fragment_weather_invalid_zip));
                mWaitListener.onWaitFragmentInteractionHide();
                return;
            } else {
                mInputText.setError(null);

            }
            JSONObject location = fullResult.getJSONObject("location");//location
            JSONObject currentObs = fullResult.getJSONObject("current_observation");//current_observation
            JSONArray forecast = fullResult.getJSONArray("forecasts");//forecasts

            //10 Day
            String temp[] = new String[4];
            JSONObject day;
            for (int i = 0; i < forecast.length(); i++){
                day = forecast.getJSONObject(i);
                temp[0] = day.getString("day");
                temp[1] = "High " + day.getString("high") + "\u00b0";
                temp[2] = "Low " + day.getString("low") + "\u00b0";
                temp[3] = day.getString("text");
                m10DayViews[0][i].setText(temp[0]);
                m10DayViews[1][i].setText(temp[1]);
                m10DayViews[2][i].setText(temp[2]);
                m10DayViews[3][i].setText(temp[3]);
                m10DayImages[i].setImageResource(mIcons.get(mCodes.get(day.getInt("code"))));
                Log.d("Weather", "Code: " + day.getInt("code"));
            }
            mLocationName.setText(location.getString("city"));

            //Today-current
            String temp2 = currentObs.getJSONObject("condition").getString("text");
            mTodayViews[todayEnum.get("description")].setText(temp2);
            temp2 = "Sunrise " + currentObs.getJSONObject("astronomy").getString("sunrise")
                    .substring(0,currentObs.getJSONObject("astronomy").getString("sunrise").length() - 2);
            mTodayViews[todayEnum.get("sunrise")].setText(temp2);
            temp2 = "Sunset " + currentObs.getJSONObject("astronomy").getString("sunset")
                    .substring(0,currentObs.getJSONObject("astronomy").getString("sunset").length() - 2);
            mTodayViews[todayEnum.get("sunset")].setText(temp2);
            temp2 = "Feels like " + currentObs.getJSONObject("wind").getString("chill") + "\u00b0";
            mTodayViews[todayEnum.get("chill")].setText(temp2);
            temp2 = currentObs.getJSONObject("wind").getString("speed") + " mph";
            mTodayViews[todayEnum.get("speed")].setText(temp2);
            temp2 = currentObs.getJSONObject("condition").getString("temperature") + "\u00b0";
            mTodayViews[todayEnum.get("temp")].setText(temp2);
            temp2 = "Low " + forecast.getJSONObject(0).getString("low") + "\u00b0";
            mTodayViews[todayEnum.get("low")].setText(temp2);
            temp2 = "High " + forecast.getJSONObject(0).getString("high") + "\u00b0";
            mTodayViews[todayEnum.get("high")].setText(temp2);
            //Log.d("Weather", "Current: " + currentObs.toString());
            mCurrentImage.setImageResource(mIcons.get(mCodes.get(currentObs.getJSONObject("condition").getInt("code"))));



            mWaitListener.onWaitFragmentInteractionHide();


            /**
             * day = 0
             * high = 1
             * low = 2
             * text = 3
             */

        } catch (JSONException e) {
            mWaitListener.onWaitFragmentInteractionHide();
            Log.e("Weather", e.toString());
            e.printStackTrace();
        }
    }

    private void handleWeatherInError (String result) {
        mWaitListener.onWaitFragmentInteractionHide();
        Log.d("Weather", result);
    }

    private void handleFavoriteAddOnPre () {
        mWaitListener.onWaitFragmentInteractionShow();
    }

    private void handleFavoriteAddOnPost (String result) {
        mWaitListener.onWaitFragmentInteractionHide();
        loadFavorites();
    }

    private void handleFavoriteAddInError (String result) {
        mWaitListener.onWaitFragmentInteractionHide();
        Log.d("Favorites", result);
    }

    private void handleFavoriteGetOnPre () {
        mWaitListener.onWaitFragmentInteractionShow();
    }

    private void handleFavoriteGetOnPost (String result) {
//        mWaitListener.onWaitFragmentInteractionHide();
        try {
            JSONObject fullResult = new JSONObject(result);
            if (fullResult != null && fullResult.getString("status").equals("success")) {
                JSONArray data = fullResult.getJSONArray("data");
                JSONObject piece;
                Log.d("FAVORITES2", result);

                ScrollView scroller = mView.findViewById(R.id.scrollview_fragment_weather_list);
                LinearLayout main = mView.findViewById(R.id.layout_fragment_weather_favorites);
                main.removeAllViews();


                Favorite aFav;
                ImageButton aDelete;
                TextView aName;
                ConstraintLayout aLayout;
                double a = 0,b = 0, zip = 0;
                for(int i=0;i<data.length();i++){
                    a = 0;b=0;zip=0;
                    piece = data.getJSONObject(i);
                    View view = mInflater.inflate(R.layout.fragment_weather_favorite_single, null);
                    aDelete = view.findViewById(R.id.imagebutton_fragment_weather_favorite_delete);
                    aName = view.findViewById(R.id.textview_fragment_weather_favorite_name);
                    aLayout = view.findViewById(R.id.constraint_layout_fragment_weather_favorite_single);
                    if (!piece.get(getString(R.string.keys_favorite_latitude)).toString().equals("null")) {
                        a = piece.getDouble(getString(R.string.keys_favorite_latitude));
                        b = piece.getDouble(getString(R.string.keys_favorite_longitude));
                    }
                    if (!piece.get(getString(R.string.keys_favorite_zipcode)).toString().equals("null")) {
                        zip = piece.getDouble(getString(R.string.keys_favorite_zipcode));
                    }
                    aFav = new Favorite.Builder(piece.getString(getString(R.string.keys_favorite_nickname)),aName,aDelete,aLayout)
                            .LatLng(a,b)
                            .zipcode(zip)
                            .build();
                    aFav.onAttach(this);
                    main.addView(view);
                    mFavorites.add(aFav);
                    Log.d("FAVORITES", "Loaded a favorite:\t" + aFav.getZip());
                }
                Log.d("FAVORITES", "Close wait fragment");
                mWaitListener.onWaitFragmentInteractionHide();
                //scroller.
            }
        } catch (JSONException e) {
            Log.d("FAVORITES2", e.toString());
            e.printStackTrace();
        }
    }

    private void handleFavoriteGetInError (String result) {
        mWaitListener.onWaitFragmentInteractionHide();
        Log.d("Favorites", "GetInError : " + result);
    }

    private void handleFavoriteRemoveInError(String result) {
        mWaitListener.onWaitFragmentInteractionHide();
        Log.d("Favorites", result);
    }

    private void handleFavoriteRemoveOnPost(String result) {
        loadFavorites();
        mWaitListener.onWaitFragmentInteractionHide();
    }

    private void handleFavoriteRemoveOnPre() {
        mWaitListener.onWaitFragmentInteractionShow();
    }

    private void handleHourlyOnPre () {
        mWaitListener.onWaitFragmentInteractionShow();
    }

    private void handleHourlyOnPost (String result) {
        try {
            //Log.d("Weather", result);
            JSONObject fullResult = new JSONObject(result);
            if (fullResult.has("success") && fullResult.getString("success").equals("false")) {
                mInputText.setError(getString(R.string.text_fragment_weather_invalid_zip));
                mWaitListener.onWaitFragmentInteractionHide();
                return;
            } else {
                mInputText.setError(null);

            }
            JSONObject hourly = fullResult.getJSONObject("hourly");//location
            JSONArray data = hourly.getJSONArray("data");//forecasts

            String temp;
            JSONObject object;
            Calendar calendar = Calendar.getInstance();
            long time;
            Date date;
            for (int i = 0; i < data.length() && i < mHourlyViews[0].length; i++){
                object = data.getJSONObject(i);
                time = object.getLong("time") * 1000;
                date = new Date(time);
                calendar.setTime(date);
                temp = calendar.get(Calendar.HOUR_OF_DAY) + ":00";
                mHourlyViews[0][i].setText(temp);
                temp = Math.round(object.getDouble("temperature")) + "\u00b0";
                mHourlyViews[1][i].setText(temp);
                temp = (int)Math.floor(object.getDouble("precipProbability")*100) + "%";
                mHourlyViews[2][i].setText(temp);
                if (mHourlyCodes.containsKey(object.getString("icon"))){
                mHourlyImages[i].setImageResource(mIcons.get(mHourlyCodes.get(object.getString("icon"))));
                } else {
                    mHourlyImages[i].setImageResource(mIcons.get("sunny"));
                }
                //Log.d("Weather", "Hourly: " + object.getString("icon"));
            }



            /**
             * 0 = time
             * 1 = high
             * 2 = low or now humidity or precip or wind speed
             *  :humidity: :precipProbability: :windSpeed:
             */
            /*
            {
                "hourly": {
                    "data":[{
                        "time": 1552089600,
                        "summary": "Mostly Cloudy",
                        "icon": "partly-cloudy-day",
                        "precipIntensity": 0.0011,
                        "precipProbability": 0.01,
                        "precipType": "sleet",
                        "temperature": 38.79,
                        "apparentTemperature": 38.79,
                        "dewPoint": 34.44,
                        "humidity": 0.84,
                        "pressure": 1014.78,
                        "windSpeed": 2.46,
                        "windGust": 4.15,
                        "windBearing": 248,
                        "cloudCover": 0.69,
                        "uvIndex": 1,
                        "visibility": 8.98,
                        "ozone": 414.19
                    },]
                }
            }

             */


            mWaitListener.onWaitFragmentInteractionHide();




        } catch (JSONException e) {
            mWaitListener.onWaitFragmentInteractionHide();
            Log.e("Weather", e.toString());
            e.printStackTrace();
        }
    }

    private void handleHourlyInError (String result) {
        mWaitListener.onWaitFragmentInteractionHide();
        Log.d("Favorites", result);
    }


    @Override
    public void onWaitFragmentInteractionShow() {

    }

    @Override
    public void onWaitFragmentInteractionHide() {

    }

    @Override
    public void onReloadFavorites() {
        loadFavorites();
    }

    @Override
    public void onLoadFavorite(LatLng latLng) {
        reloadWeather(latLng);
        showToday(null);
    }

    @Override
    public void onLoadFavorite(String zipcode) {
        reloadWeather(zipcode);
        showToday(null);
    }

    @Override
    public void onDeleteFavorite(String nickname) {
        removeFromFavorites(nickname);
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
    public interface OnWeatherFragmentInteractionListener {
        // TODO: Update argument type and name
        void onWeatherFragmentInteraction(Uri uri);
        void onWeatherFragmentOpenMap(LatLng location);
    }
}
