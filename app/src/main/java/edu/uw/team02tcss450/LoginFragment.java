package edu.uw.team02tcss450;


import android.content.Context;
import edu.uw.team02tcss450.model.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.team02tcss450.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * @author Zebin Zhou
 * @version 13 January 2019
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private OnLoginFragmentInteractionListener mListener;

    private Credentials mCredentials;


    public LoginFragment() {
        // Required empty public constructor
    }

    private EditText email_text;
    private EditText password_text;
    private Button register_btn;
    private Button sign_in_btn;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false);
        email_text = (EditText) v.findViewById(R.id.edittext_fragment_login_email);
        password_text = (EditText) v.findViewById(R.id.edittext_fragment_login_password);
        register_btn = (Button) v.findViewById(R.id.btn_fragment_login_register);
        register_btn.setOnClickListener(this);
        sign_in_btn = (Button) v.findViewById(R.id.btn_fragment_login_signin);
        sign_in_btn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        System.exit(0);
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
    public interface OnLoginFragmentInteractionListener extends
            WaitFragment.OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLoginSuccess(Credentials user_name_password, String jwt);
        void onRegisterClicked();
    }

    @Override
    public void onClick(View view) {

        String email_string = email_text.getText().toString();
        String password_string = password_text.getText().toString();
        boolean is_email_contains_at = email_validation(email_string);

        if(mListener != null) {
            switch (view.getId()) {
                case R.id.btn_fragment_login_signin:
                    attemptLogin(sign_in_btn);
                    break;
                case R.id.btn_fragment_login_register:
                    mListener.onRegisterClicked();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }

    /*
     * Methods to check whether email contains @.
     *
     * @param String Email in String type.
     * @return true if email contains @, false if email doesn't contain @.
     */
    public boolean email_validation(String email_string) {
        for(int i = 0; i < email_string.length(); i++) {
            if(email_string.charAt(i) == '@') {
                return true;
            }
        }
        return false;
    }

    public void updateContentEmail(String email) {
        TextView tv = getActivity().findViewById(R.id.edittext_fragment_login_email);
        tv.setText(email);
    }

    public void updateContentPassword(String email) {
        TextView tv = getActivity().findViewById(R.id.edittext_fragment_login_password);
        tv.setText(email);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            String email = getArguments().getString(getString(R.string.
                    email_back_to_login_fragment));
            updateContentEmail(email);
            String password = getArguments().getString(getString(R.string.
                    password_back_to_login_fragment));
            updateContentPassword(password);
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR",  result);
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
                mListener.onLoginSuccess(mCredentials,
                        resultsJSON.getString(
                                getString(R.string.keys_json_login_jwt)));
                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
//                edit_login_email to login_email when in Step 48!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                ((TextView) getView().findViewById(R.id.edittext_fragment_login_email))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
//            edit_login_email to login_email when in Step 48!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            ((TextView) getView().findViewById(R.id.edittext_fragment_login_email))
                    .setError("Login Unsuccessful");
        }
    }

    private void attemptLogin(final View theButton) {
        EditText emailEdit = getActivity().findViewById(R.id.edittext_fragment_login_email);

        EditText passwordEdit = getActivity().findViewById(R.id.edittext_fragment_login_password);

        boolean hasError = false;
        if (emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        }  else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address.");
        }
        if (passwordEdit.getText().length() == 0) {
            hasError = true;
            passwordEdit.setError("Field must not be empty.");
        }

        if (!hasError) {
            Credentials credentials = new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_login))
                    .build();

            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();

            mCredentials = credentials;

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleLoginOnPre)
                    .onPostExecute(this::handleLoginOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

}
