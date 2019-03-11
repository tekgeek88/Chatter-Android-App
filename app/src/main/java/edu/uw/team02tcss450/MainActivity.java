package edu.uw.team02tcss450;

import edu.uw.team02tcss450.model.Credentials;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        ResetPasswordFragment.OnResetPasswordFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener,
        VerificationFragment.OnVerificationFragmentInteractionListener {

    private boolean mLoadFromChatNotification = false;
    private boolean mLoadFromConnRequest = false;
    private boolean mLoadFromConvoRequest = false;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String type;
        // Check for notifications from pushy
        if (getIntent().getExtras() != null) {
            args = new Bundle();
            if (getIntent().getExtras().containsKey("type")) {
                type = getIntent().getExtras().getString("type");
                args.putString("type", type);
                mLoadFromChatNotification = "msg".equals(type);
                mLoadFromConnRequest = "conn".equals(type);
                mLoadFromConvoRequest = "convo".equals(type);
                if (mLoadFromChatNotification) {
                    args.putString("sender", getIntent().getStringExtra("sender"));
                    args.putString("message", getIntent().getStringExtra("message"));
                    args.putString("chat_id", getIntent().getStringExtra("chat_id"));
                } else if (mLoadFromConvoRequest) {
                    args.putString("fromFirstname", getIntent().getStringExtra("fromFirstname"));
                    args.putString("fromUsername", getIntent().getStringExtra("fromUsername"));
                    args.putString("message", getIntent().getStringExtra("message"));
                } else if (mLoadFromConnRequest) {

                }

            } 
        }



        if (savedInstanceState == null) {
            if (findViewById(R.id.main_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, new LoginFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onLoginSuccess(Credentials credentials, String jwt) {
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra(getString(R.string.keys_intent_credentials), (Serializable) credentials);
        i.putExtra(getString(R.string.keys_intent_jwt), jwt);
        i.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        if (null != args) {
            i.putExtras(args);
        }
        startActivity(i);
        //End this Activity and remove it from the Activity back stack.
        finish();


    }

    @Override
    public void onRegisterClicked() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, registerFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void OnForgotPasswordClicked() {

        ResetPasswordFragment resetFragment = new ResetPasswordFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, resetFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void onRegisterSuccess(Credentials account_email) {
        VerificationFragment verificationFragment = new VerificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.string_fragment_from_register_to_login_email),
                account_email.getEmail());
        args.putString("fragment_header", getString(R.string.string_fragment_verification_registration_successful));
        args.putString("fragment_body", getString(R.string.string_fragment_verification_first_phrase));

        verificationFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, verificationFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void onGoBackLoginClicked() {
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, loginFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }


    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    @Override
    public void onResetPasswordSuccess(Credentials account_email) {

        VerificationFragment verificationFragment = new VerificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.string_fragment_from_register_to_login_email),
                account_email.getEmail());
        args.putString("fragment_header", getString(R.string.string_fragment_reset_password_verification_reset_successful));
        args.putString("fragment_body", getString(R.string.string_fragment_reset_password_verification_first_phrase));

        verificationFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, verificationFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();


    }
}

