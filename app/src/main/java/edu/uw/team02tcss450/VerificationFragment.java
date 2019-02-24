package edu.uw.team02tcss450;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerificationFragment extends Fragment implements View.OnClickListener {

    private OnVerificationFragmentInteractionListener mListener;


    public VerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_verification, container, false);
        Button go_back_login_btn = (Button) v.findViewById(R.id.btn_fragment_verification_btn_bo_back);
        go_back_login_btn.setOnClickListener(this);
        return v;
    }

    public interface OnVerificationFragmentInteractionListener extends
            WaitFragment.OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGoBackLoginClicked();

    }


    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            String email = getArguments().getString(getString(R.string.
                    string_fragment_from_register_to_login_email));
            String header = getArguments().getString("fragment_header");
            String body = getArguments().getString("fragment_body");
            updateContentEmail(email, header, body);
        }
    }

    public void updateContentEmail(String email, String header, String body) {
        TextView hd = getActivity().findViewById(R.id.textview_fragment_verification_registration_successful);
        hd.setText(header);
        TextView bd = getActivity().findViewById(R.id.textview_fragment_verification_first_phrase);
        bd.setText(body);
        TextView tv = getActivity().findViewById(R.id.textview_fragment_verification_email);
        tv.setText(email);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVerificationFragmentInteractionListener) {
            mListener = (OnVerificationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View view) {

        if (mListener != null) {
            switch (view.getId()) {
                case R.id.btn_fragment_verification_btn_bo_back:

                    mListener.onGoBackLoginClicked();

                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }

}
