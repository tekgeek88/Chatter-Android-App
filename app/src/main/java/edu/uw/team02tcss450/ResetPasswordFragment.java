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
public class ResetPasswordFragment extends Fragment {

    private OnResetPasswordFragmentInteractionListener mListener;
    private Credentials mCredentials;


    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);

        Button reset_btn = (Button) v.findViewById(R.id.btn_fragment_reset_password_resetNow);
        reset_btn.setOnClickListener(this::resetPassword);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResetPasswordFragmentInteractionListener) {
            mListener = (OnResetPasswordFragmentInteractionListener) context;
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


    public interface OnResetPasswordFragmentInteractionListener extends
            WaitFragment.OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onResetPasswordSuccess(Credentials account_email);
    }

    private void resetPassword(final View theButton) {

        EditText emailEdit = getActivity().findViewById(R.id.edittext_fragment_reset_password_email);
        if (emailEdit.length() == 0) {
            emailEdit.setError("Field must not be empty.");
            return;
        }

        Credentials.Builder builder = new Credentials.Builder(emailEdit.getText().toString(),
                null);

        Credentials credentials = builder.build();

        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_resetPassword))
                .build();

        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        mCredentials = credentials;

        //instantiate and execute the AsyncTask.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleResetPasswordOnPre)
                .onPostExecute(this::handleResetPasswordOnPost)
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
    private void handleResetPasswordOnPre() {

        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleResetPasswordOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));

            if (success) {
                mListener.onResetPasswordSuccess(mCredentials);
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
