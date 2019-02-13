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

    private Button go_back_login_btn;
    private View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_verification, container, false);
        go_back_login_btn = (Button) v.findViewById(R.id.btn_fragment_verification_btn_bo_back);
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
            updateContentEmail(email);
        }
    }

    public void updateContentEmail(String email) {
        TextView tv = getActivity().findViewById(R.id.edittext_fragment_verification_email);
        tv.setText(email);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVerificationFragmentInteractionListener) {
            mListener = (OnVerificationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            switch (view.getId()) {
                case R.id.btn_fragment_verification_btn_bo_back:
                    mListener.onGoBackLoginClicked();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }

}
