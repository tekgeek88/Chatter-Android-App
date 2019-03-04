package edu.uw.team02tcss450;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        mViews.put(Weather.Speed, view.findViewById(R.id.textview_condition_fragment_speed));
        mViews.put(Weather.Wind, view.findViewById(R.id.textview_condition_fragment_wind));
        mViews.put(Weather.Set, view.findViewById(R.id.textview_condition_fragment_set));
        mViews.put(Weather.Rise, view.findViewById(R.id.textview_condition_fragment_rise));
        mViews.put(Weather.Temp, view.findViewById(R.id.textview_condition_fragment_current_temp));
        mViews.put(Weather.Description, view.findViewById(R.id.textview_condition_fragment_description));
        mViews.put(Weather.High, view.findViewById(R.id.textview_condition_fragment_high));
        mViews.put(Weather.Low, view.findViewById(R.id.textview_condition_fragment_low));
        mViews.put(Weather.Location, view.findViewById(R.id.textview_condition_fragment_location));

        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
    }


    @Override
    public void onStart () {
        super.onStart();

        if (getArguments() != null) {
            mJwt = getArguments().getString(getString(R.string.keys_intent_jwt));
        }

        //build the web service URL
        //https://team02-tcss450-backend.herokuapp.com/
        //weather/
        //forecast?
        // location=98335&u=f
        String location = "98404";
        String unit = "f";
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_forecast))
                .appendQueryParameter(getString(R.string.keys_weather_location), location)
                .appendQueryParameter(getString(R.string.keys_weather_units), unit)
                .build();
        //Log.d("Conditions pre", uri.toString());
        //build the JSONObject


        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new GetAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleWeatherOnPre)
                .onPostExecute(this::handleWeatherOnPost)
                .onCancelled(this::handleWeatherInError)
                .build().execute();

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

            mWaitListener.onWaitFragmentInteractionHide();

        } catch (JSONException e) {
            Log.e("Conditions", e.toString());
            e.printStackTrace();
        }

    }

    private void handleWeatherInError (String result) {
        Log.d("Conditions", result);
    }


}
