package edu.uw.team02tcss450;

import edu.uw.team02tcss450.model.Credentials;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener,
        VerificationFragment.OnVerificationFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            if (findViewById(R.id.main_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, new LoginFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onLoginSuccess(Credentials user_name_password, String jwt) {
//        Bundle args  = new Bundle();
//        args.putSerializable(getString(R.string.user_email), user_name_password.getEmail());
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.putExtras(args);
//        intent.putExtra(getString(R.string.keys_intent_jwt), jwt);
//
//        startActivity(intent);


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
    public void onRegisterSuccess(Credentials account_email) {
//        LoginFragment loginFragment = new LoginFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(getString(R.string.email_back_to_login_fragment),
//                user_name_password.getEmail());
//        args.putSerializable(getString(R.string.password_back_to_login_fragment),
//                user_name_password.getPassword());
//        loginFragment.setArguments(args);
        VerificationFragment verificationFragment = new VerificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.string_fragment_from_register_to_login_email),
                account_email.getEmail());
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
}

