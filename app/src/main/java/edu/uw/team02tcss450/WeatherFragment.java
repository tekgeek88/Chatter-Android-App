package edu.uw.team02tcss450;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uw.team02tcss450.utils.GetAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWeatherFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment implements WaitFragment.OnFragmentInteractionListener{

    public static String TAG = "WEATHER_FRAG";

    private WaitFragment.OnFragmentInteractionListener mWaitListener;

    private String mJwt;

    private TextView[][] m10DayViews = new TextView[4][10];

    private TextView[] mTodayViews = new TextView[8];

    private LinearLayout m10DayLayout;

    private LinearLayout mTodayLayout;

    private ImageView[] mIcons = new ImageView[10];

    private TextView mLocationName;

    private EditText mInputText;

    private LatLng mLatLng;

    private String mLocation = "98404";

    private String mUnit = "f";

    private OnWeatherFragmentInteractionListener mListener;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        initMap();
        for (int i = 0; i < 10; i++){
            m10DayViews[0][i] = view.findViewById(getResources().getIdentifier("textview_fragment_weather_date_" + i, "id", getActivity().getPackageName()));
            m10DayViews[1][i] = view.findViewById(getResources().getIdentifier("textview_weather_high_" + i, "id", getActivity().getPackageName()));
            m10DayViews[2][i] = view.findViewById(getResources().getIdentifier("textview_fragment_weather_low_" + i, "id", getActivity().getPackageName()));
            m10DayViews[3][i] = view.findViewById(getResources().getIdentifier("textview_fragment_weather_state_" + i, "id", getActivity().getPackageName()));
            mIcons[i] = view.findViewById(getResources().getIdentifier("imageview_fragment_weather_icon_" + i, "id", getActivity().getPackageName()));
        }
        mTodayViews[todayEnum.get("low")] = view.findViewById(R.id.textview_fragment_weather_current_low);
        mTodayViews[todayEnum.get("high")] = view.findViewById(R.id.textview_fragment_weather_current_high);
        mTodayViews[todayEnum.get("chill")] = view.findViewById(R.id.textview_fragment_weather_current_chill);
        mTodayViews[todayEnum.get("description")] = view.findViewById(R.id.textview_fragment_weather_current_description);
        mTodayViews[todayEnum.get("sunrise")] = view.findViewById(R.id.textview_fragment_weather_current_sunrise);
        mTodayViews[todayEnum.get("sunset")] = view.findViewById(R.id.textview_fragment_weather_current_sunset);
        mTodayViews[todayEnum.get("speed")] = view.findViewById(R.id.textview_fragment_weather_current_wind_speed);
        mTodayViews[todayEnum.get("temp")] = view.findViewById(R.id.textview_fragment_weather_current_temp);
        m10DayLayout = view.findViewById(R.id.layout_fragment_weather_10_day);
        mTodayLayout = view.findViewById(R.id.layout_fragment_weather_today);
        mTodayLayout.setVisibility(View.GONE);
        mLocationName = view.findViewById(R.id.textview_fragment_weather_location);
        ImageButton searchButton = view.findViewById(R.id.imagebutton_fragment_weather_search);
        searchButton.setOnClickListener(this::onSearch);
        ImageButton mapButton = view.findViewById(R.id.imagebutton_fragment_weather_map);
        mapButton.setOnClickListener(this::openMap);
        Button dayButton = view.findViewById(R.id.button_weather_fragment_today);
        dayButton.setOnClickListener(this::showToday);
        dayButton = view.findViewById(R.id.button_weather_fragment_10_day);
        dayButton.setOnClickListener(this::show10Day);
        mInputText = view.findViewById(R.id.edittext_fragment_weather_search);
        getActivity().setTitle("Weather");
        return view;
    }

    private void reloadWeather () {
        reloadWeather(null);
    }

    private void reloadWeather (LatLng latLng) {
        Uri uri;
        if (latLng == null) {
            uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_forecast))
                    .appendQueryParameter(getString(R.string.keys_weather_location), mLocation)
                    .appendQueryParameter(getString(R.string.keys_weather_units), mUnit)
                    .build();
        } else {
            uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_forecast))
                    .appendQueryParameter(getString(R.string.keys_weather_latitude), Double.toString(mLatLng.latitude))
                    .appendQueryParameter(getString(R.string.keys_weather_longitude), Double.toString(mLatLng.longitude))
                    .appendQueryParameter(getString(R.string.keys_weather_units), mUnit)
                    .build();
        }
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
            mLatLng = getArguments().getParcelable(getString(R.string.keys_map_latlng));
            //Get the location
            //Get the unit
        }

        Log.d("MAP", Boolean.toString(mLatLng == null));
        if (mLatLng != null) {
            //Log.d("MAP", Double.toString(mLatLng.latitude));
            //Log.d("MAP", Double.toString(mLatLng.longitude));
        }
        reloadWeather(mLatLng);

    }

    private void onSearch(View v){
        mLocation = mInputText.getText().toString();
        //mInputText.setText("");
        reloadWeather();
    }

    private void openMap(View v) {
        mListener.onWeatherFragmentOpenMap(mLatLng);
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
        }if (context instanceof WaitFragment.OnFragmentInteractionListener) {
            mWaitListener = (WaitFragment.OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void show10Day (View v) {
        mTodayLayout.setVisibility(View.GONE);
        m10DayLayout.setVisibility(View.VISIBLE);
    }

    private void showToday (View v) {
        mTodayLayout.setVisibility(View.VISIBLE);
        m10DayLayout.setVisibility(View.GONE);
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
            for (int i = 0; i < forecast.length(); i++){
                temp[0] = forecast.getJSONObject(i).getString("day");
                temp[1] = "High " + forecast.getJSONObject(i).getString("high") + "\u00b0";
                temp[2] = "Low " + forecast.getJSONObject(i).getString("low") + "\u00b0";
                temp[3] = forecast.getJSONObject(i).getString("text");
                m10DayViews[0][i].setText(temp[0]);
                m10DayViews[1][i].setText(temp[1]);
                m10DayViews[2][i].setText(temp[2]);
                m10DayViews[3][i].setText(temp[3]);
            }
            mLocationName.setText(location.getString("city"));

            //Today
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

    @Override
    public void onWaitFragmentInteractionShow() {

    }

    @Override
    public void onWaitFragmentInteractionHide() {

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
