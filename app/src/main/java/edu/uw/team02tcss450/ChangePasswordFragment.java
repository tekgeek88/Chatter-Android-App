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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {

    private OnChangePasswordFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mEmail;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button register_btn = (Button) v.findViewById(R.id.btn_fragment_change_password_submit);
        register_btn.setOnClickListener(this::attemptPasswordChange);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            mEmail = getArguments().getString(getString(R.string.keys_intent_credentials));

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChangePasswordFragmentInteractionListener) {
            mListener = (OnChangePasswordFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChangePasswordFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnChangePasswordFragmentInteractionListener extends
            WaitFragment.OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChangePasswordSuccess(Credentials cr);
    }


    private void attemptPasswordChange(View view) {

        EditText passwordEdit = getActivity().findViewById(R.id.edittext_fragment_change_password_password);
        EditText retypeEdit = getActivity().findViewById(R.id.edittext_fragment_change_password_retype_password);


        if (passwordEdit.length() != 0 && retypeEdit.length() != 0) {

            if (checkIfSamePassword(passwordEdit, retypeEdit) && passwordEdit.length() >= 6) {
                doPasswordChange(passwordEdit);

            } else {
                passwordEdit.setError("Passwords do not match or too short");
                retypeEdit.setError("Passwords do not match or too short");
            }


        } else {
            passwordEdit.setError("Fields should not be empty");


        }


    }

    public boolean checkIfSamePassword(EditText password, EditText rePassword) {
        boolean bool = false;
        if (password.getText().length() == rePassword.getText().length()) {
            bool = true;
            for (int i = 0; i < password.getText().length(); i++) {
                if (password.getText().charAt(i) != rePassword.getText().charAt(i)) {
                    bool = false;
                    break;
                }


            }
        }
        return bool;
    }

    private void doPasswordChange(EditText passwordEdit) {

        Credentials.Builder builder = new Credentials.Builder(mEmail,
                null);
        builder.addNewPassword(passwordEdit.getText().toString());
        Credentials credentials = builder.build();

        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_changePassword))
                .build();

        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        mCredentials = credentials;

        //instantiate and execute the AsyncTask.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleChangePasswordOnPre)
                .onPostExecute(this::handleChangePasswordOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

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
    private void handleChangePasswordOnPre() {

        mListener.onWaitFragmentInteractionShow();
    }

    private void handleChangePasswordOnPost(String result) {

        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));

            if (success) {
                mListener.onChangePasswordSuccess(mCredentials);
                return;
            } else {
                // inform the user of the errors
                Map<String, TextView> fields = new HashMap<String, TextView>();

                fields.put("email", (TextView) getView().findViewById(R.id.edittext_fragment_reset_password_email));

                JSONArray errorsJSON = resultsJSON.getJSONArray("data");
                for (int i = 0; i < errorsJSON.length(); i++) {
                    JSONObject error = errorsJSON.getJSONObject(i);
                    String param = error.getString("param");
                    String msg = error.getString("msg");
                    fields.get(param).setError(msg);

                }
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


}
