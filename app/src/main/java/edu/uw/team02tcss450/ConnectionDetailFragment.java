package edu.uw.team02tcss450;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionDetailFragment extends Fragment {

    private  OnIndividualConnectionListener mListener;
    private String mButtonAction;



    public ConnectionDetailFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_connection_detail, container, false);
        mButtonAction = getArguments().getString("action");
        Button b = v.findViewById(R.id.button_connection_detail_action);
        Button bChat = v.findViewById(R.id.button_connection_detail_chat);


        if(mButtonAction.equals("ADDED")) {
            b.setText("Remove");


            b.setOnClickListener(view -> mListener.OnIndividualConnectionRemoveInteraction(getArguments().getString("username")));
            bChat.setOnClickListener(view -> mListener.OnIndividualConnectionChatInteraction(getArguments().getString("username")));
        }else{
            b.setText("Accept");
            b.setOnClickListener(view -> mListener.OnIndividualConnectionAddInteraction(getArguments().getString("username")));

            bChat.setEnabled(false);
        }

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            String firstName = getArguments().getString("firstname");
            String lastName = getArguments().getString("lastname");
            String userName = getArguments().getString("username");




            update(firstName,lastName,userName);
        }
    }
    public void update(String firstName,String lastName, String userName) {
        TextView t = getActivity().findViewById(R.id.textview_connection_detail_fName);
        t.setText(firstName);
        TextView d = getActivity().findViewById(R.id.textview_connection_detail_lName);
        d.setText(lastName);
        TextView ts = getActivity().findViewById(R.id.textview_connection_detail_userName);
        ts.setText(userName);


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnIndividualConnectionListener) {
            mListener = (OnIndividualConnectionListener) context;
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

    public interface OnIndividualConnectionListener{
        void OnIndividualConnectionAddInteraction(String url);
        void OnIndividualConnectionRemoveInteraction(String url);
        void OnIndividualConnectionChatInteraction(String url);
    }

}
