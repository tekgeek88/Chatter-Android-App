package edu.uw.team02tcss450;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uw.team02tcss450.utils.GetAsyncTask;
import edu.uw.team02tcss450.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */


public class ConditionsFragment extends Fragment implements WaitFragment.OnFragmentInteractionListener{

    private WaitFragment.OnFragmentInteractionListener mWaitListener;

    private Map<String, Integer> mIcons = new HashMap<>(8);

    private Map<Integer, String> mCodes = new HashMap<>(47);

    private LatLng mLatlng;

    private ImageView mCurrentImage;

    @Override
    public void onWaitFragmentInteractionShow() {

    }

    @Override
    public void onWaitFragmentInteractionHide() {

    }

    public static final String TAG = "CONDITIONS_FRAGMENT";

    enum Weather {
        Speed, Wind, Set, Rise, Temp, Description, High, Low, Location
    }

    private String mJwt = "";

    private  Map<Weather, TextView> mViews = new HashMap<Weather, TextView>();


    public ConditionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conditions, container, false);
        initIcons();
        initConditions();
        mViews.put(Weather.Speed, view.findViewById(R.id.textview_condition_fragment_speed));
        mViews.put(Weather.Wind, view.findViewById(R.id.textview_condition_fragment_wind));
        mViews.put(Weather.Set, view.findViewById(R.id.textview_condition_fragment_set));
        mViews.put(Weather.Rise, view.findViewById(R.id.textview_condition_fragment_rise));
        mViews.put(Weather.Temp, view.findViewById(R.id.textview_condition_fragment_current_temp));
        mViews.put(Weather.Description, view.findViewById(R.id.textview_condition_fragment_description));
        mViews.put(Weather.High, view.findViewById(R.id.textview_condition_fragment_high));
        mViews.put(Weather.Low, view.findViewById(R.id.textview_condition_fragment_low));
        mViews.put(Weather.Location, view.findViewById(R.id.textview_condition_fragment_location));
        mCurrentImage = view.findViewById(R.id.image_conditions_fragment_condition);

        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
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
        mCodes.put(12,"snowy");
        mCodes.put(13,"snowy");
        mCodes.put(14,"snowy");
        mCodes.put(15,"snowy");
        mCodes.put(16,"snowy");
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
        mCodes.put(39,"lightning");
        mCodes.put(40,"rainy");
        mCodes.put(41,"snowy");
        mCodes.put(42,"snowy");
        mCodes.put(43,"snowy");
        mCodes.put(44,"partly cloudy");
        mCodes.put(45,"lightning");
        mCodes.put(46,"snowy");
        mCodes.put(47,"lightning");
    }


    @Override
    public void onStart () {
        super.onStart();

        LatLng latLng = null;
        if (getArguments() != null) {
            mJwt = getArguments().getString(getString(R.string.keys_intent_jwt));
            if (getArguments().getParcelable(getString(R.string.keys_map_latlng)) != null){
                mLatlng = getArguments().getParcelable(getString(R.string.keys_map_latlng));
            }
        }

        reloadWeather(mLatlng);

    }


    private void handleWeatherOnPre () {
        mWaitListener.onWaitFragmentInteractionShow();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WaitFragment.OnFragmentInteractionListener) {
            mWaitListener = (WaitFragment.OnFragmentInteractionListener) context;
        }
    }

    public void reloadWeather () {
        reloadWeather(null);
    }

    public void reloadWeather (LatLng latLng) {
        mLatlng = (latLng != null) ? latLng : mLatlng;
        String location = "98404";
        String unit = "f";
        Uri uri;
        if (mLatlng == null) {
            uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_forecast))
                    .appendQueryParameter(getString(R.string.keys_weather_location), location)
                    .appendQueryParameter(getString(R.string.keys_weather_units), unit)
                    .build();
        } else {
            uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_forecast))
                    .appendQueryParameter(getString(R.string.keys_weather_latitude), Double.toString(mLatlng.latitude))
                    .appendQueryParameter(getString(R.string.keys_weather_longitude), Double.toString(mLatlng.longitude))
                    .appendQueryParameter(getString(R.string.keys_weather_units), unit)
                    .build();
        }
        new GetAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleWeatherOnPre)
                .onPostExecute(this::handleWeatherOnPost)
                .onCancelled(this::handleWeatherInError)
                .build().execute();
    }

    private void handleWeatherOnPost (String result) {
        //Log.d("Conditions post", result);
        try {
            JSONObject fullResult = new JSONObject(result);
            JSONObject location = fullResult.getJSONObject("location");//location
            JSONObject currentObs = fullResult.getJSONObject("current_observation");//current_observation
            JSONArray forecast = fullResult.getJSONArray("forecasts");//forecasts



            //Log.d("Conditions forecast", forecast.);
            String temp = location.getString("city") + ", " + location.getString("region");
            mViews.get(Weather.Location).setText(temp);
            temp = currentObs.getJSONObject("condition").getString("text");
            mViews.get(Weather.Description).setText(temp);
            temp = "Sunrise " + currentObs.getJSONObject("astronomy").getString("sunrise")
                    .substring(0,currentObs.getJSONObject("astronomy").getString("sunrise").length() - 2);
            mViews.get(Weather.Rise).setText(temp);
            temp = "Sunset " + currentObs.getJSONObject("astronomy").getString("sunset")
                    .substring(0,currentObs.getJSONObject("astronomy").getString("sunset").length() - 2);
            mViews.get(Weather.Set).setText(temp);
            temp = "Feels like " + currentObs.getJSONObject("wind").getString("chill") + "\u00b0";
            mViews.get(Weather.Wind).setText(temp);
            temp = currentObs.getJSONObject("wind").getString("speed") + " mph";
            mViews.get(Weather.Speed).setText(temp);
            temp = currentObs.getJSONObject("condition").getString("temperature") + "\u00b0";
            mViews.get(Weather.Temp).setText(temp);
            temp = "Low " + forecast.getJSONObject(0).getString("low") + "\u00b0";
            mViews.get(Weather.Low).setText(temp);
            temp = "High " + forecast.getJSONObject(0).getString("high") + "\u00b0";
            mViews.get(Weather.High).setText(temp);
            mCurrentImage.setImageResource(mIcons.get(mCodes.get(currentObs.getJSONObject("condition").getInt("code"))));

            mWaitListener.onWaitFragmentInteractionHide();

        } catch (JSONException e) {
            Log.e("Conditions", e.toString());
            e.printStackTrace();
        }

    }

    private void handleWeatherInError (String result) {
        Log.d("Conditions", result);
        mWaitListener.onWaitFragmentInteractionHide();
    }


}
