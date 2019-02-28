package edu.uw.team02tcss450;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import edu.uw.team02tcss450.utils.GetAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWeatherFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment {

    private String mJwt;

    private TextView[][] mViews = new TextView[4][10];

    private EditText mInputText;

    private String mLocation = "98404";

    private String mUnit = "f";

    private OnWeatherFragmentInteractionListener mListener;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        for (int i = 0; i < 10; i++){
            mViews[0][i] = view.findViewById(getResources().getIdentifier("textview_fragment_weather_date_" + i, "id", getActivity().getPackageName()));
            mViews[1][i] = view.findViewById(getResources().getIdentifier("textview_weather_high_" + i, "id", getActivity().getPackageName()));
            mViews[2][i] = view.findViewById(getResources().getIdentifier("textview_fragment_weather_low_" + i, "id", getActivity().getPackageName()));
            mViews[3][i] = view.findViewById(getResources().getIdentifier("textview_fragment_weather_state_" + i, "id", getActivity().getPackageName()));
        }
        ImageButton searchButton = view.findViewById(R.id.imagebutton_fragment_weather_search);
        searchButton.setOnClickListener(this::onSearch);
        mInputText = view.findViewById(R.id.edittext_fragment_weather_search);
        return view;
    }

    private void reloadWeather () {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_forecast))
                .appendQueryParameter(getString(R.string.keys_weather_location), mLocation)
                .appendQueryParameter(getString(R.string.keys_weather_units), mUnit)
                .build();

        new GetAsyncTask.Builder(uri.toString())
                .addHeaderField("authorization", mJwt)
                .onPreExecute(this::handleWeatherOnPre)
                .onPostExecute(this::handleWeatherOnPost)
                .onCancelled(this::handleWeatherInError)
                .build().execute();
    }

    @Override
    public void onStart () {
        super.onStart();

        if (getArguments() != null) {
            mJwt = getArguments().getString(getString(R.string.keys_intent_jwt));
            //Get the location
            //Get the unit
        }

        reloadWeather();

    }

    private void onSearch(View v){
        mLocation = mInputText.getText().toString();
        mInputText.setText("");
        reloadWeather();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onWeatherFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWeatherFragmentInteractionListener) {
            mListener = (OnWeatherFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    private void handleWeatherOnPre () {

    }

    private void handleWeatherOnPost (String result) {
        //Log.d("Conditions post", result);
        try {
            //Log.d("Weather", result);
            JSONObject fullResult = new JSONObject(result);
            if (fullResult.has("success") && fullResult.getString("success").equals("false")) {
                mInputText.setError(getString(R.string.text_fragment_weather_invalid_zip));
                return;
            } else {
                mInputText.setError(null);

            }
            JSONObject location = fullResult.getJSONObject("location");//location
            JSONObject currentObs = fullResult.getJSONObject("current_observation");//current_observation
            JSONArray forecast = fullResult.getJSONArray("forecasts");//forecasts



            //Log.d("Conditions forecast", forecast.);
//            temp = "Low " + forecast.getJSONObject(0).getString("low") + "\u00b0";
//            mViews.get(ConditionsFragment.Weather.Low).setText(temp);
//            temp = "High " + forecast.getJSONObject(0).getString("high") + "\u00b0";
//            mViews.get(ConditionsFragment.Weather.High).setText(temp);
            String temp[] = new String[4];
            for (int i = 0; i < 10; i++){
                temp[0] = forecast.getJSONObject(i).getString("day");
                temp[1] = "High " + forecast.getJSONObject(i).getString("high") + "\u00b0";
                temp[2] = "Low " + forecast.getJSONObject(i).getString("low") + "\u00b0";
                temp[3] = forecast.getJSONObject(i).getString("text");
                mViews[0][i].setText(temp[0]);
                mViews[1][i].setText(temp[1]);
                mViews[2][i].setText(temp[2]);
                mViews[3][i].setText(temp[3]);
            }
            //Log.d("Weather", Arrays.toString(temp));

            /**
             * day = 0
             * high = 1
             * low = 2
             * text = 3
             */


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
            Log.e("Weather", e.toString());
            e.printStackTrace();
        }

    }

    private void handleWeatherInError (String result) {
        Log.d("Weather", result);
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
    }
}
