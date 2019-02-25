package edu.uw.team02tcss450;


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


public class ConditionsFragment extends Fragment {

    enum Weather {
        Speed, Wind, Set, Rise, Temp, Description, High, Low, Location
    }


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
        //build the web service URL
        //https://team02-tcss450-backend.herokuapp.com/
        //weather/
        //forecast?
        // location=98335&u=f
        String location = "98404";
        String unit = "f";
        String temp = "?"+getString(R.string.keys_weather_location)+"="+location
                +"&"+getString(R.string.keys_weather_units)+"="+unit;
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_forecast))
                .appendEncodedPath(temp)
                .build();
        //Log.d("Conditions pre", uri.toString());
        //build the JSONObject


        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(this::handleWeatherOnPre)
                .onPostExecute(this::handleWeatherOnPost)
                .onCancelled(this::handleWeatherInError)
                .build().execute();

    }


    private void handleWeatherOnPre () {

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


/**
 * "location": {
 *         "woeid": 12799101,
 *         "city": "Gig Harbor",
 *         "region": " WA",
 *         "country": "United States",
 *         "lat": 47.299831,
 *         "long": -122.617188,
 *         "timezone_id": "America/Los_Angeles"
 *     },
 *     "current_observation": {
 *         "wind": {
 *             "chill": 39,
 *             "direction": 200,
 *             "speed": 5.59
 *         },
 *         "atmosphere": {
 *             "humidity": 69,
 *             "visibility": 10,
 *             "pressure": 29.94,
 *             "rising": 0
 *         },
 *         "astronomy": {
 *             "sunrise": "7:02 am",
 *             "sunset": "5:47 pm"
 *         },
 *         "condition": {
 *             "text": "Showers",
 *             "code": 11,
 *             "temperature": 43
 *         },
 *         "pubDate": 1550966400
 *     },"forecasts": [
 *         {
 *             "day": "Sat",
 *             "date": 1550908800,
 *             "low": 35,
 *             "high": 44,
 *             "text": "Showers",
 *             "code": 11
 *         },
 */






        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleWeatherInError (String result) {
        Log.d("Conditions", result);
    }


}
