package edu.uw.team02tcss450;


import android.app.ActionBar;
import android.content.Context;

import edu.uw.team02tcss450.model.Connections;
import edu.uw.team02tcss450.model.Credentials;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.utils.SendPostAsyncTask;
import me.pushy.sdk.Pushy;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Zebin Zhou
 * @version 13 January 2019
 */
public class LoginFragment extends Fragment {

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
    public interface OnLoginFragmentInteractionListener extends
            WaitFragment.OnFragmentInteractionListener {

        void onLoginSuccess(Credentials user_name_password, String jwt);

        void onRegisterClicked();

        void OnForgotPasswordClicked();
    }


    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            String deviceToken = "";

            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());

                //subscribe to a topic (this is a Blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            } catch (Exception exc) {

                cancel(true);
                // Return exc to onCancelled
                return exc.getMessage();
            }

            // Success
            return deviceToken;
        }

        @Override
        protected void onCancelled(String errorMsg) {
            super.onCancelled(errorMsg);
            Log.d("PhishApp", "Error getting Pushy Token: " + errorMsg);
        }

        @Override
        protected void onPostExecute(String deviceToken) {
            // Log it for debugging purposes
            Log.d("PhishApp", "Pushy device token: " + deviceToken);

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_pushy))
                    .appendPath(getString(R.string.ep_token))
                    .build();

            //build the JSONObject
            JSONObject msg = mCredentials.asJSONObject();

            try {
                msg.put("token", deviceToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(LoginFragment.this::handlePushyTokenOnPost)
                    .onCancelled(LoginFragment.this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwt)
                    .build().execute();

        }
    }


    // Declare member variables
    private OnLoginFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mJwt;
    private boolean mRememberMe = false;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
//        ActionBar ab = getActivity().getActionBar();
//        ab.setTitle("");

        Button b = (Button) v.findViewById(R.id.btn_fragment_login_register);
        b.setOnClickListener(this::attemptRegister);

        b = (Button) v.findViewById(R.id.btn_fragment_login_signin);
        b.setOnClickListener(this::attemptLogin);

        b = (Button) v.findViewById(R.id.btn_fragment_login_forgot_password);
        b.setOnClickListener(view -> mListener.OnForgotPasswordClicked());

        CheckBox box = (CheckBox) v.findViewById(R.id.checkBox_fragment_login_rememberMe);
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRememberMe = true;
            }
        });


        // Disable the ActionBar
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {

            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.edittext_fragment_login_email);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.edittext_fragment_login_password);
            passwordEdit.setText(password);

            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
    }


    private void attemptRegister(View view) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
    }

    private void attemptLogin(final View theButton) {

        EditText emailEdit = getActivity().findViewById(R.id.edittext_fragment_login_email);
        EditText passwordEdit = getActivity().findViewById(R.id.edittext_fragment_login_password);
        boolean hasError = false;

        if (emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        } else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address.");
        }

        if (passwordEdit.getText().length() == 0) {
            hasError = true;
            passwordEdit.setError("Field must not be empty.");
        }

        if (passwordEdit.getText().length() < 6) {
            hasError = true;
            passwordEdit.setError("Field must not be empty.");
        }

        if (!hasError) {
            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
    }


    private void doLogin(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        mCredentials = credentials;

        Log.d("JSON Credentials", msg.toString());

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }


    public void updateContent(Credentials credentials) {
        EditText textView_email = getActivity().findViewById(R.id.edittext_fragment_login_email);
        textView_email.setText(credentials.getEmail());

        EditText textView_password = getActivity().findViewById(R.id.edittext_fragment_login_password);
        textView_password.setText(credentials.getPassword());
    }


    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));
            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                new RegisterForPushNotificationsAsync().execute();

                mJwt = resultsJSON.getString(
                        getString(R.string.keys_json_login_jwt));
                if (resultsJSON.has("data")) {
                    JSONObject resultsJSONJSONObject = resultsJSON.getJSONObject("data");
                    //JSONObject data = resultsJSON.getJSONObject("data");
                  // JSONArray jsonConnection = data.getJSONArray("data");
                   Credentials.Builder builder =  new Credentials.Builder(mCredentials.getEmail(),
                                mCredentials.getPassword());
                   builder.addUsername(resultsJSONJSONObject.getString("username")).build();


                   mCredentials = builder.build();
                }
                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
                String msg = resultsJSON.getString("msg");
                ((TextView) getActivity().findViewById(R.id.edittext_fragment_login_email)).setError(msg);
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
        }
    }


    private void handlePushyTokenOnPost(String result) {
        try {

            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");


            if (success && mRememberMe) {
                saveCredentials(mCredentials);
                mListener.onLoginSuccess(mCredentials, mJwt);
                return;
            } else if (success) {
                mListener.onLoginSuccess(mCredentials, mJwt);
                return;

            } else {
                //Saving the token wrong. Don’t switch fragments and inform the user
                ((EditText) getView().findViewById(R.id.edittext_fragment_login_email))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((EditText) getView().findViewById(R.id.edittext_fragment_login_email))
                    .setError("Login Unsuccessful");
        }
    }


}
