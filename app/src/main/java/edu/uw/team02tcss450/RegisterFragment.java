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

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnRegisterFragmentInteractionListener} interface
 * to handle interaction events.
 * @author Zebin Zhou
 * @version 13 January 2019
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {



    private OnRegisterFragmentInteractionListener mListener;

    private Credentials mCredentials;


    public RegisterFragment() {
        // Required empty public constructor
    }


    private View v;
    private EditText email_text;
    private EditText password_text;
    private EditText re_password_text;
    private Button register_btn;

    private EditText first_name_text;
    private EditText last_name_text;
    private EditText nickname_text;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_register, container, false);
        email_text = (EditText) v.findViewById(R.id.edittext_fragment_register_email);
        password_text = (EditText) v.findViewById(R.id.edittext_fragment_register_password);
        re_password_text = (EditText) v.findViewById(R.id.edittext_fragment_register_retype_password);
        first_name_text = (EditText) v.findViewById(R.id.edittext_fragment_register_firstname);
        last_name_text = (EditText) v.findViewById(R.id.edittext_fragment_register_lastname);
        nickname_text = (EditText) v.findViewById(R.id.edittext_fragment_register_nickname);

        register_btn = (Button) v.findViewById(R.id.btn_fragment_register_register);
        register_btn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnRegisterFragmentInteractionListener extends
            WaitFragment.OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRegisterSuccess(Credentials account_email);
    }

    @Override
    public void onClick(View view) {
//        String email_string = email_text.getText().toString();
//        String password_string = password_text.getText().toString();
//        String re_password_string = re_password_text.getText().toString();

        if(mListener != null) {
            switch (view.getId()) {
                case R.id.btn_fragment_register_register:
                    attemptRegister(register_btn);
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

    public boolean password_length_validation(String password_string) {
        if(password_string.length() > 5) {
            return true;
        } else {
            return false;
        }
    }

    public boolean password_match_validation(String password_string, String re_password_string) {
        if(password_string.equals(re_password_string)) {
            return true;
        } else {
            return false;
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
    private void handleRegisterOnPre() {

        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));

            if (success) {
                mListener.onRegisterSuccess(mCredentials);
                return;
            } else {
                // Login was unsuccessful. Don’t switch fragments and
                // inform the user
                ((TextView) getView().findViewById(R.id.edittext_fragment_register_email))
                        .setError("Register Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.edittext_fragment_register_email))
                    .setError("Register Unsuccessful");
        }
    }

    private void attemptRegister(final View theButton) {

        EditText emailEdit = getActivity().findViewById(R.id.edittext_fragment_register_email);
        EditText passwordEdit = getActivity().findViewById(R.id.edittext_fragment_register_password);
        EditText nicknameEdit = getActivity().findViewById(R.id.edittext_fragment_register_nickname);

        EditText firstNameEdit = getActivity().findViewById(R.id.edittext_fragment_register_firstname);
        EditText lastNameEdit = getActivity().findViewById(R.id.edittext_fragment_register_lastname);
        EditText rePasswordEdit = getActivity().findViewById
                (R.id.edittext_fragment_register_retype_password);

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
        } else if (passwordEdit.getText().length() < 6) {
            hasError = true;
            passwordEdit.setError("Password must have at least 6 characters.");
        } else if (!passwordEdit.getText().toString().equals(rePasswordEdit.getText().toString())) {
            hasError = true;
            passwordEdit.setError("Password and retype password not match.");
        }
        if(rePasswordEdit.getText().length() == 0) {
            hasError = true;
            rePasswordEdit.setError("Field must not be empty.");
        } else if (!rePasswordEdit.getText().toString().equals(passwordEdit.getText().toString())) {
            hasError = true;
            rePasswordEdit.setError("Password and retype password not match.");
        }
        if(nicknameEdit.getText().length() == 0) {
            hasError = true;
            nicknameEdit.setError("Field must not be empty.");
        }
        if(firstNameEdit.getText().length() == 0) {
            hasError = true;
            firstNameEdit.setError("Field must not be empty.");
        }
        if(lastNameEdit.getText().length() == 0) {
            hasError = true;
            lastNameEdit.setError("Field must not be empty.");
        }

        if (!hasError) {
            Credentials.Builder builder = new Credentials.Builder(emailEdit.getText().toString(),
                    passwordEdit.getText().toString());
            builder.addFirstName(firstNameEdit.getText().toString());
            builder.addLastName(lastNameEdit.getText().toString());
            builder.addUsername(nicknameEdit.getText().toString());
            Credentials credentials = builder.build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_register))
                    .build();

            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();

            mCredentials = credentials;

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleRegisterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }
}
