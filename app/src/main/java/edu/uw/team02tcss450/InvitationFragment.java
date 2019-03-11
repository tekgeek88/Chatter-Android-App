package edu.uw.team02tcss450;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.uw.team02tcss450.tasks.AsyncTaskFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvitationFragment extends Fragment {


    public InvitationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_invitation, container, false);

        Button b = v.findViewById(R.id.btn_send_email);
        b.setOnClickListener(this::handleSendEmail);

        //Include these 2 lines ONLY if need to use Toolbar from layout xml as Action Bar
        getActivity().setTitle("Send invite");

        return v;
    }

    private void handleSendEmail(View view) {
        HomeActivity homeActivity = (HomeActivity)getActivity();
        TextView tv = (TextView)getActivity().findViewById(R.id.editText_fragment_invitiation_email_address);
        String friendsEmail = tv.getText().toString();
        AsyncTaskFactory.sendInvitiationEmail(homeActivity, homeActivity.getmJwToken(), friendsEmail);
    }

}
